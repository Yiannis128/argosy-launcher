package com.nendo.argosy.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nendo.argosy.data.local.entity.CoreVersionHistoryEntity

@Dao
interface CoreVersionHistoryDao {
    @Insert
    suspend fun insert(entity: CoreVersionHistoryEntity)

    @Query("SELECT * FROM core_version_history WHERE coreId = :coreId ORDER BY archivedAt DESC")
    suspend fun getByCore(coreId: String): List<CoreVersionHistoryEntity>

    @Query("SELECT * FROM core_version_history WHERE coreId = :coreId AND version = :version LIMIT 1")
    suspend fun getByCoreAndVersion(coreId: String, version: String): CoreVersionHistoryEntity?

    @Query("DELETE FROM core_version_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM core_version_history WHERE coreId = :coreId")
    suspend fun deleteByCore(coreId: String)
}
