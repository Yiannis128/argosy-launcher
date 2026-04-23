package com.nendo.argosy.data.launcher

import android.content.Context

object SteamLaunchers {
    val all: List<SteamLauncher> = listOf(
        GameHubLauncher,
        GameHubLiteLauncher,
        GameHubLiteAntutuLauncher,
        GameHubLiteLudashiLauncher,
        GameHubLitePubgLauncher,
        GameNativeLauncher
    )

    private val gameHubVariants: List<SteamLauncher> = listOf(
        GameHubLauncher,
        GameHubLiteLauncher,
        GameHubLiteAntutuLauncher,
        GameHubLiteLudashiLauncher,
        GameHubLitePubgLauncher
    )

    fun getInstalled(context: Context): List<SteamLauncher> =
        all.filter { it.isInstalled(context) }

    fun getByPackage(packageName: String): SteamLauncher? =
        all.find { it.packageName == packageName }

    fun getPreferred(context: Context): SteamLauncher? =
        getInstalled(context).firstOrNull()

    /**
     * User-facing grouping for the Mark-as-Installed modal. GameHub variants
     * (proper + every GH Lite flavor) collapse into one option: the specific
     * package stored is whichever variant is installed first.
     */
    sealed class MarkOption(val launcherPackage: String, val displayName: String) {
        object GameNative : MarkOption(GameNativeLauncher.packageName, GameNativeLauncher.displayName)
        class GameHub(launcherPackage: String) : MarkOption(launcherPackage, "GameHub")
    }

    fun getMarkOptions(context: Context): List<MarkOption> = buildList {
        if (GameNativeLauncher.isInstalled(context)) add(MarkOption.GameNative)
        gameHubVariants.firstOrNull { it.isInstalled(context) }?.let {
            add(MarkOption.GameHub(it.packageName))
        }
    }

    /** Resolve a stored launcher package back to a user-friendly group label. */
    fun displayNameForPackage(launcherPackage: String?): String = when {
        launcherPackage == null -> ""
        launcherPackage == GameNativeLauncher.packageName -> GameNativeLauncher.displayName
        gameHubVariants.any { it.packageName == launcherPackage } -> "GameHub"
        else -> getByPackage(launcherPackage)?.displayName ?: launcherPackage
    }
}
