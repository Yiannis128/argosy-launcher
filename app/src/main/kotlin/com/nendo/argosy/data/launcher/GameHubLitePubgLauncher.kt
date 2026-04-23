package com.nendo.argosy.data.launcher

import android.content.ComponentName
import android.content.Intent

object GameHubLitePubgLauncher : SteamLauncher {
    override val packageName = "com.tencent.ig"
    override val displayName = "GameHub Lite (PUBG)"

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
