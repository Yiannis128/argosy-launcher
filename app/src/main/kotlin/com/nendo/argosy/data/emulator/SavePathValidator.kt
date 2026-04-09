package com.nendo.argosy.data.emulator

import android.os.Build
import android.os.Environment
import com.nendo.argosy.data.local.dao.EmulatorSaveConfigDao
import com.nendo.argosy.data.storage.FileAccessLayer
import com.nendo.argosy.util.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavePathValidator @Inject constructor(
    private val emulatorSaveConfigDao: EmulatorSaveConfigDao,
    private val fileAccessLayer: FileAccessLayer
) {
    companion object {
        private const val TAG = "SavePathValidator"
    }

    sealed class Result {
        data object Valid : Result()
        data object PermissionRequired : Result()
        data class SavePathNotFound(val checkedPaths: List<String>) : Result()
        data class AccessDenied(val path: String) : Result()
        data object NotFolderBased : Result()
        data object NoConfig : Result()
    }

    suspend fun validateAccess(emulatorId: String, emulatorPackage: String? = null): Result {
        val config = SavePathRegistry.getConfigIncludingUnsupported(emulatorId)
        if (config == null) {
            Logger.debug(TAG, "[SaveSync] VALIDATE | No config for emulator | emulator=$emulatorId")
            return Result.NoConfig
        }

        if (!config.usesFolderBasedSaves) {
            return Result.NotFolderBased
        }

        if (!hasFileAccessPermission()) {
            Logger.debug(TAG, "[SaveSync] VALIDATE | Permission not granted | emulator=$emulatorId")
            return Result.PermissionRequired
        }

        val resolvedPaths = resolvePaths(emulatorId, config, emulatorPackage)

        for (path in resolvedPaths) {
            if (!fileAccessLayer.exists(path) || !fileAccessLayer.isDirectory(path)) continue

            val canRead = try {
                fileAccessLayer.listFiles(path) != null
            } catch (e: SecurityException) {
                Logger.debug(TAG, "[SaveSync] VALIDATE | SecurityException reading path | path=$path, error=${e.message}")
                false
            }

            if (canRead) {
                Logger.debug(TAG, "[SaveSync] VALIDATE | Path accessible | path=$path")
                return Result.Valid
            } else {
                Logger.debug(TAG, "[SaveSync] VALIDATE | Path exists but access denied (SELinux/OEM restriction?) | path=$path")
                return Result.AccessDenied(path)
            }
        }

        Logger.debug(TAG, "[SaveSync] VALIDATE | No save path found | emulator=$emulatorId, package=$emulatorPackage, paths=$resolvedPaths")
        return Result.SavePathNotFound(resolvedPaths)
    }

    suspend fun resolvePaths(
        emulatorId: String,
        config: SavePathConfig,
        emulatorPackage: String?,
        platformSlug: String? = null
    ): List<String> {
        val userConfig = emulatorSaveConfigDao.getByEmulator(emulatorId)
        return if (userConfig?.isUserOverride == true) {
            val basePath = userConfig.savePathPattern
            val effectivePath = when (platformSlug) {
                "3ds" -> {
                    if (basePath.endsWith("/sdmc/Nintendo 3DS") || basePath.endsWith("/sdmc/Nintendo 3DS/")) {
                        basePath.trimEnd('/')
                    } else {
                        "$basePath/sdmc/Nintendo 3DS"
                    }
                }
                else -> basePath
            }
            listOf(effectivePath)
        } else {
            SavePathRegistry.resolvePathWithPackage(config, emulatorPackage)
        }
    }

    private fun hasFileAccessPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            true
        }
    }
}
