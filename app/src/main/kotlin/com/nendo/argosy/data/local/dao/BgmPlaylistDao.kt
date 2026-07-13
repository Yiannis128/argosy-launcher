package com.nendo.argosy.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.nendo.argosy.data.local.entity.BgmPlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BgmPlaylistDao {

    @Query("SELECT * FROM bgm_playlist ORDER BY position ASC")
    fun observeAll(): Flow<List<BgmPlaylistEntity>>

    @Query("SELECT * FROM bgm_playlist ORDER BY position ASC")
    suspend fun getAll(): List<BgmPlaylistEntity>

    @Query("SELECT * FROM bgm_playlist WHERE filePath = :filePath")
    suspend fun getByPath(filePath: String): BgmPlaylistEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM bgm_playlist WHERE filePath = :filePath)")
    suspend fun exists(filePath: String): Boolean

    @Query("SELECT MAX(position) FROM bgm_playlist")
    suspend fun getMaxPosition(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: BgmPlaylistEntity): Long

    @Query("DELETE FROM bgm_playlist WHERE filePath = :filePath")
    suspend fun deleteByPath(filePath: String)

    @Query("DELETE FROM bgm_playlist WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE bgm_playlist SET position = :position WHERE id = :id")
    suspend fun updatePosition(id: Long, position: Int)

    @Transaction
    suspend fun updatePositions(orderedIds: List<Long>) {
        orderedIds.forEachIndexed { index, id -> updatePosition(id, index) }
    }
}
