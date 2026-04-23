package com.nendo.argosy.data.launcher

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.nendo.argosy.data.storage.AndroidDataAccessor

data class ScannedSteamGame(
    val appId: Long,
    val name: String
)

interface SteamLauncher {
    val packageName: String
    val displayName: String
    val supportsScanning: Boolean get() = false
    val scanMayIncludeUninstalled: Boolean get() = false

    fun isInstalled(context: Context): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun createLaunchIntent(steamAppId: Long): Intent

    suspend fun scan(context: Context, androidDataAccessor: AndroidDataAccessor? = null): List<ScannedSteamGame> = emptyList()
}
