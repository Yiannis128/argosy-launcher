package com.nendo.argosy.data.steam

import android.content.Context
import android.util.Log
import com.nendo.argosy.data.local.dao.GameDao
import com.nendo.argosy.data.model.GameSource
import com.nendo.argosy.data.preferences.UserPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import `in`.dragonbra.javasteam.steam.cdn.Client as CDNClient
import `in`.dragonbra.javasteam.steam.contentdownloader.ContentDownloader
import `in`.dragonbra.javasteam.steam.handlers.steamapps.PICSRequest
import `in`.dragonbra.javasteam.steam.handlers.steamapps.SteamApps
import `in`.dragonbra.javasteam.steam.handlers.steamapps.callback.DepotKeyCallback
import `in`.dragonbra.javasteam.steam.handlers.steamapps.callback.PICSProductInfoCallback
import `in`.dragonbra.javasteam.steam.handlers.steamcontent.SteamContent
import `in`.dragonbra.javasteam.steam.steamclient.SteamClient
import `in`.dragonbra.javasteam.steam.steamclient.callbackmgr.CallbackManager
import `in`.dragonbra.javasteam.types.KeyValue
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import java.io.Closeable
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "SteamContentManager"
private const val STEAM_PLATFORM_DIR = "steam"
private const val GN_PACKAGE = "app.gamenative"
private const val WINDOWS_OS = "windows"
private const val ARCH_64 = "64"
private const val MAX_PARALLEL_DOWNLOADS = 4
private const val MAX_PARALLEL_DEPOTS = 4

sealed class SteamDownloadState {
    data object Idle : SteamDownloadState()
    data class Preparing(val appId: Long, val gameName: String) : SteamDownloadState()
    data class Downloading(
        val appId: Long,
        val gameName: String,
        val progress: Float,
        val currentDepot: Int,
        val totalDepots: Int
    ) : SteamDownloadState()
    data class Moving(val appId: Long, val gameName: String) : SteamDownloadState()
    data class Completed(val appId: Long, val gameName: String, val installPath: String) : SteamDownloadState()
    data class Failed(val appId: Long, val gameName: String, val error: String) : SteamDownloadState()
    data class Paused(val appId: Long, val gameName: String, val progress: Float, val needsVerification: Boolean = false) : SteamDownloadState()
}

data class DepotInfo(
    val depotId: Int,
    val manifestId: Long,
    val name: String,
    val os: String?,
    val arch: String?,
    val size: Long
)

data class SteamDownloadProgress(
    val appId: Long,
    val gameName: String,
    val coverPath: String?,
    val progress: Float,
    val totalBytes: Long,
    val bytesDownloaded: Long,
    val state: SteamDownloadState
) {
    val progressPercent: Int get() = (progress * 100).toInt()
}

data class QueuedSteamDownload(
    val appId: Long,
    val gameName: String,
    val coverPath: String?,
    val appInfo: KeyValue?
)

