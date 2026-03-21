package com.nendo.argosy.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nendo.argosy.data.local.entity.CheatEntity
import com.nendo.argosy.data.local.entity.CheatVariantTuple
import com.nendo.argosy.data.local.entity.EnabledCheatKey
import kotlinx.coroutines.flow.Flow

@Dao
interface CheatDao {

    @Query("SELECT * FROM cheats WHERE gameId = :gameId ORDER BY cheatIndex ASC")
    suspend fun getCheatsForGame(gameId: Long): List<CheatEntity>

    @Query("SELECT * FROM cheats WHERE gameId = :gameId ORDER BY cheatIndex ASC")
    fun observeCheatsForGame(gameId: Long): Flow<List<CheatEntity>>

    @Query("SELECT * FROM cheats WHERE gameId = :gameId AND enabled = 1 ORDER BY cheatIndex ASC")
    suspend fun getEnabledCheats(gameId: Long): List<CheatEntity>

    @Query("SELECT COUNT(*) FROM cheats WHERE gameId = :gameId")
    suspend fun getCheatCount(gameId: Long): Int

    @Query("SELECT MAX(cheatIndex) FROM cheats WHERE gameId = :gameId")
    suspend fun getMaxCheatIndex(gameId: Long): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cheat: CheatEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cheats: List<CheatEntity>)

    @Query("UPDATE cheats SET enabled = :enabled, lastUsedAt = :timestamp WHERE id = :id")
    suspend fun setEnabled(id: Long, enabled: Boolean, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE cheats SET enabled = :enabled WHERE gameId = :gameId")
    suspend fun setAllEnabled(gameId: Long, enabled: Boolean)

    @Query("DELETE FROM cheats WHERE gameId = :gameId")
    suspend fun deleteByGameId(gameId: Long)

    @Query("DELETE FROM cheats WHERE gameId = :gameId AND isUserCreated = 0")
    suspend fun deleteServerCheats(gameId: Long)

    @Query("SELECT COUNT(*) FROM cheats WHERE gameId = :gameId AND isUserCreated = 0")
    suspend fun getServerCheatCount(gameId: Long): Int

    @Query("SELECT cheatIndex FROM cheats WHERE gameId = :gameId AND isUserCreated = 0 AND enabled = 1")
    suspend fun getEnabledServerCheatIndices(gameId: Long): List<Int>

    @Query("SELECT variantRegion, variantVersion, cheatIndex FROM cheats WHERE gameId = :gameId AND isUserCreated = 0 AND enabled = 1")
    suspend fun getEnabledServerCheatKeys(gameId: Long): List<EnabledCheatKey>

    @Query("DELETE FROM cheats WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE cheats SET description = :description WHERE id = :id")
    suspend fun updateDescription(id: Long, description: String)

    @Query("UPDATE cheats SET code = :code WHERE id = :id")
    suspend fun updateCode(id: Long, code: String)

    @Query("UPDATE cheats SET description = :description, code = :code WHERE id = :id")
    suspend fun updateCheat(id: Long, description: String, code: String)

    @Query("""
        SELECT DISTINCT variantRegion, variantVersion, COUNT(*) as cheatCount
        FROM cheats
        WHERE gameId = :gameId AND isUserCreated = 0 AND variantRegion IS NOT NULL
        GROUP BY variantRegion, variantVersion
        ORDER BY cheatCount DESC
    """)
    suspend fun getVariantsForGame(gameId: Long): List<CheatVariantTuple>

    @Query("""
        SELECT * FROM cheats
        WHERE gameId = :gameId
          AND ((variantRegion = :region AND variantVersion = :version) OR isUserCreated = 1)
        ORDER BY isUserCreated ASC, cheatIndex ASC
    """)
    suspend fun getCheatsForVariant(gameId: Long, region: String, version: String): List<CheatEntity>
}
