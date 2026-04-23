package com.nendo.argosy.data.launcher

import android.content.ComponentName
import android.content.Intent

// GameHub scanning was previously a log-scraping backchannel that missed
// games and didn't detect uninstalls. Dropped in favor of the Mark-as-Installed
// modal (Steam login) which gives users a reliable manual path. The launcher
// remains for actually launching games flagged as GH-managed.
object GameHubLauncher : SteamLauncher {
    override val packageName = "com.xiaoji.egggame"
    override val displayName = "GameHub"

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
