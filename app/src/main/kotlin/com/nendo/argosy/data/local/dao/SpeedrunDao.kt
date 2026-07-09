package com.nendo.argosy.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.nendo.argosy.data.local.entity.SpeedrunAttemptEntity
import com.nendo.argosy.data.local.entity.SpeedrunCategoryEntity
import com.nendo.argosy.data.local.entity.SpeedrunSegmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SpeedrunDao {
    @Query("SELECT * FROM speedrun_categories WHERE gameId = :gameId ORDER BY createdAt")
    fun observeCategoriesForGame(gameId: Long): Flow<List<SpeedrunCategoryEntity>>

    @Query("SELECT * FROM speedrun_categories WHERE gameId = :gameId ORDER BY createdAt")
    suspend fun getCategoriesForGame(gameId: Long): List<SpeedrunCategoryEntity>

    @Query("SELECT * FROM speedrun_categories WHERE id = :categoryId")
    suspend fun getCategory(categoryId: Long): SpeedrunCategoryEntity?

    @Insert
    suspend fun insertCategory(category: SpeedrunCategoryEntity): Long

    @Update
    suspend fun updateCategory(category: SpeedrunCategoryEntity)

    @Query("DELETE FROM speedrun_categories WHERE id = :categoryId")
    suspend fun deleteCategory(categoryId: Long)

    @Query("SELECT * FROM speedrun_segments WHERE categoryId = :categoryId ORDER BY orderIndex")
    suspend fun getSegmentsForCategory(categoryId: Long): List<SpeedrunSegmentEntity>

    @Insert
    suspend fun insertSegments(segments: List<SpeedrunSegmentEntity>)

    @Query("DELETE FROM speedrun_segments WHERE categoryId = :categoryId")
    suspend fun deleteSegmentsForCategory(categoryId: Long)

    @Insert
    suspend fun insertAttempt(attempt: SpeedrunAttemptEntity): Long

    @Query("SELECT * FROM speedrun_attempts WHERE categoryId = :categoryId ORDER BY startedAt")
    suspend fun getAttemptsForCategory(categoryId: Long): List<SpeedrunAttemptEntity>

    @Query("SELECT COUNT(*) FROM speedrun_attempts WHERE categoryId = :categoryId")
    suspend fun getAttemptCount(categoryId: Long): Int
}
