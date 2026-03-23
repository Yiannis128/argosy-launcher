package com.nendo.argosy.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nendo.argosy.data.local.entity.SteamLicenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SteamLicenseDao {

    @Query("SELECT * FROM steam_licenses WHERE accountId = :accountId")
    fun observeByAccount(accountId: Long): Flow<List<SteamLicenseEntity>>

    @Query("SELECT * FROM steam_licenses WHERE accountId = :accountId")
    suspend fun getByAccount(accountId: Long): List<SteamLicenseEntity>

    @Query("SELECT * FROM steam_licenses WHERE packageId = :packageId AND accountId = :accountId")
    suspend fun getByPackageId(packageId: Int, accountId: Long): SteamLicenseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(licenses: List<SteamLicenseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(license: SteamLicenseEntity)

    @Query("DELETE FROM steam_licenses WHERE accountId = :accountId")
    suspend fun deleteByAccount(accountId: Long)

    @Query("DELETE FROM steam_licenses")
    suspend fun deleteAll()
}
