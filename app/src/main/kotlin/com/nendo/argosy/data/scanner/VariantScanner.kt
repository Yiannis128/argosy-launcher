package com.nendo.argosy.data.scanner

import com.nendo.argosy.data.local.dao.GameDiscDao
import com.nendo.argosy.data.local.dao.GameFileDao
import com.nendo.argosy.data.local.entity.GameEntity
import com.nendo.argosy.data.local.entity.GameFileEntity
import com.nendo.argosy.data.model.VariantCategory
import com.nendo.argosy.data.platform.PlatformDefinitions
import com.nendo.argosy.util.Logger
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "VariantScanner"

private val PATCH_EXTENSIONS = setOf("ips", "bps", "ups", "xdelta", "ppf")
private val DISC_COMPONENT_EXTENSIONS = setOf("bin", "raw", "wav", "ogg", "img", "sub")

@Singleton
class VariantScanner @Inject constructor(
    private val gameFileDao: GameFileDao,
    private val gameDiscDao: GameDiscDao
) {
    suspend fun scanForVariants(game: GameEntity): Int {
        if (game.platformSlug in VariantCategory.VARIANT_EXCLUDED_PLATFORMS) return 0

        val primaryPath = game.localPath ?: return 0
        val primaryFile = File(primaryPath)
        val parentDir = primaryFile.parentFile ?: return 0
        if (!parentDir.exists() || !parentDir.isDirectory) return 0
        if (PlatformDefinitions.getBySlug(parentDir.name) != null) return 0

        val platformDef = PlatformDefinitions.getBySlug(game.platformSlug) ?: return 0
        val validExtensions = platformDef.extensions + setOf("m3u")

        val primaryAbsolute = primaryFile.absolutePath
        val primaryM3u = game.m3uPath?.let { File(it).absolutePath }

        val discPaths = gameDiscDao.getDiscsForGame(game.id).mapNotNull { it.localPath }.toSet()

        val referencedFiles = mutableSetOf<String>()

        val allFiles = parentDir.listFiles()?.filter { it.isFile } ?: return 0

        for (file in allFiles) {
            when (file.extension.lowercase()) {
                "m3u" -> file.readLines()
                    .filter { it.isNotBlank() && !it.startsWith("#") }
                    .forEach { referencedFiles.add(File(parentDir, it.trim()).absolutePath) }
                "cue" -> file.readLines()
                    .filter { it.trimStart().startsWith("FILE", ignoreCase = true) }
                    .forEach { line ->
                        val match = Regex("""FILE\s+"([^"]+)"""", RegexOption.IGNORE_CASE).find(line)
                            ?: Regex("""FILE\s+(\S+)""", RegexOption.IGNORE_CASE).find(line)
                        match?.groupValues?.getOrNull(1)?.let {
                            referencedFiles.add(File(parentDir, it).absolutePath)
                        }
                    }
            }
        }

        val candidates = allFiles.filter { file ->
            val ext = file.extension.lowercase()
            val nameLC = file.name.lowercase()
            if (ext in PATCH_EXTENSIONS) return@filter false
            if (ext in DISC_COMPONENT_EXTENSIONS) return@filter false
            if (ext !in validExtensions) return@filter false
            if (nameLC.endsWith(".zip.m3u") || nameLC.endsWith(".7z.m3u")) return@filter false
            if (file.absolutePath == primaryAbsolute) return@filter false
            if (file.absolutePath == primaryM3u) return@filter false
            if (file.absolutePath in discPaths) return@filter false
            if (file.absolutePath in referencedFiles) return@filter false
            true
        }.sortedBy { it.name }

        val launchTargets = candidates

        val existingVariants = gameFileDao.getVariantsForGame(game.id)
        val launchTargetPaths = launchTargets.map { it.absolutePath }.toSet()
        for (variant in existingVariants) {
            val path = variant.localPath ?: continue
            if (path !in launchTargetPaths && variant.rommFileId == null) {
                gameFileDao.deleteById(variant.id)
                Logger.debug(TAG, "Removed stale variant: ${variant.fileName} for game ${game.title}")
            }
        }

        var added = 0
        for (file in launchTargets) {
            val existing = gameFileDao.getByLocalPath(file.absolutePath)
            if (existing != null) continue

            val isM3u = file.extension.equals("m3u", ignoreCase = true)
            val category = if (isM3u) VariantCategory.UNKNOWN else FilenameTagParser.inferCategory(file.name)

            gameFileDao.insert(
                GameFileEntity(
                    gameId = game.id,
                    fileName = file.name,
                    filePath = file.absolutePath,
                    category = category.key,
                    fileSize = file.length(),
                    localPath = file.absolutePath,
                    downloadedAt = java.time.Instant.now(),
                    isLaunchTarget = true,
                    isMultiDisc = isM3u,
                    m3uPath = if (isM3u) file.absolutePath else null
                )
            )
            added++
            Logger.debug(TAG, "Found local variant: ${file.name} (${category.key}) for game ${game.title}")
        }

        return added
    }
}
