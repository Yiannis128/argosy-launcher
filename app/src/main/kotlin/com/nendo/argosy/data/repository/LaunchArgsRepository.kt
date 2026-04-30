package com.nendo.argosy.data.repository

import com.nendo.argosy.data.local.dao.EmulatorLaunchArgsDao
import com.nendo.argosy.data.local.entity.EmulatorLaunchArgsEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Settings UI facade over [EmulatorLaunchArgsDao] for per-platform/per-emulator
 * launch argument overrides (intent flags, MIME type, ROM binding, etc.).
 */
@Singleton
class LaunchArgsRepository @Inject constructor(
    private val emulatorLaunchArgsDao: EmulatorLaunchArgsDao
) {
    suspend fun getByPlatformAndEmulator(
        platformId: Long,
        emulatorId: String
    ): EmulatorLaunchArgsEntity? =
        emulatorLaunchArgsDao.getByPlatformAndEmulator(platformId, emulatorId)

    fun observeByPlatformAndEmulator(
        platformId: Long,
        emulatorId: String
    ): Flow<EmulatorLaunchArgsEntity?> =
        emulatorLaunchArgsDao.observeByPlatformAndEmulator(platformId, emulatorId)

    suspend fun upsert(entity: EmulatorLaunchArgsEntity) =
        emulatorLaunchArgsDao.upsert(entity)

    suspend fun deleteByPlatformAndEmulator(platformId: Long, emulatorId: String) =
        emulatorLaunchArgsDao.deleteByPlatformAndEmulator(platformId, emulatorId)

    suspend fun hasOverride(platformId: Long, emulatorId: String): Boolean =
        emulatorLaunchArgsDao.hasOverride(platformId, emulatorId)
}
