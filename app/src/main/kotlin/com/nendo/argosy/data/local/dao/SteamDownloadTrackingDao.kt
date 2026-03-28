package com.nendo.argosy.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nendo.argosy.data.local.entity.SteamCompletedDepotEntity
import com.nendo.argosy.data.local.entity.SteamCompletedFileEntity

@Dao
interface SteamDownloadTrackingDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCompletedFiles(entities: List<SteamCompletedFileEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCompletedDepot(entity: SteamCompletedDepotEntity)

    @Query("SELECT fileName FROM steam_completed_files WHERE appId = :appId")
    suspend fun getCompletedFileNames(appId: Long): List<String>

    @Query("SELECT depotId FROM steam_completed_depots WHERE appId = :appId")
    suspend fun getCompletedDepotIds(appId: Long): List<Int>

    @Query("DELETE FROM steam_completed_files WHERE appId = :appId")
    suspend fun clearFilesForApp(appId: Long)

    @Query("DELETE FROM steam_completed_depots WHERE appId = :appId")
    suspend fun clearDepotsForApp(appId: Long)
}
