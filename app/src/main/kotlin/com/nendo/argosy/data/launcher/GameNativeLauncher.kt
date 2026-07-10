package com.nendo.argosy.data.launcher

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.nendo.argosy.data.storage.AndroidDataAccessor

object GameNativeLauncher : SteamLauncher {
    override val packageName = "app.gamenative"
    override val displayName = "GameNative"
    override val supportsScanning = true

    override fun createLaunchIntent(steamAppId: Long): Intent = Intent().apply {
        component = ComponentName(packageName, "$packageName.MainActivity")
        action = "app.gamenative.LAUNCH_GAME"
        putExtra("app_id", steamAppId.toInt())
    }

    fun createCustomGameLaunchIntent(appId: Int): Intent = Intent().apply {
        component = ComponentName(packageName, "$packageName.MainActivity")
        action = "app.gamenative.LAUNCH_GAME"
        putExtra("app_id", appId)
        putExtra("game_source", "CUSTOM_GAME")
    }

    override suspend fun scan(context: Context, androidDataAccessor: AndroidDataAccessor?): List<ScannedSteamGame> =
        GameNativeScanner.scan(context, androidDataAccessor)
}