@Singleton
class SteamContentManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gameDao: GameDao,
    private val preferencesRepository: UserPreferencesRepository,
    private val steamAuthManager: SteamAuthManager,
    private val androidDataAccessor: com.nendo.argosy.data.storage.AndroidDataAccessor
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var steamClient: SteamClient? = null
    private var steamApps: SteamApps? = null
    private var steamContent: SteamContent? = null
    private var callbackManager: CallbackManager? = null
    private var contentDownloader: ContentDownloader? = null
    private var picsSubscription: Closeable? = null

    private val _downloadState = MutableStateFlow<SteamDownloadState>(SteamDownloadState.Idle)
    val downloadState: StateFlow<SteamDownloadState> = _downloadState.asStateFlow()

    private val _activeDownload = MutableStateFlow<SteamDownloadProgress?>(null)
    val activeDownload: StateFlow<SteamDownloadProgress?> = _activeDownload.asStateFlow()

    private val _downloadQueue = MutableStateFlow<List<QueuedSteamDownload>>(emptyList())
    val downloadQueue: StateFlow<List<QueuedSteamDownload>> = _downloadQueue.asStateFlow()

    private val _completedDownloads = MutableStateFlow<List<SteamDownloadProgress>>(emptyList())
    val completedDownloads: StateFlow<List<SteamDownloadProgress>> = _completedDownloads.asStateFlow()

    private var currentDownloadJob: kotlinx.coroutines.Job? = null
    private val activeDepotDeferreds = mutableListOf<Deferred<Boolean>>()
    private var isCancelled = false

    init {
        scope.launch {
            discoverLocalSteamGames()
            restorePausedDownloads()
        }
    }

    fun initialize(client: SteamClient, apps: SteamApps, cm: CallbackManager) {
        steamClient = client
        steamApps = apps
        steamContent = client.getHandler(SteamContent::class.java)
        callbackManager = cm
        contentDownloader = ContentDownloader(client)
        Log.d(TAG, "SteamContentManager initialized with ContentDownloader")
    }

    suspend fun restorePausedDownloads() = withContext(Dispatchers.IO) {
        // Scan GN path and fallback path for .download_in_progress markers
        val candidates = mutableListOf<File>()

        // Check GN path on all volumes
        val volumes = mutableListOf(android.os.Environment.getExternalStorageDirectory().absolutePath)
        try {
            File("/storage").listFiles()?.forEach { vol ->
                if (vol.isDirectory && vol.name != "emulated" && vol.name != "self") {
                    volumes.add(vol.absolutePath)
                }
            }
        } catch (_: Exception) {}

        for (root in volumes) {
            val gnCommonPath = "$root/Android/data/$GN_PACKAGE/files/Steam/steamapps/common"
            // Try raw path first (SD card), then alt-access (internal)
            val commonDir = File(gnCommonPath).takeIf { it.exists() }
                ?: androidDataAccessor.getFile(gnCommonPath).takeIf { it.exists() }
            commonDir?.listFiles()?.forEach { gameDir ->
                if (File(gameDir, ".download_in_progress").exists()) {
                    candidates.add(gameDir)
                }
            }
        }

        // Check fallback storage paths
        val prefs = preferencesRepository.userPreferences.first()
        val fallbackPaths = listOfNotNull(
            prefs.romStoragePath,
            context.getExternalFilesDir(null)?.absolutePath,
            context.filesDir.absolutePath
        )
        for (base in fallbackPaths) {
            val steamDir = File(base, "steam")
            if (steamDir.exists()) {
                steamDir.listFiles()?.forEach { appDir ->
                    if (File(appDir, ".download_in_progress").exists()) {
                        candidates.add(appDir)
                    }
                }
            }
        }

        val restoredDownloads = mutableListOf<SteamDownloadProgress>()

        for (dir in candidates) {
            val size = getDirectorySize(dir)
            val dirAppId = dir.name.toLongOrNull()
            val game = gameDao.getAllWithSteamAppId().find {
                it.localPath == dir.absolutePath ||
                    sanitizeGameName(it.title) == dir.name ||
                    (dirAppId != null && it.steamAppId == dirAppId)
            }
            val gameName = game?.title ?: dir.name
            val appId = game?.steamAppId ?: dirAppId ?: continue

            Log.d(TAG, "Restored paused download: $gameName (${size / 1024 / 1024}MB)")

            val pausedState = SteamDownloadState.Paused(appId, gameName, 0f, needsVerification = true)
            restoredDownloads.add(SteamDownloadProgress(
                appId = appId,
                gameName = gameName,
                coverPath = game?.coverPath,
                progress = 0f,
                totalBytes = 0L,
                bytesDownloaded = size,
                state = pausedState
            ))
        }

        if (restoredDownloads.isNotEmpty()) {
            // Set the largest as the active paused download
            val primary = restoredDownloads.maxBy { it.bytesDownloaded }
            _downloadState.value = primary.state
            _activeDownload.value = primary

            // Queue the rest
            val rest = restoredDownloads.filter { it.appId != primary.appId }
            if (rest.isNotEmpty()) {
                _downloadQueue.value = _downloadQueue.value + rest.map { dl ->
                    QueuedSteamDownload(dl.appId, dl.gameName, dl.coverPath, null)
                }
                Log.d(TAG, "Queued ${rest.size} additional paused downloads")
            }
        }
    }

    fun isConnected(): Boolean {
        val hasApps = steamApps != null
        val hasCm = callbackManager != null
        val loggedIn = steamAuthManager.isLoggedIn.value
        Log.d(TAG, "isConnected check: hasApps=$hasApps, hasCm=$hasCm, loggedIn=$loggedIn")
        return hasApps && hasCm && loggedIn
    }

    fun onDisconnected() {
        Log.d(TAG, "Steam disconnected, clearing handlers")
        if (_downloadState.value !is SteamDownloadState.Paused) {
            cancelDownload()
        }
        steamClient = null
        steamApps = null
        callbackManager = null
        contentDownloader = null
    }

    fun getWindowsDepots(appInfo: KeyValue): List<DepotInfo> {
        val depots = mutableListOf<DepotInfo>()
        val depotsKv = appInfo["depots"] ?: return emptyList()

        for (child in depotsKv.children) {
            val depotIdStr = child.name ?: continue
            val depotId = depotIdStr.toIntOrNull() ?: continue

            // Skip special depots (branches, baselines, etc.)
            if (depotId < 1000) continue

            val config = child["config"]
            val oslist = config["oslist"]?.asString()?.lowercase()
            val osarch = config["osarch"]?.asString()

            // Skip non-Windows depots
            if (oslist != null && !oslist.contains(WINDOWS_OS)) {
                Log.d(TAG, "Skipping depot $depotId: OS=$oslist (not Windows)")
                continue
            }

            // Prefer 64-bit, but accept if no arch specified
            if (osarch != null && osarch != ARCH_64 && osarch != "32") {
                Log.d(TAG, "Skipping depot $depotId: arch=$osarch")
                continue
            }

            val manifests = child["manifests"]
            val publicManifest = manifests?.get("public")

            // Try different ways to get manifest ID
            var manifestId = publicManifest?.asString()?.toLongOrNull()
                ?: publicManifest?.value?.toString()?.toLongOrNull()
                ?: 0L

            // If still 0, try gid subfield
            if (manifestId == 0L) {
                manifestId = publicManifest?.get("gid")?.asString()?.toLongOrNull() ?: 0L
            }

            // Debug: log what we found
            if (manifestId == 0L && manifests != null) {
                Log.d(TAG, "Depot $depotId manifests children: ${manifests.children.map { "${it.name}=${it.value}" }}")
                if (publicManifest != null) {
                    Log.d(TAG, "Depot $depotId public manifest: value=${publicManifest.value}, children=${publicManifest.children.map { "${it.name}=${it.value}" }}")
                }
            }

            // Skip depots with no manifest (nothing to download)
            if (manifestId == 0L) {
                Log.d(TAG, "Skipping depot $depotId: no manifest")
                continue
            }

            val name = child["name"]?.asString() ?: "Depot $depotId"
            val maxSize = child["maxsize"]?.asString()?.toLongOrNull() ?: 0L

            depots.add(
                DepotInfo(
                    depotId = depotId,
                    manifestId = manifestId,
                    name = name,
                    os = oslist,
                    arch = osarch,
                    size = maxSize
                )
            )

            Log.d(TAG, "Found depot $depotId: $name (os=$oslist, arch=$osarch, manifest=$manifestId, size=${maxSize / 1024 / 1024}MB)")
        }

        Log.d(TAG, "Found ${depots.size} Windows depots total")
        return depots
    }

    suspend fun fetchDepotSizes(appId: Int, depots: List<DepotInfo>): Long = withContext(Dispatchers.IO) {
        val client = steamClient ?: return@withContext 0L
        val content = steamContent ?: return@withContext 0L
        val apps = steamApps ?: return@withContext 0L
        val cm = callbackManager ?: return@withContext 0L

        // Log session info for debugging
        val steamId = client.steamID
        Log.d(TAG, "Fetching depot sizes with SteamID: $steamId, isConnected: ${client.isConnected}")

        var totalSize = 0L
        val cdnClient = CDNClient(client)

        try {
            // Get CDN servers
            val servers = content.getServersForSteamPipe(null, null, scope).await()
            if (servers.isEmpty()) {
                Log.w(TAG, "No CDN servers available")
                return@withContext 0L
            }
            val server = servers.first()
            Log.d(TAG, "Using CDN server: ${server.host}")

            for (depot in depots) {
                try {
                    // Skip if no manifest ID (will be fetched by ContentDownloader)
                    if (depot.manifestId == 0L) {
                        Log.d(TAG, "Depot ${depot.depotId} has no manifest ID, skipping size fetch")
                        continue
                    }

                    // Get depot key first
                    Log.d(TAG, "Fetching depot key for depot ${depot.depotId}...")
                    val depotKey = getDepotKey(apps, cm, depot.depotId, appId)
                    if (depotKey == null) {
                        Log.w(TAG, "Failed to get depot key for ${depot.depotId}")
                        continue
                    }
                    Log.d(TAG, "Got depot key for depot ${depot.depotId} (${depotKey.size} bytes)")

                    // Get manifest request code BEFORE auth token (depotId comes first, then appId)
                    Log.d(TAG, "Fetching manifest request code for depot ${depot.depotId}, app $appId, manifest ${depot.manifestId}...")
                    val requestCode = content.getManifestRequestCode(
                        depot.depotId, appId, depot.manifestId,
                        "public", null, scope
                    ).await()
                    Log.d(TAG, "Got manifest request code: $requestCode (as Long=${requestCode.toLong()})")

                    if (requestCode.toLong() == 0L) {
                        Log.w(TAG, "Got zero manifest request code for depot ${depot.depotId} - may indicate access denied")
                        continue
                    }

                    // Get CDN auth token after request code
                    Log.d(TAG, "Fetching CDN auth token for depot ${depot.depotId} on ${server.host}...")
                    val authToken = content.getCDNAuthToken(appId, depot.depotId, server.host, scope).await()
                    Log.d(TAG, "Got CDN auth token: expires=${authToken.expiration}, hasToken=${authToken.token?.isNotEmpty()}")

                    // Download manifest with depot key and auth token
                    Log.d(TAG, "Downloading manifest from ${server.host} for depot ${depot.depotId}...")
                    val manifest = cdnClient.downloadManifestFuture(
                        depot.depotId, depot.manifestId, requestCode.toLong(), server, depotKey, null, authToken.token
                    ).get()

                    val depotSize = manifest.totalUncompressedSize
                    Log.d(TAG, "Depot ${depot.depotId} size: ${depotSize / 1024 / 1024}MB")
                    totalSize += depotSize
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to fetch manifest for depot ${depot.depotId}: ${e.message}")
                }
            }
        } finally {
            cdnClient.close()
        }

        Log.d(TAG, "Total size for app $appId: ${totalSize / 1024 / 1024}MB")
        totalSize
    }

    private suspend fun getDepotKey(apps: SteamApps, cm: CallbackManager, depotId: Int, appId: Int): ByteArray? {
        return withTimeoutOrNull(10_000L) {
            suspendCancellableCoroutine { continuation ->
                var subscription: Closeable? = null
                subscription = cm.subscribe(DepotKeyCallback::class.java) { callback ->
                    if (callback.depotID == depotId) {
                        subscription?.close()
                        if (callback.result == `in`.dragonbra.javasteam.enums.EResult.OK) {
                            continuation.resume(callback.depotKey)
                        } else {
                            continuation.resume(null)
                        }
                    }
                }

                continuation.invokeOnCancellation {
                    subscription.close()
                }

                apps.getDepotDecryptionKey(depotId, appId)
            }
        }
    }

    suspend fun ensureConnected(): Boolean {
        if (steamClient != null && steamApps != null && steamAuthManager.isLoggedIn.value) return true

        Log.d(TAG, "Steam not connected, starting service and waiting...")
        val intent = android.content.Intent(context, SteamService::class.java).apply {
            putExtra(SteamService.EXTRA_AUTO_CONNECT, true)
        }
        context.startService(intent)

        // Poll for connection with timeout
        val deadline = System.currentTimeMillis() + 30_000L
        while (System.currentTimeMillis() < deadline) {
            if (steamClient != null && steamApps != null && steamAuthManager.isLoggedIn.value) {
                Log.d(TAG, "Steam connected after waiting")
                return true
            }
            kotlinx.coroutines.delay(500)
        }
        Log.e(TAG, "Steam connection timeout (client=${steamClient != null}, apps=${steamApps != null}, loggedIn=${steamAuthManager.isLoggedIn.value})")
        return false
    }

    fun notifyConnected() {
        Log.d(TAG, "notifyConnected called")
    }

    suspend fun fetchAppInfo(appId: Int): KeyValue = withTimeout(30_000L) {
        if (steamApps == null || callbackManager == null) {
            if (!ensureConnected()) {
                throw IllegalStateException("Steam not connected")
            }
        }

        suspendCancellableCoroutine { continuation ->
            val apps = steamApps
            val cm = callbackManager

            if (apps == null || cm == null) {
                continuation.resumeWithException(IllegalStateException("Steam not connected"))
                return@suspendCancellableCoroutine
            }

            Log.d(TAG, "Fetching PICS info for app $appId")

            var subscription: Closeable? = null
            subscription = cm.subscribe(PICSProductInfoCallback::class.java) { callback ->
                val appInfo = callback.apps[appId]
                if (appInfo != null) {
                    Log.d(TAG, "Received PICS info for app $appId")
                    subscription?.close()
                    continuation.resume(appInfo.keyValues)
                }
            }

            continuation.invokeOnCancellation {
                subscription.close()
            }

            try {
                apps.picsGetProductInfo(PICSRequest(appId), null)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to request PICS info for app $appId", e)
                subscription.close()
                continuation.resumeWithException(e)
            }
        }
    }

    suspend fun downloadGame(
        appId: Long,
        gameName: String,
        coverPath: String?
    ) = withContext(Dispatchers.IO) {
        val appInfo = fetchAppInfo(appId.toInt())
        downloadGame(appId, gameName, appInfo, coverPath)
    }

    fun queueDownload(appId: Long, gameName: String, appInfo: KeyValue, coverPath: String?) {
        // Don't queue duplicates (but allow re-queuing paused downloads)
        val active = _activeDownload.value
        if (active?.appId == appId && active.state !is SteamDownloadState.Paused) {
            Log.d(TAG, "Game $appId already downloading")
            return
        }
        if (_downloadQueue.value.any { it.appId == appId }) {
            Log.d(TAG, "Game $appId already in queue")
            return
        }

        // Clear paused state if resuming
        if (active?.appId == appId && active.state is SteamDownloadState.Paused) {
            _activeDownload.value = null
            Log.d(TAG, "Clearing paused state for $appId to resume")
        }

        val queued = QueuedSteamDownload(appId, gameName, coverPath, appInfo)
        _downloadQueue.value = _downloadQueue.value + queued
        Log.d(TAG, "Queued Steam download: $gameName (queue size: ${_downloadQueue.value.size})")

        // Start if nothing is currently downloading
        if (currentDownloadJob?.isActive != true) {
            processNextInQueue()
        }
    }

    private fun processNextInQueue() {
        val queue = _downloadQueue.value
        if (queue.isEmpty()) {
            Log.d(TAG, "Download queue empty")
            return
        }

        val next = queue.first()
        _downloadQueue.value = queue.drop(1)
        Log.d(TAG, "Starting next queued download: ${next.gameName}")

        scope.launch {
            val appInfo = next.appInfo ?: try {
                fetchAppInfo(next.appId.toInt())
            } catch (e: Exception) {
                Log.e(TAG, "Failed to fetch app info for ${next.gameName}: ${e.message}")
                _downloadState.value = SteamDownloadState.Failed(next.appId, next.gameName, "Could not fetch app info")
                processNextInQueue()
                return@launch
            }
            startDownload(next.appId, next.gameName, appInfo, next.coverPath)
        }
    }

    suspend fun downloadGame(
        appId: Long,
        gameName: String,
        appInfo: KeyValue,
        coverPath: String?
    ) {
        // Use the queue system
        queueDownload(appId, gameName, appInfo, coverPath)
    }

    private suspend fun startDownload(
        appId: Long,
        gameName: String,
        appInfo: KeyValue,
        coverPath: String?
    ) {
        val client = steamClient
        if (client == null) {
            _downloadState.value = SteamDownloadState.Failed(appId, gameName, "Steam not connected")
            processNextInQueue()
            return
        }

        isCancelled = false

        currentDownloadJob = scope.launch {
            try {
                _downloadState.value = SteamDownloadState.Preparing(appId, gameName)

                // Download directly to final destination (GN path or fallback)
                // Staging dir stays on internal storage (temporary compressed chunks)
                val installDir = getInstallDir(appId)
                val stagingDir = File(context.filesDir, "steam_staging/${appId}")

                Log.d(TAG, "Download paths for $appId:")
                Log.d(TAG, "  Install (final): ${installDir.absolutePath}")
                Log.d(TAG, "  Staging (temp): ${stagingDir.absolutePath}")

                installDir.mkdirs()
                stagingDir.mkdirs()
                Log.d(TAG, "Created download directories: install=${installDir.exists()}, staging=${stagingDir.exists()}")

                // Mark download in progress (GN pattern)
                File(installDir, ".download_complete").delete()
                File(installDir, ".download_in_progress").createNewFile()

                _activeDownload.value = SteamDownloadProgress(
                    appId = appId,
                    gameName = gameName,
                    coverPath = coverPath,
                    progress = 0f,
                    totalBytes = 0L,
                    bytesDownloaded = 0L,
                    state = _downloadState.value
                )

                // Get Windows depots
                val depots = getWindowsDepots(appInfo)
                if (depots.isEmpty()) {
                    throw IllegalStateException("No Windows depots found for this game")
                }

                // Try to get size from PICS data first, then from manifests if needed
                var totalSize = depots.sumOf { it.size }
                if (totalSize == 0L) {
                    Log.d(TAG, "PICS sizes are 0, fetching from manifests...")
                    totalSize = fetchDepotSizes(appId.toInt(), depots)
                }
                Log.d(TAG, "Total size calculated: ${totalSize / 1024 / 1024}MB for $gameName")

                if (totalSize == 0L) {
                    Log.e(TAG, "Cannot determine download size for $gameName - aborting to prevent storage issues")
                    _downloadState.value = SteamDownloadState.Failed(appId, gameName, "Could not determine download size")
                    return@launch
                }

                // Measure existing content before starting (for resumed downloads)
                val baselineBytes = getDirectorySize(installDir)
                val bytesDownloaded = java.util.concurrent.atomic.AtomicLong(baselineBytes)
                Log.d(TAG, "Starting download: $gameName (${depots.size} depots, ${totalSize / 1024 / 1024}MB, baseline=${baselineBytes / 1024 / 1024}MB)")

                val initialProgress = if (totalSize > 0) (baselineBytes.toFloat() / totalSize).coerceIn(0f, 1f) else 0f
                _downloadState.value = SteamDownloadState.Downloading(
                    appId = appId,
                    gameName = gameName,
                    progress = initialProgress,
                    currentDepot = 0,
                    totalDepots = depots.size
                )

                // Launch depots with limited concurrency
                val completedCount = java.util.concurrent.atomic.AtomicInteger(0)
                synchronized(activeDepotDeferreds) { activeDepotDeferreds.clear() }
                val depotSemaphore = kotlinx.coroutines.sync.Semaphore(MAX_PARALLEL_DEPOTS)

                // Track per-depot last reported bytes for delta calculation
                val depotLastBytes = java.util.concurrent.ConcurrentHashMap<Int, Long>()

                val depotJobs = depots.map { depot ->
                    scope.async {
                        depotSemaphore.acquire()
                        try {
                            if (isCancelled) return@async false
                            Log.d(TAG, "Starting depot ${depot.depotId}: ${depot.name}")
                            val success = downloadDepotParallel(
                                appId = appId.toInt(),
                                depotId = depot.depotId,
                                depotSize = depot.size,
                                installPath = installDir.absolutePath,
                                stagingPath = stagingDir.absolutePath
                            ) { depotBytes ->
                                val lastBytes = depotLastBytes.getOrDefault(depot.depotId, 0L)
                                val delta = (depotBytes - lastBytes).coerceAtLeast(0L)
                                depotLastBytes[depot.depotId] = depotBytes
                                bytesDownloaded.addAndGet(delta)
                            }
                            val count = completedCount.incrementAndGet()
                            Log.d(TAG, "Depot ${depot.depotId} ${if (success) "completed" else "failed"} ($count/${depots.size})")
                            success
                        } finally {
                            depotSemaphore.release()
                        }
                    }
                }

                // Progress updater — reads atomic counter, no directory walking
                val progressJob = scope.launch {
                    var lastLoggedPct = -1
                    while (depotJobs.any { it.isActive }) {
                        val currentBytes = bytesDownloaded.get()
                        val progress = if (totalSize > 0) (currentBytes.toFloat() / totalSize).coerceIn(0f, 1f) else 0f
                        val pct = (progress * 100).toInt()
                        val completed = completedCount.get()

                        if (pct != lastLoggedPct && pct % 5 == 0) {
                            Log.d(TAG, "Download progress: $pct% (${currentBytes / 1024 / 1024}MB / ${totalSize / 1024 / 1024}MB, $completed/${depots.size} depots done)")
                            lastLoggedPct = pct
                        }

                        val currentState = SteamDownloadState.Downloading(
                            appId = appId,
                            gameName = gameName,
                            progress = progress,
                            currentDepot = completed,
                            totalDepots = depots.size
                        )
                        _downloadState.value = currentState
                        _activeDownload.value = SteamDownloadProgress(
                            appId = appId,
                            gameName = gameName,
                            coverPath = coverPath,
                            progress = progress,
                            totalBytes = totalSize,
                            bytesDownloaded = currentBytes,
                            state = currentState
                        )

                        kotlinx.coroutines.delay(500)
                    }
                }

                // Wait for all depots
                val results = depotJobs.map { runCatching { it.await() } }
                progressJob.cancel()
                synchronized(activeDepotDeferreds) { activeDepotDeferreds.clear() }

                val successCount = results.count { it.getOrDefault(false) }
                Log.d(TAG, "All depots processed ($successCount/${depots.size} succeeded)")

                // Clean up staging directory
                stagingDir.deleteRecursively()

                if (isCancelled) {
                    val progress = completedCount.get().toFloat() / depots.size
                    _downloadState.value = SteamDownloadState.Paused(appId, gameName, progress)
                    return@launch
                }
                Log.d(TAG, "Download complete at: ${installDir.absolutePath}")

                // Replace in-progress marker with completion marker
                File(installDir, ".download_in_progress").delete()
                File(installDir, ".download_complete").createNewFile()
                File(installDir, ".DownloadInfo").mkdirs()
                Log.d(TAG, "Created GN markers at ${installDir.absolutePath}")

                // Update game with local path
                gameDao.getBySteamAppId(appId)?.let { game ->
                    gameDao.update(game.copy(
                        localPath = installDir.absolutePath,
                        source = GameSource.STEAM
                    ))
                }

                val completedState = SteamDownloadState.Completed(appId, gameName, installDir.absolutePath)
                _downloadState.value = completedState

                // Add to completed list
                val finalSize = getDirectorySize(installDir)
                val completedProgress = SteamDownloadProgress(
                    appId = appId,
                    gameName = gameName,
                    coverPath = coverPath,
                    progress = 1f,
                    totalBytes = finalSize,
                    bytesDownloaded = finalSize,
                    state = completedState
                )
                _completedDownloads.value = _completedDownloads.value + completedProgress

                Log.d(TAG, "Download complete: $gameName -> ${installDir.absolutePath}")

            } catch (_: kotlinx.coroutines.CancellationException) {
                // Check if this was a pause (state already set by pauseDownload)
                // or a real cancellation
                val wasPaused = _downloadState.value is SteamDownloadState.Paused
                Log.d(TAG, "Download coroutine cancelled, wasPaused=$wasPaused")
                if (!wasPaused) {
                    _activeDownload.value = null
                    processNextInQueue()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Download failed: ${e.message}", e)
                _downloadState.value = SteamDownloadState.Failed(appId, gameName, e.message ?: "Unknown error")
                _activeDownload.value = null
                processNextInQueue()
            }
        }
    }

    private suspend fun downloadDepotParallel(
        appId: Int,
        depotId: Int,
        depotSize: Long,
        installPath: String,
        stagingPath: String,
        onBytesUpdate: (Long) -> Unit = {}
    ): Boolean = withContext(Dispatchers.IO) {
        val client = steamClient ?: return@withContext false

        try {
            val downloader = ContentDownloader(client)
            val deferred = downloader.downloadApp(
                appId = appId,
                depotId = depotId,
                installPath = installPath,
                stagingPath = stagingPath,
                branch = "public",
                maxDownloads = MAX_PARALLEL_DOWNLOADS,
                onDownloadProgress = { progress ->
                    if (depotSize > 0) {
                        onBytesUpdate((progress * depotSize).toLong())
                    }
                },
                parentScope = scope
            ) ?: return@withContext false

            synchronized(activeDepotDeferreds) { activeDepotDeferreds.add(deferred) }

            val result = withTimeoutOrNull(3600_000L) { deferred.await() }

            synchronized(activeDepotDeferreds) { activeDepotDeferreds.remove(deferred) }

            if (result == null) {
                Log.e(TAG, "Depot $depotId timed out after 60 minutes")
                deferred.cancel()
                return@withContext false
            }

            result
        } catch (e: Exception) {
            Log.e(TAG, "Error downloading depot $depotId", e)
            false
        }
    }

    suspend fun checkForUpdate(appId: Long, currentBuildId: Long?, appInfo: KeyValue): Boolean {
        if (currentBuildId == null) return true

        val latestBuildId = appInfo["depots"]["branches"]["public"]["buildid"]
            ?.asString()?.toLongOrNull() ?: return false

        val hasUpdate = latestBuildId > currentBuildId
        Log.d(TAG, "Update check for $appId: current=$currentBuildId, latest=$latestBuildId, hasUpdate=$hasUpdate")
        return hasUpdate
    }

    fun getEstimatedSize(appInfo: KeyValue): Long {
        val depots = getWindowsDepots(appInfo)
        return depots.sumOf { it.size }
    }

    suspend fun getInstallDir(appId: Long): File {
        // Prefer GameNative path for GN-compatible installs
        val gnPath = findGnInstallPath(appId)
        if (gnPath != null) return gnPath

        val prefs = preferencesRepository.userPreferences.first()
        val basePath = prefs.romStoragePath
            ?: context.getExternalFilesDir(null)?.absolutePath
            ?: context.filesDir.absolutePath

        return File(basePath, "$STEAM_PLATFORM_DIR/$appId")
    }

    private fun findGnInstallPath(appId: Long): File? {
        val gameName = kotlinx.coroutines.runBlocking {
            gameDao.getBySteamAppId(appId)?.title
        } ?: return null

        val gnBasePath = findGnStoragePath() ?: return null
        val sanitized = sanitizeGameName(gameName)
        return File("$gnBasePath/Steam/steamapps/common/$sanitized")
    }

    private fun findGnStoragePath(): String? {
        val volumes = mutableListOf(android.os.Environment.getExternalStorageDirectory().absolutePath)
        try {
            File("/storage").listFiles()?.forEach { vol ->
                if (vol.isDirectory && vol.name != "emulated" && vol.name != "self") {
                    volumes.add(vol.absolutePath)
                }
            }
        } catch (_: Exception) {}

        for (root in volumes) {
            val basePath = "$root/Android/data/$GN_PACKAGE/files"
            val steamappsPath = "$basePath/Steam/steamapps"

            // Try raw path first (works on SD cards with MANAGE_EXTERNAL_STORAGE)
            if (File(steamappsPath).exists()) {
                return basePath
            }

            // Fall back to alt-access path (needed for internal storage on Android 11+)
            if (androidDataAccessor.exists(steamappsPath)) {
                return androidDataAccessor.transformPath(basePath)
            }
        }
        return null
    }

    private fun sanitizeGameName(name: String): String {
        return name.replace(Regex("[<>:\"/\\\\|?*]"), "_").trim()
    }


    fun isGameInstalled(appId: Long): Boolean {
        val game = kotlinx.coroutines.runBlocking { gameDao.getBySteamAppId(appId) }
        val path = game?.localPath ?: return false
        return File(path, ".download_complete").exists() || androidDataAccessor.exists("$path/.download_complete")
    }

    fun isGameDownloading(appId: Long): Boolean {
        val game = kotlinx.coroutines.runBlocking { gameDao.getBySteamAppId(appId) }
        val path = game?.localPath ?: return false
        return File(path, ".download_in_progress").exists() || androidDataAccessor.exists("$path/.download_in_progress")
    }

    private fun getDirectorySize(dir: File): Long {
        if (!dir.exists()) return 0L
        var totalSize = 0L
        var fileCount = 0
        dir.walkTopDown().forEach { file ->
            if (file.isFile) {
                totalSize += file.length()
                fileCount++
            }
        }
        // Debug log occasionally
        if (fileCount > 0 || totalSize > 0) {
            Log.v(TAG, "getDirectorySize(${dir.name}): $fileCount files, ${totalSize / 1024 / 1024}MB")
        }
        return totalSize
    }

    private fun moveDirectory(source: File, destination: File): Boolean {
        try {
            // First try a simple rename (works if on same filesystem)
            if (source.renameTo(destination)) {
                Log.d(TAG, "Moved directory via rename")
                return true
            }

            // If rename fails (different filesystems), copy then delete
            Log.d(TAG, "Rename failed, copying files to destination...")
            destination.mkdirs()

            var filesCopied = 0
            var bytesCopied = 0L

            source.walkTopDown().forEach { file ->
                val relativePath = file.relativeTo(source).path
                val destFile = File(destination, relativePath)

                if (file.isDirectory) {
                    destFile.mkdirs()
                } else {
                    destFile.parentFile?.mkdirs()
                    file.copyTo(destFile, overwrite = true)
                    filesCopied++
                    bytesCopied += file.length()

                    if (filesCopied % 100 == 0) {
                        Log.d(TAG, "Copied $filesCopied files (${bytesCopied / 1024 / 1024}MB)...")
                    }
                }
            }

            Log.d(TAG, "Copied $filesCopied files (${bytesCopied / 1024 / 1024}MB) to destination")

            // Verify destination has content
            val destSize = getDirectorySize(destination)
            if (destSize == 0L) {
                Log.e(TAG, "Destination directory is empty after copy!")
                return false
            }

            // Delete source after successful copy
            source.deleteRecursively()
            Log.d(TAG, "Deleted source directory after move")

            return true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to move directory: ${e.message}", e)
            return false
        }
    }

    fun cancelDownload() {
        val activeAppId = _activeDownload.value?.appId
        Log.d(TAG, "cancelDownload called, activeAppId=$activeAppId")

        isCancelled = true
        synchronized(activeDepotDeferreds) {
            activeDepotDeferreds.forEach { it.cancel() }
            activeDepotDeferreds.clear()
        }
        currentDownloadJob?.cancel()
        currentDownloadJob = null
        _downloadState.value = SteamDownloadState.Idle
        _activeDownload.value = null
        Log.d(TAG, "Download cancelled, cleaning up...")

        // Clean up partial download files
        if (activeAppId != null) {
            scope.launch {
                Log.d(TAG, "Starting cleanup for appId=$activeAppId")
                val cleaned = cleanupIncompleteDownload(activeAppId)
                Log.d(TAG, "Cleanup complete for appId=$activeAppId, cleaned=$cleaned")
            }
        } else {
            Log.w(TAG, "No activeAppId to clean up")
        }
    }

    fun cancelQueuedDownload(appId: Long) {
        _downloadQueue.value = _downloadQueue.value.filter { it.appId != appId }
        Log.d(TAG, "Removed $appId from queue (new queue size: ${_downloadQueue.value.size})")
    }

    fun pauseDownload() {
        val active = _activeDownload.value ?: return
        val currentProgress = active.progress
        val currentState = _downloadState.value

        // Set paused state BEFORE cancelling job to prevent finally block from clearing it
        val needsVerification = currentState is SteamDownloadState.Preparing
        val pausedState = SteamDownloadState.Paused(active.appId, active.gameName, currentProgress, needsVerification)
        _downloadState.value = pausedState
        _activeDownload.value = active.copy(state = pausedState)

        isCancelled = true
        synchronized(activeDepotDeferreds) {
            activeDepotDeferreds.forEach { it.cancel() }
            activeDepotDeferreds.clear()
        }
        currentDownloadJob?.cancel()
        currentDownloadJob = null

        Log.d(TAG, "Download paused at ${(currentProgress * 100).toInt()}% (needsVerification=$needsVerification)")
    }

    suspend fun deleteGame(appId: Long): Boolean = withContext(Dispatchers.IO) {
        try {
            val game = gameDao.getBySteamAppId(appId) ?: return@withContext false
            val installPath = game.localPath ?: return@withContext false
            val installDir = File(installPath)

            if (installDir.exists()) {
                installDir.deleteRecursively()
                Log.d(TAG, "Deleted game files: ${installDir.absolutePath}")
            }

            gameDao.update(game.copy(localPath = null))
            Log.d(TAG, "Cleared local path for game: ${game.title}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete game", e)
            false
        }
    }

    data class IncompleteDownload(
        val appId: Long,
        val installPath: String,
        val stagingPath: String,
        val bytesDownloaded: Long,
        val gameName: String?
    )

    suspend fun getSteamDir(): File {
        val prefs = preferencesRepository.userPreferences.first()
        val basePath = prefs.romStoragePath
            ?: context.getExternalFilesDir(null)?.absolutePath
            ?: context.filesDir.absolutePath
        return File(basePath, STEAM_PLATFORM_DIR)
    }

    suspend fun discoverLocalSteamGames(): Int = withContext(Dispatchers.IO) {
        var discovered = 0

        // Scan fallback storage (steam/{appId} directories)
        val steamDir = getSteamDir()
        if (steamDir.exists()) {
            val appDirs = steamDir.listFiles { file ->
                file.isDirectory && file.name.toLongOrNull() != null && !file.name.endsWith("_staging")
            } ?: emptyArray()

            for (appDir in appDirs) {
                val appId = appDir.name.toLongOrNull() ?: continue
                val stagingDir = File(steamDir, "${appId}_staging")
                if (stagingDir.exists()) continue

                val game = gameDao.getBySteamAppId(appId)
                if (game?.localPath != null) continue

                val size = getDirectorySize(appDir)
                if (size < 1024 * 1024) continue

                if (game != null) {
                    gameDao.update(game.copy(localPath = appDir.absolutePath, source = GameSource.STEAM))
                    Log.d(TAG, "Discovered Steam game: ${game.title} at ${appDir.absolutePath}")
                    discovered++
                }
            }
        }

        // Scan GN steamapps/common (game title directories with .download_complete)
        val gnBasePath = findGnStoragePath()
        if (gnBasePath != null) {
            val commonDir = File("$gnBasePath/Steam/steamapps/common")
            if (commonDir.exists()) {
                val gameDirs = commonDir.listFiles { file ->
                    file.isDirectory && File(file, ".download_complete").exists()
                } ?: emptyArray()

                val steamGames = gameDao.getAllWithSteamAppId()
                for (gameDir in gameDirs) {
                    val gameName = gameDir.name
                    val match = steamGames.find { sanitizeGameName(it.title) == gameName }
                    if (match != null && match.localPath == null) {
                        gameDao.update(match.copy(localPath = gameDir.absolutePath, source = GameSource.STEAM))
                        Log.d(TAG, "Discovered GN game: ${match.title} at ${gameDir.absolutePath}")
                        discovered++
                    }
                }
            }
        }

        Log.d(TAG, "Steam discovery complete: $discovered games recovered")
        discovered
    }

    suspend fun getIncompleteDownloads(): List<IncompleteDownload> = withContext(Dispatchers.IO) {
        val incomplete = mutableListOf<IncompleteDownload>()

        // Check final destination (SD card)
        val steamDir = getSteamDir()
        if (steamDir.exists()) {
            val stagingDirs = steamDir.listFiles { file ->
                file.isDirectory && file.name.endsWith("_staging")
            } ?: emptyArray()

            for (stagingDir in stagingDirs) {
                val appIdStr = stagingDir.name.removeSuffix("_staging")
                val appId = appIdStr.toLongOrNull() ?: continue
                val installDir = File(steamDir, appIdStr)

                val bytesDownloaded = getDirectorySize(installDir) + getDirectorySize(stagingDir)
                val game = gameDao.getBySteamAppId(appId)

                incomplete.add(IncompleteDownload(
                    appId = appId,
                    installPath = installDir.absolutePath,
                    stagingPath = stagingDir.absolutePath,
                    bytesDownloaded = bytesDownloaded,
                    gameName = game?.title
                ))
            }
        }

        // Check internal storage (where active downloads happen)
        val internalSteamDir = File(context.filesDir, "steam_downloads")
        if (internalSteamDir.exists()) {
            // Check for staging directories
            val internalStagingDirs = internalSteamDir.listFiles { file ->
                file.isDirectory && file.name.endsWith("_staging")
            } ?: emptyArray()

            // Also check for app directories without staging (paused downloads)
            val internalAppDirs = internalSteamDir.listFiles { file ->
                file.isDirectory && !file.name.endsWith("_staging") && file.name.toLongOrNull() != null
            } ?: emptyArray()

            for (appDir in internalAppDirs) {
                val appId = appDir.name.toLongOrNull() ?: continue

                // Skip if already found via staging
                if (incomplete.any { it.appId == appId }) continue

                // Check if this is an incomplete download (has content but not completed)
                val game = gameDao.getBySteamAppId(appId)
                val isCompleted = game?.localPath?.let { File(it).exists() } ?: false
                if (isCompleted) continue

                val bytesDownloaded = getDirectorySize(appDir)
                if (bytesDownloaded < 1024 * 1024) continue // Skip if less than 1MB

                val stagingDir = File(internalSteamDir, "${appId}_staging")
                incomplete.add(IncompleteDownload(
                    appId = appId,
                    installPath = appDir.absolutePath,
                    stagingPath = stagingDir.absolutePath,
                    bytesDownloaded = bytesDownloaded,
                    gameName = game?.title
                ))
                Log.d(TAG, "Found paused download in internal storage: $appId (${bytesDownloaded / 1024 / 1024}MB)")
            }

            for (stagingDir in internalStagingDirs) {
                val appIdStr = stagingDir.name.removeSuffix("_staging")
                val appId = appIdStr.toLongOrNull() ?: continue

                // Skip if already found in final location
                if (incomplete.any { it.appId == appId }) continue

                val installDir = File(internalSteamDir, appIdStr)
                val bytesDownloaded = getDirectorySize(installDir) + getDirectorySize(stagingDir)
                val game = gameDao.getBySteamAppId(appId)

                incomplete.add(IncompleteDownload(
                    appId = appId,
                    installPath = installDir.absolutePath,
                    stagingPath = stagingDir.absolutePath,
                    bytesDownloaded = bytesDownloaded,
                    gameName = game?.title
                ))
            }
        }

        Log.d(TAG, "Found ${incomplete.size} incomplete Steam downloads")
        incomplete
    }

    suspend fun recoverDownload(appId: Long): Boolean = withContext(Dispatchers.IO) {
        if (!isConnected()) {
            Log.w(TAG, "Cannot recover download - not connected to Steam")
            return@withContext false
        }

        val game = gameDao.getBySteamAppId(appId)
        if (game == null) {
            Log.w(TAG, "Cannot recover download - game $appId not in database")
            return@withContext false
        }

        try {
            Log.d(TAG, "Recovering download for ${game.title} (appId: $appId)")
            val appInfo = fetchAppInfo(appId.toInt())
            queueDownload(appId, game.title, appInfo, game.coverPath)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to recover download for $appId", e)
            false
        }
    }

    suspend fun cleanupIncompleteDownload(appId: Long): Boolean = withContext(Dispatchers.IO) {
        var cleaned = false

        // Collect all paths that might contain this download
        val pathsToClean = mutableSetOf<File>()

        // 1. The install dir we'd use for a new download (GN path or fallback)
        pathsToClean.add(getInstallDir(appId))

        // 2. The game's recorded localPath in the DB
        gameDao.getBySteamAppId(appId)?.localPath?.let { pathsToClean.add(File(it)) }

        // 3. Fallback storage path (in case GN path changed since download)
        val steamDir = getSteamDir()
        pathsToClean.add(File(steamDir, appId.toString()))

        // 4. Internal staging dir
        val stagingDir = File(context.filesDir, "steam_staging/$appId")

        Log.d(TAG, "Cleanup paths for $appId:")
        for (dir in pathsToClean) {
            Log.d(TAG, "  ${dir.absolutePath} (exists=${dir.exists()})")
            if (dir.exists()) {
                val deleted = dir.deleteRecursively()
                Log.d(TAG, "  Deleted: $deleted")
                cleaned = true
            }
        }

        if (stagingDir.exists()) {
            val deleted = stagingDir.deleteRecursively()
            Log.d(TAG, "  Staging ${stagingDir.absolutePath}: deleted=$deleted")
            cleaned = true
        }

        // Clear local path in database
        gameDao.getBySteamAppId(appId)?.let { game ->
            if (game.localPath != null) {
                gameDao.update(game.copy(localPath = null))
                Log.d(TAG, "Cleared local path in database for ${game.title}")
            }
        }

        cleaned
    }

    suspend fun recoverAllIncomplete(): Int = withContext(Dispatchers.IO) {
        if (!isConnected()) {
            Log.w(TAG, "Cannot recover downloads - not connected to Steam")
            return@withContext 0
        }

        val incomplete = getIncompleteDownloads()
        var recovered = 0

        for (download in incomplete) {
            if (recoverDownload(download.appId)) {
                recovered++
            }
        }

        Log.d(TAG, "Recovered $recovered of ${incomplete.size} incomplete downloads")
        recovered
    }

    fun cleanup() {
        cancelDownload()
        picsSubscription?.close()
        picsSubscription = null
        steamClient = null
        steamApps = null
        callbackManager = null
        contentDownloader = null
    }
}
