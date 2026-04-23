package com.nendo.argosy.data.launcher

import android.content.ComponentName
import android.content.Intent

object GameHubLiteLauncher : SteamLauncher {
    override val packageName = "gamehub.lite"
    override val displayName = "GameHub Lite"

    override fun createLaunchIntent(steamAppId: Long): Intent = Intent().apply {
        component = ComponentName(
            packageName,
            "com.xj.landscape.launcher.ui.gamedetail.GameDetailActivity"
        )
        action = "gamehub.lite.LAUNCH_GAME"
        putExtra("steamAppId", steamAppId.toString())
        putExtra("autoStartGame", true)
    }
}
