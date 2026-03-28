package com.nendo.argosy.data.steam

import com.nendo.argosy.data.local.dao.SteamDownloadTrackingDao
import com.nendo.argosy.data.local.entity.SteamCompletedDepotEntity
import com.nendo.argosy.data.local.entity.SteamCompletedFileEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedQueue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SteamDownloadTracker @Inject constructor(
    private val trackingDao: SteamDownloadTrackingDao
) {
    private val fileBuffer = ConcurrentLinkedQueue<SteamCompletedFileEntity>()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var flushJob: Job? = null

    fun onFileCompleted(appId: Long, depotId: Int, manifestId: Long, fileName: String) {
        fileBuffer.add(
            SteamCompletedFileEntity(
                appId = appId,
                depotId = depotId,
                manifestId = manifestId,
                fileName = fileName
            )
        )
        if (fileBuffer.size >= 100) {
            scope.launch { flushFileBuffer() }
        }
    }

    fun onDepotCompleted(appId: Long, depotId: Int, manifestId: Long) {
        scope.launch {
            flushFileBuffer()
            trackingDao.insertCompletedDepot(
                SteamCompletedDepotEntity(
                    appId = appId,
                    depotId = depotId,
                    manifestId = manifestId
                )
            )
        }
    }

    fun startPeriodicFlush() {
        flushJob?.cancel()
        flushJob = scope.launch {
            while (true) {
                delay(5000)
                flushFileBuffer()
            }
        }
    }

    fun stopPeriodicFlush() {
        flushJob?.cancel()
        flushJob = null
    }

    suspend fun flushNow() {
        flushFileBuffer()
    }

    suspend fun getCompletedFileNames(appId: Long): Set<String> {
        return trackingDao.getCompletedFileNames(appId).toHashSet()
    }

    suspend fun getCompletedDepotIds(appId: Long): Set<Int> {
        return trackingDao.getCompletedDepotIds(appId).toHashSet()
    }

    suspend fun clearForApp(appId: Long) {
        trackingDao.clearFilesForApp(appId)
        trackingDao.clearDepotsForApp(appId)
    }

    private suspend fun flushFileBuffer() {
        val batch = mutableListOf<SteamCompletedFileEntity>()
        while (true) {
            val item = fileBuffer.poll() ?: break
            batch.add(item)
        }
        if (batch.isNotEmpty()) {
            trackingDao.insertCompletedFiles(batch)
        }
    }
}
