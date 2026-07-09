package com.nendo.argosy.data.speedrun

import com.nendo.argosy.data.local.dao.SpeedrunDao
import com.nendo.argosy.data.local.entity.SpeedrunAttemptEntity
import com.nendo.argosy.data.local.entity.SpeedrunCategoryEntity
import com.nendo.argosy.data.local.entity.SpeedrunSegmentEntity
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Singleton

data class SpeedrunComparison(
    val pbSplitTimesMs: List<Long?> = emptyList(),
    val bestSegmentDurationsMs: List<Long?> = emptyList(),
    val attemptCount: Int = 0
)

@Singleton
class SpeedrunRepository @Inject constructor(
    private val speedrunDao: SpeedrunDao
) {
    fun observeCategoriesForGame(gameId: Long): Flow<List<SpeedrunCategoryEntity>> =
        speedrunDao.observeCategoriesForGame(gameId)

    suspend fun getCategoriesForGame(gameId: Long): List<SpeedrunCategoryEntity> =
        speedrunDao.getCategoriesForGame(gameId)

    suspend fun getSegmentNames(categoryId: Long): List<String> =
        speedrunDao.getSegmentsForCategory(categoryId).map { it.name }

    suspend fun createCategory(gameId: Long, name: String, segmentNames: List<String>, sourceLabel: String? = null): Long {
        val categoryId = speedrunDao.insertCategory(
            SpeedrunCategoryEntity(gameId = gameId, name = name, sourceLabel = sourceLabel, createdAt = System.currentTimeMillis())
        )
        speedrunDao.insertSegments(
            segmentNames.mapIndexed { index, segmentName ->
                SpeedrunSegmentEntity(categoryId = categoryId, orderIndex = index, name = segmentName)
            }
        )
        return categoryId
    }

    suspend fun renameCategory(categoryId: Long, name: String) {
        val category = speedrunDao.getCategory(categoryId) ?: return
        speedrunDao.updateCategory(category.copy(name = name))
    }

    suspend fun replaceSegments(categoryId: Long, segmentNames: List<String>) {
        speedrunDao.deleteSegmentsForCategory(categoryId)
        speedrunDao.insertSegments(
            segmentNames.mapIndexed { index, segmentName ->
                SpeedrunSegmentEntity(categoryId = categoryId, orderIndex = index, name = segmentName)
            }
        )
    }

    suspend fun deleteCategory(categoryId: Long) = speedrunDao.deleteCategory(categoryId)

    suspend fun recordAttempt(
        categoryId: Long,
        startedAt: Long,
        completed: Boolean,
        finalTimeMs: Long?,
        splitTimesMs: List<Long?>
    ) {
        speedrunDao.insertAttempt(
            SpeedrunAttemptEntity(
                categoryId = categoryId,
                startedAt = startedAt,
                completed = completed,
                finalTimeMs = finalTimeMs,
                splitTimesJson = encodeSplitTimes(splitTimesMs)
            )
        )
    }

    suspend fun getComparison(categoryId: Long, segmentCount: Int): SpeedrunComparison {
        val attempts = speedrunDao.getAttemptsForCategory(categoryId)
        val pb = attempts
            .filter { it.completed && it.finalTimeMs != null }
            .minByOrNull { it.finalTimeMs!! }
        val pbSplits = pb?.let { decodeSplitTimes(it.splitTimesJson, segmentCount) }
            ?: List(segmentCount) { null }

        val best = MutableList<Long?>(segmentCount) { null }
        attempts.forEach { attempt ->
            val splits = decodeSplitTimes(attempt.splitTimesJson, segmentCount)
            splits.forEachIndexed { index, cumulative ->
                if (cumulative == null) return@forEachIndexed
                val previous = if (index == 0) 0L else splits[index - 1] ?: return@forEachIndexed
                val duration = cumulative - previous
                val currentBest = best[index]
                if (currentBest == null || duration < currentBest) best[index] = duration
            }
        }
        return SpeedrunComparison(
            pbSplitTimesMs = pbSplits,
            bestSegmentDurationsMs = best,
            attemptCount = attempts.size
        )
    }

    private fun encodeSplitTimes(splits: List<Long?>): String {
        val array = JSONArray()
        splits.forEach { array.put(it ?: org.json.JSONObject.NULL) }
        return array.toString()
    }

    private fun decodeSplitTimes(json: String, segmentCount: Int): List<Long?> = try {
        val array = JSONArray(json)
        List(segmentCount) { index ->
            if (index < array.length() && !array.isNull(index)) array.getLong(index) else null
        }
    } catch (_: Exception) {
        List(segmentCount) { null }
    }
}
