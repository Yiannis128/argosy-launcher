package com.nendo.argosy.domain.usecase.achievement

import com.nendo.argosy.data.cache.ImageCacheManager
import com.nendo.argosy.data.local.dao.AchievementDao
import com.nendo.argosy.data.local.dao.GameDao
import com.nendo.argosy.data.local.entity.AchievementEntity
import com.nendo.argosy.data.remote.romm.RomMRepository
import com.nendo.argosy.data.remote.romm.RomMResult
import com.nendo.argosy.data.repository.RA_BADGE_BASE_URL
import com.nendo.argosy.data.repository.RetroAchievementsRepository
import com.nendo.argosy.util.parseTimestamp
import javax.inject.Inject

data class AchievementCounts(
    val total: Int,
    val earned: Int
)

class FetchAchievementsUseCase @Inject constructor(
    private val romMRepository: RomMRepository,
    private val raRepository: RetroAchievementsRepository,
    private val verifyRAGameIdUseCase: VerifyRAGameIdUseCase,
    private val achievementDao: AchievementDao,
    private val gameDao: GameDao,
    private val imageCacheManager: ImageCacheManager
) {
    suspend operator fun invoke(gameId: Long, rommId: Long? = null, raId: Long? = null): AchievementCounts? {
        // Fetch RA data from RomM (which syncs with RA server-side using its API key).
        // Never call RA directly for UI display -- that's only for in-game sessions.
        if (rommId != null) {
            return fetchFromRomM(rommId, gameId)
        }

        return null
    }

    private suspend fun fetchFromRomM(rommId: Long, gameId: Long): AchievementCounts? {
        return when (val result = romMRepository.getRom(rommId)) {
            is RomMResult.Success -> {
                val rom = result.data
                val apiAchievements = rom.raMetadata?.achievements
                if (apiAchievements.isNullOrEmpty()) return null

                romMRepository.refreshRAProgressionIfNeeded(force = true)
                val earnedAchievements = rom.raId?.let { romMRepository.getEarnedAchievements(it) } ?: emptyList()
                val earnedByBadgeId = earnedAchievements.associateBy { it.id }

                val entities = apiAchievements.map { achievement ->
                    val earned = earnedByBadgeId[achievement.badgeId]
                    val unlockedAt = earned?.date?.let { parseTimestamp(it) }
                    val unlockedHardcoreAt = earned?.dateHardcore?.let { parseTimestamp(it) }

                    AchievementEntity(
                        gameId = gameId,
                        raId = achievement.raId,
                        title = achievement.title,
                        description = achievement.description,
                        points = achievement.points,
                        type = achievement.type,
                        badgeUrl = achievement.badgeUrl,
                        badgeUrlLock = achievement.badgeUrlLock,
                        unlockedAt = unlockedAt,
                        unlockedHardcoreAt = unlockedHardcoreAt
                    )
                }
                achievementDao.replaceForGame(gameId, entities)
                gameDao.updateAchievementsFetchedAt(gameId, System.currentTimeMillis())

                val earnedCount = entities.count { it.isUnlocked }
                gameDao.updateAchievementCount(gameId, entities.size, earnedCount)
                queueBadgeCaching(gameId)

                AchievementCounts(total = entities.size, earned = earnedCount)
            }
            is RomMResult.Error -> null
        }
    }

    private suspend fun queueBadgeCaching(gameId: Long) {
        achievementDao.getByGameId(gameId).forEach { achievement ->
            if (achievement.cachedBadgeUrl == null && achievement.badgeUrl != null) {
                imageCacheManager.queueBadgeCache(
                    achievement.id,
                    achievement.badgeUrl,
                    achievement.badgeUrlLock
                )
            }
        }
    }
}
