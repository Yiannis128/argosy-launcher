package com.nendo.argosy.data.storage

import android.content.Context
import android.os.Environment
import android.os.StatFs
import com.nendo.argosy.core.storage.StorageVolumeType
import com.nendo.argosy.data.cache.ImageCacheManager
import com.nendo.argosy.data.local.dao.GameDao
import com.nendo.argosy.data.local.dao.GameDiscDao
import com.nendo.argosy.data.local.dao.GameFileDao
import com.nendo.argosy.data.local.dao.PlatformDao
import com.nendo.argosy.data.music.MusicDirectoryManager
import com.nendo.argosy.data.preferences.StoragePreferencesRepository
import com.nendo.argosy.util.AppPaths
import com.nendo.argosy.util.SafeCoroutineScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

private const val DATABASE_NAME = "alauncher.db"
private const val TMP_SUFFIX = ".tmp"
private const val WALK_PROGRESS_INTERVAL = 512
private const val GAMES_PROGRESS_INTERVAL = 128

private val NIO_CATEGORIES = setOf(
    StorageCategory.MUSIC,
    StorageCategory.IMAGE_CACHE,
    StorageCategory.STEAM
)

/** Computes per-category, per-volume disk usage attribution; walks run on an app-lifetime IO scope. */
@Singleton
class StorageAttributionRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val volumeDetector: StorageVolumeDetector,
    private val snapshotStore: StorageSnapshotStore,
    private val storagePreferences: StoragePreferencesRepository,
    private val musicDirectoryManager: MusicDirectoryManager,
    private val imageCacheManager: ImageCacheManager,
    private val gameDao: GameDao,
    private val gameFileDao: GameFileDao,
    private val gameDiscDao: GameDiscDao,
    private val platformDao: PlatformDao
) {
    private val scope = SafeCoroutineScope(Dispatchers.IO, "StorageAttribution")
    private val refreshLock = Any()
    private var activeRefresh: Job? = null

    private val _snapshot = MutableStateFlow<StorageSnapshot?>(null)
    val snapshot: StateFlow<StorageSnapshot?> = _snapshot.asStateFlow()

    private val _walkProgress = MutableStateFlow<Map<StorageCategory, WalkState>>(emptyMap())
    val walkProgress: StateFlow<Map<StorageCategory, WalkState>> = _walkProgress.asStateFlow()

    private val _volumes = MutableStateFlow<List<StorageVolumeInfo>>(emptyList())
    val volumes: StateFlow<List<StorageVolumeInfo>> = _volumes.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _dirtyCategories = MutableStateFlow<Set<StorageCategory>>(emptySet())
    val dirtyCategories: StateFlow<Set<StorageCategory>> = _dirtyCategories.asStateFlow()

    init {
        scope.launch {
            snapshotStore.load()?.let { persisted ->
                _snapshot.update { current -> current ?: persisted }
            }
        }
    }

    fun markDirty(category: StorageCategory) {
        _dirtyCategories.update { it + category }
    }

    /** Single-flight: returns the active refresh Job unless [force], which cancels and restarts it. */
    fun refresh(force: Boolean = false): Job = synchronized(refreshLock) {
        val current = activeRefresh
        if (current != null && current.isActive) {
            if (!force) return current
            current.cancel()
        }
        val previous = activeRefresh
        scope.launch {
            previous?.join()
            runRefresh()
        }.also { activeRefresh = it }
    }

    private suspend fun runRefresh() {
        val ctx = currentCoroutineContext()
        _isRefreshing.value = true
        try {
            val volumes = detectVolumes()
            _volumes.value = volumes
            val internalKey = volumes.firstOrNull { it.type == StorageVolumeType.INTERNAL }?.key
                ?: volumes.firstOrNull()?.key
            _walkProgress.value = StorageCategory.entries.associateWith { WalkState.Pending }

            val musicDir = musicDirectoryManager.resolveMusicDir()
            val musicRoot = StoragePathUtils.canonicalize(musicDir.absolutePath)

            setWalkState(StorageCategory.GAMES, WalkState.Walking(0L, 0))
            try {
                val games = collectGames(ctx, volumes, internalKey, musicRoot)
                publishCategory(StorageCategory.GAMES, games.usage, games.perPlatform)
                setWalkState(StorageCategory.GAMES, WalkState.Complete)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                setWalkState(StorageCategory.GAMES, WalkState.Failed)
            }

            runCategory(StorageCategory.DATABASE) { databaseUsage(volumes, internalKey) }

            for ((category, roots) in resolveCategoryRoots(musicDir)) {
                runCategory(category) {
                    walkRoots(category, roots, volumes, internalKey, ctx, useNio = category in NIO_CATEGORIES)
                }
            }

            _snapshot.value?.let { snapshotStore.save(it) }
            _dirtyCategories.value = emptySet()
        } finally {
            _isRefreshing.value = false
        }
    }

    private suspend fun runCategory(category: StorageCategory, block: suspend () -> CategoryUsage) {
        setWalkState(category, WalkState.Walking(0L, 0))
        try {
            publishCategory(category, block())
            setWalkState(category, WalkState.Complete)
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            setWalkState(category, WalkState.Failed)
        }
    }

    private fun publishCategory(
        category: StorageCategory,
        usage: CategoryUsage,
        perPlatform: List<PlatformUsage>? = null
    ) {
        _snapshot.update { current ->
            val base = current ?: StorageSnapshot(0L, emptyMap(), emptyList())
            base.copy(
                computedAt = System.currentTimeMillis(),
                categories = base.categories + (category to usage),
                gamesPerPlatform = perPlatform ?: base.gamesPerPlatform
            )
        }
    }

    private fun setWalkState(category: StorageCategory, state: WalkState) {
        _walkProgress.update { it + (category to state) }
    }

    private fun detectVolumes(): List<StorageVolumeInfo> {
        val seenPaths = HashSet<String>()
        val seenFingerprints = HashSet<String>()
        val result = mutableListOf<StorageVolumeInfo>()
        for (volume in volumeDetector.detectStorageVolumes()) {
            if (volume.totalBytes <= 0L) continue
            val canonical = StoragePathUtils.canonicalize(volume.path).trimEnd('/')
            if (canonical.isEmpty()) continue
            if (!seenPaths.add(canonical)) continue
            val fingerprint = statFingerprint(canonical) ?: canonical
            if (!seenFingerprints.add(fingerprint)) continue
            result.add(
                StorageVolumeInfo(
                    key = canonical,
                    displayName = volume.displayName,
                    type = volume.type,
                    totalBytes = volume.totalBytes,
                    availableBytes = volume.availableBytes
                )
            )
        }
        return result
    }

    private fun statFingerprint(path: String): String? = try {
        val stat = StatFs(path)
        "${stat.blockSizeLong}:${stat.blockCountLong}:${stat.availableBlocksLong}"
    } catch (_: Exception) {
        null
    }

    private fun volumeKeyFor(
        canonicalPath: String,
        volumes: List<StorageVolumeInfo>,
        internalKey: String?
    ): String? {
        val match = volumes
            .filter { canonicalPath == it.key || canonicalPath.startsWith("${it.key}/") }
            .maxByOrNull { it.key.length }
        return match?.key ?: internalKey
    }

    private class UsageAccumulator {
        var bytes = 0L
        var files = 0
        val perVolume = HashMap<String, Long>()

        fun add(entryBytes: Long, entryFiles: Int, volumeKey: String?) {
            bytes += entryBytes
            files += entryFiles
            if (volumeKey != null && entryBytes > 0) perVolume.merge(volumeKey, entryBytes, Long::plus)
        }
    }

    private data class GamesResult(val usage: CategoryUsage, val perPlatform: List<PlatformUsage>)

    private suspend fun collectGames(
        ctx: CoroutineContext,
        volumes: List<StorageVolumeInfo>,
        internalKey: String?,
        musicRoot: String
    ): GamesResult {
        val gameRows = gameDao.getGamesWithLocalPathInfo()
        val fileRows = gameFileDao.getAllLocalPathsWithPlatform()
        val discRows = gameDiscDao.getAllLocalPathsWithPlatform()
        val platforms = platformDao.getAllPlatformsOrdered().associateBy { it.id }
        val downloadedCounts = gameRows.groupingBy { it.platformId }.eachCount()

        val rows = ArrayList<Pair<Long, String>>(gameRows.size + fileRows.size + discRows.size)
        gameRows.forEach { row -> row.localPath?.let { rows.add(row.platformId to it) } }
        fileRows.forEach { rows.add(it.platformId to it.localPath) }
        discRows.forEach { rows.add(it.platformId to it.localPath) }

        val seen = HashSet<String>(rows.size * 2)
        val total = UsageAccumulator()
        val perPlatform = HashMap<Long, UsageAccumulator>()
        var processed = 0

        for ((platformId, rawPath) in rows) {
            ctx.ensureActive()
            val canonical = StoragePathUtils.canonicalize(rawPath)
            if (!seen.add(canonical)) continue
            if (canonical.endsWith(TMP_SUFFIX)) continue
            if (canonical == musicRoot || canonical.startsWith("$musicRoot/")) continue
            val entry = File(canonical)
            val measured = when {
                entry.isDirectory -> topDownWalk(entry, ctx, excludeTmp = true)
                entry.isFile -> entry.length() to 1
                else -> null
            } ?: continue
            val volumeKey = volumeKeyFor(canonical, volumes, internalKey)
            total.add(measured.first, measured.second, volumeKey)
            perPlatform.getOrPut(platformId) { UsageAccumulator() }
                .add(measured.first, measured.second, volumeKey)
            processed += 1
            if (processed % GAMES_PROGRESS_INTERVAL == 0) {
                setWalkState(StorageCategory.GAMES, WalkState.Walking(total.bytes, total.files))
            }
        }

        val perPlatformUsages = (perPlatform.keys + downloadedCounts.keys).map { platformId ->
            val meta = platforms[platformId]
            val acc = perPlatform[platformId]
            PlatformUsage(
                platformId = platformId,
                name = meta?.name ?: "Unknown",
                sortOrder = meta?.sortOrder ?: Int.MAX_VALUE,
                downloadedCount = downloadedCounts[platformId] ?: 0,
                bytes = acc?.bytes ?: 0L,
                perVolume = acc?.perVolume ?: emptyMap()
            )
        }.sortedBy { it.sortOrder }

        return GamesResult(
            usage = CategoryUsage(total.bytes, total.files, total.perVolume),
            perPlatform = perPlatformUsages
        )
    }

    private suspend fun resolveCategoryRoots(musicDir: File): Map<StorageCategory, List<File>> {
        val prefs = storagePreferences.preferences.first()
        val filesDir = context.filesDir
        val cacheDir = context.cacheDir
        val externalFilesDir = context.getExternalFilesDir(null)
        val customBios = prefs.customBiosPath?.let { resolveCustomBiosDir(it) }
        return linkedMapOf(
            StorageCategory.SAVE_STATE_CACHE to listOf(
                AppPaths.saveCacheDir(filesDir),
                AppPaths.stateCacheDir(filesDir)
            ),
            StorageCategory.ROM_EXTRACTION to listOf(AppPaths.romCacheDir(filesDir)),
            StorageCategory.SFX_CACHE to listOf(File(cacheDir, "sfx")),
            StorageCategory.BIOS to listOfNotNull(
                File(filesDir, "bios"),
                AppPaths.libretroSystemDir(filesDir),
                customBios
            ),
            StorageCategory.CORES_SYSTEM to listOf(
                File(filesDir, "libretro/cores"),
                File(filesDir, "libretro/core_history"),
                File(filesDir, "libretro/compat_cores")
            ),
            StorageCategory.SHADERS_CATALOG to listOfNotNull(
                externalFilesDir?.let { File(it, "shaders/catalog") }
            ),
            StorageCategory.SHADERS_CUSTOM to listOfNotNull(
                externalFilesDir?.let { File(it, "shaders/custom") }
            ),
            StorageCategory.FRAMES to listOfNotNull(externalFilesDir?.let { File(it, "frames") }),
            StorageCategory.FONTS to listOf(File(filesDir, "fonts")),
            StorageCategory.EMULATOR_APKS to listOf(File(cacheDir, "emulator_apks")),
            StorageCategory.MISC_DOWNLOADS to listOf(
                File(cacheDir, "presence_covers"),
                File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "drivers"
                )
            ),
            StorageCategory.MUSIC to listOf(musicDir),
            StorageCategory.IMAGE_CACHE to listOf(
                File(imageCacheManager.getCurrentCachePath()),
                File(filesDir, "steam")
            ),
            StorageCategory.STEAM to listOf(
                AppPaths.steamStagingRoot(filesDir),
                File(filesDir, "steam_downloads")
            )
        )
    }

    private fun resolveCustomBiosDir(basePath: String): File {
        val base = File(basePath)
        return if (base.name.equals("bios", ignoreCase = true)) base else File(base, "bios")
    }

    private fun walkRoots(
        category: StorageCategory,
        roots: List<File>,
        volumes: List<StorageVolumeInfo>,
        internalKey: String?,
        ctx: CoroutineContext,
        useNio: Boolean
    ): CategoryUsage {
        val total = UsageAccumulator()
        val seenRoots = HashSet<String>()
        for (root in roots) {
            val canonicalRoot = StoragePathUtils.canonicalize(root.absolutePath)
            if (!seenRoots.add(canonicalRoot)) continue
            if (!root.exists()) continue
            val volumeKey = volumeKeyFor(canonicalRoot, volumes, internalKey)
            val measured = if (useNio) {
                nioWalk(root, ctx) { bytes, files ->
                    setWalkState(category, WalkState.Walking(total.bytes + bytes, total.files + files))
                }
            } else {
                topDownWalk(root, ctx)
            }
            total.add(measured.first, measured.second, volumeKey)
            setWalkState(category, WalkState.Walking(total.bytes, total.files))
        }
        return CategoryUsage(total.bytes, total.files, total.perVolume)
    }

    private fun nioWalk(
        root: File,
        ctx: CoroutineContext,
        onProgress: (Long, Int) -> Unit
    ): Pair<Long, Int> {
        var bytes = 0L
        var files = 0
        Files.walkFileTree(root.toPath(), object : SimpleFileVisitor<Path>() {
            override fun preVisitDirectory(dir: Path?, attrs: BasicFileAttributes?): FileVisitResult {
                ctx.ensureActive()
                return FileVisitResult.CONTINUE
            }

            override fun visitFile(file: Path?, attrs: BasicFileAttributes?): FileVisitResult {
                if (attrs != null && attrs.isRegularFile) {
                    bytes += attrs.size()
                    files += 1
                    if (files % WALK_PROGRESS_INTERVAL == 0) onProgress(bytes, files)
                }
                return FileVisitResult.CONTINUE
            }

            override fun visitFileFailed(file: Path?, exc: IOException?): FileVisitResult =
                FileVisitResult.CONTINUE
        })
        return bytes to files
    }

    private fun topDownWalk(
        root: File,
        ctx: CoroutineContext,
        excludeTmp: Boolean = false
    ): Pair<Long, Int> {
        var bytes = 0L
        var files = 0
        root.walkTopDown()
            .onEnter {
                ctx.ensureActive()
                true
            }
            .forEach { entry ->
                if (entry.isFile && !(excludeTmp && entry.name.endsWith(TMP_SUFFIX))) {
                    bytes += entry.length()
                    files += 1
                }
            }
        return bytes to files
    }

    private fun databaseUsage(volumes: List<StorageVolumeInfo>, internalKey: String?): CategoryUsage {
        val db = context.getDatabasePath(DATABASE_NAME)
        val parts = listOf(db, File(db.parentFile, "${db.name}-wal"), File(db.parentFile, "${db.name}-shm"))
        var bytes = 0L
        var count = 0
        parts.forEach { part ->
            if (part.isFile) {
                bytes += part.length()
                count += 1
            }
        }
        val volumeKey = volumeKeyFor(StoragePathUtils.canonicalize(db.absolutePath), volumes, internalKey)
        val perVolume = if (volumeKey != null && bytes > 0) mapOf(volumeKey to bytes) else emptyMap()
        return CategoryUsage(bytes, count, perVolume)
    }
}
