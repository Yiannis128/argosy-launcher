package com.nendo.argosy.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nendo.argosy.data.local.entity.SteamAccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SteamAccountDao {

    @Query("SELECT * FROM steam_accounts WHERE isActive = 1 LIMIT 1")
    fun observeActiveAccount(): Flow<SteamAccountEntity?>

    @Query("SELECT * FROM steam_accounts WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveAccount(): SteamAccountEntity?

    @Query("SELECT * FROM steam_accounts WHERE refreshToken IS NOT NULL AND refreshToken != '' ORDER BY lastLoginAt DESC LIMIT 1")
    suspend fun getAnyAccount(): SteamAccountEntity?

    @Query("SELECT * FROM steam_accounts WHERE steamId = :steamId")
    suspend fun getBySteamId(steamId: Long): SteamAccountEntity?

    @Query("SELECT * FROM steam_accounts")
    suspend fun getAll(): List<SteamAccountEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: SteamAccountEntity): Long

    @Update
    suspend fun update(account: SteamAccountEntity)

    @Query("UPDATE steam_accounts SET isActive = 0")
    suspend fun deactivateAll()

    @Query("UPDATE steam_accounts SET accessToken = :token, accessTokenExpiry = :expiry WHERE id = :id")
    suspend fun updateAccessToken(id: Long, token: String, expiry: Long)

    @Query("DELETE FROM steam_accounts WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM steam_accounts")
    suspend fun deleteAll()
}
