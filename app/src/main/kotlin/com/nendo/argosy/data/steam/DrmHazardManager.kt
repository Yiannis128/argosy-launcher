package com.nendo.argosy.data.steam

import android.util.Log
import com.nendo.argosy.data.local.dao.GameDao
import com.nendo.argosy.data.model.GameSource
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "DrmHazardManager"

@Singleton
class DrmHazardManager @Inject constructor(
    private val gameDao: GameDao
) {
    private val knownDrmAppIds: Set<Long> = setOf(
        // Well-known Denuvo titles that have compatibility issues
        601150L,   // Devil May Cry 5
        1091500L,  // Cyberpunk 2077 (removed Denuvo later)
        1172470L,  // Apex Legends
        1245620L,  // Elden Ring
        1328670L,  // Mass Effect Legendary Edition
        1817070L,  // Marvel's Spider-Man Remastered
        1817190L,  // Marvel's Spider-Man: Miles Morales
        1593500L,  // God of War
        1888160L,  // Armored Core VI
        2050650L,  // Resident Evil 4 (2023)
        1113560L,  // Ni no Kuni II
        814380L,   // Sekiro
        1151640L,  // Horizon Zero Dawn
        1151340L,  // Fallout 76 (has custom launcher DRM)
        1286830L,  // Star Wars Jedi: Fallen Order
        1659040L,  // Forza Horizon 5
        1240440L,  // Halo Infinite
        1142710L,  // Star Wars Jedi: Survivor
        1938010L,  // Tekken 8
        1145360L,  // Hades II (Early Access)
        2208920L,  // Lords of the Fallen (2023)
        // Games with other problematic DRM
        292030L,   // The Witcher 3 (GOG Galaxy required for some features)
        1222670L,  // The Sims 4 (EA App required)
    )

    private val knownDrmKeywords = setOf(
        "denuvo",
        "securom",
        "starforce",
        "ea app",
        "origin",
        "ubisoft connect",
        "uplay",
        "rockstar games launcher",
        "epic games",
        "battle.net"
    )

    fun isKnownDrmTitle(steamAppId: Long): Boolean {
        return steamAppId in knownDrmAppIds
    }

    fun hasDrmKeywords(eula: String?): Boolean {
        if (eula.isNullOrBlank()) return false
        val lowerEula = eula.lowercase()
        return knownDrmKeywords.any { it in lowerEula }
    }

    fun detectDrmFromPics(drm: String?): Boolean {
        if (drm.isNullOrBlank()) return false
        val lowerDrm = drm.lowercase()
        return knownDrmKeywords.any { it in lowerDrm }
    }

    suspend fun checkDrmHazard(steamAppId: Long): Boolean {
        gameDao.getBySteamAppId(steamAppId) ?: return false
        return isKnownDrmTitle(steamAppId)
    }

    suspend fun getKnownDrmGameIds(): Set<Long> {
        val steamGames = gameDao.getBySource(GameSource.STEAM)
        return steamGames
            .mapNotNull { it.steamAppId }
            .filter { isKnownDrmTitle(it) }
            .toSet()
    }
}
