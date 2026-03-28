package com.nendo.argosy.data.steam

import android.content.Context
import android.os.StatFs
import android.util.Log
import com.nendo.argosy.data.local.dao.GameDao
import com.nendo.argosy.data.preferences.UserPreferencesRepository
import com.nendo.argosy.data.storage.AndroidDataAccessor
import com.nendo.argosy.data.storage.StorageVolumeDetector
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "SteamPathResolver"
private const val STEAM_PLATFORM_DIR = "steam"
private const val GN_PACKAGE = "app.gamenative"
private const val DOWNLOAD_INFO_DIR = ".DownloadInfo"

data class SteamInstallVolume(
    val path: String,
    val label: String,
    val freeBytes: Long,
    val hasGnPath: Boolean
)

@Singleton
class SteamPathResolver @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gameDao: GameDao,
    private val preferencesRepository: UserPreferencesRepository,
    private val androidDataAccessor: AndroidDataAccessor,
    private val storageVolumeDetector: StorageVolumeDetector
) {

    suspend fun getInstallDirByName(dirName: String): File {
        val configuredBase = getConfiguredInstallBase()
        if (configuredBase != null) {
            return File("$configuredBase/Steam/steamapps/common/$dirName")
        }
        val gnBasePath = findGnStoragePath()
        if (gnBasePath != null) {
            return File("$gnBasePath/Steam/steamapps/common/$dirName")
        }
        val prefs = preferencesRepository.userPreferences.first()
        val basePath = prefs.romStoragePath
            ?: externalFilesDir()
            ?: internalFilesDir()
        return File(basePath, "$STEAM_PLATFORM_DIR/$dirName")
    }

    suspend fun getInstallDir(appId: Long): File {
        val configuredBase = getConfiguredInstallBase()
        if (configuredBase != null) {
            val gameName = gameDao.getBySteamAppId(appId)?.title
            if (gameName != null) {
                val sanitized = sanitizeGameName(gameName)
                return File("$configuredBase/Steam/steamapps/common/$sanitized")
            }
        }
        val gnPath = findGnInstallPath(appId)
        if (gnPath != null) return gnPath

        val prefs = preferencesRepository.userPreferences.first()
        val basePath = prefs.romStoragePath
            ?: externalFilesDir()
            ?: internalFilesDir()

        return File(basePath, "$STEAM_PLATFORM_DIR/$appId")
    }

    suspend fun getSteamDir(): File {
        val prefs = preferencesRepository.userPreferences.first()
        val basePath = prefs.romStoragePath
            ?: externalFilesDir()
            ?: internalFilesDir()
        return File(basePath, STEAM_PLATFORM_DIR)
    }

    suspend fun isGameInstalled(appId: Long): Boolean {
        val game = gameDao.getBySteamAppId(appId)
        val path = game?.localPath ?: return false
        return File(path, ".download_complete").exists() || androidDataAccessor.exists("$path/.download_complete")
    }

    fun findGnStoragePath(): String? {
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

            if (File(steamappsPath).exists()) {
                return basePath
            }

            if (androidDataAccessor.exists(steamappsPath)) {
                return androidDataAccessor.transformPath(basePath)
            }
        }
        return null
    }

    fun sanitizeGameName(name: String): String {
        return name.replace(Regex("[<>:\"/\\\\|?*]"), "_").trim()
    }

    fun getAvailableBytes(dir: File): Long? {
        return try {
            var check: File? = dir
            while (check != null) {
                try {
                    return android.os.StatFs(check.absolutePath).availableBytes
                } catch (_: IllegalArgumentException) {
                    check = check.parentFile
                }
            }
            null
        } catch (_: Exception) {
            null
        }
    }

    fun getDirectorySize(dir: File): Long {
        if (!dir.exists()) return 0L
        var totalSize = 0L
        var fileCount = 0
        dir.walkTopDown()
            .onEnter { it.name != ".DepotDownloader" && it.name != DOWNLOAD_INFO_DIR }
            .forEach { file ->
                if (file.isFile && !file.name.startsWith(".download_")) {
                    totalSize += file.length()
                    fileCount++
                }
            }
        if (fileCount > 0 || totalSize > 0) {
            Log.v(TAG, "getDirectorySize(${dir.name}): $fileCount files, ${totalSize / 1024 / 1024}MB")
        }
        return totalSize
    }

    fun moveDirectory(source: File, destination: File): Boolean {
        try {
            if (source.renameTo(destination)) {
                Log.d(TAG, "Moved directory via rename")
                return true
            }

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

            val destSize = getDirectorySize(destination)
            if (destSize == 0L) {
                Log.e(TAG, "Destination directory is empty after copy!")
                return false
            }

            source.deleteRecursively()
            Log.d(TAG, "Deleted source directory after move")

            return true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to move directory: ${e.message}", e)
            return false
        }
    }

    fun getAvailableVolumes(): List<SteamInstallVolume> {
        val volumes = storageVolumeDetector.detectStorageVolumes()
        return volumes.map { vol ->
            SteamInstallVolume(
                path = vol.path,
                label = vol.displayName,
                freeBytes = vol.availableBytes,
                hasGnPath = hasGnSteamPath(vol.path)
            )
        }
    }

    private fun hasGnSteamPath(volumeRoot: String): Boolean {
        val steamappsPath = "$volumeRoot/Android/data/$GN_PACKAGE/files/Steam/steamapps"
        return File(steamappsPath).exists() || androidDataAccessor.exists(steamappsPath)
    }

    private suspend fun getConfiguredInstallBase(): String? {
        val volume = preferencesRepository.userPreferences.first().steamInstallVolume ?: return null
        val basePath = "$volume/Android/data/$GN_PACKAGE/files"
        val steamappsPath = "$basePath/Steam/steamapps"

        if (File(steamappsPath).exists()) return basePath
        if (androidDataAccessor.exists(steamappsPath)) return androidDataAccessor.transformPath(basePath)

        Log.w(TAG, "Configured volume $volume not accessible, falling back to auto-detect")
        return null
    }

    private suspend fun findGnInstallPath(appId: Long): File? {
        val gameName = gameDao.getBySteamAppId(appId)?.title ?: return null
        val gnBasePath = findGnStoragePath() ?: return null
        val sanitized = sanitizeGameName(gameName)
        return File("$gnBasePath/Steam/steamapps/common/$sanitized")
    }

    private fun externalFilesDir(): String? {
        return context.getExternalFilesDir(null)?.absolutePath
    }

    private fun internalFilesDir(): String {
        return context.filesDir.absolutePath
    }
}
