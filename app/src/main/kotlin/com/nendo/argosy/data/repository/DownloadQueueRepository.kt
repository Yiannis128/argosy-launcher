package com.nendo.argosy.data.repository

import com.nendo.argosy.data.local.dao.DownloadQueueDao
import com.nendo.argosy.data.local.entity.DownloadQueueEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Thin pass-through wrapper around [DownloadQueueDao] so UI code never has to
 * inject the DAO directly. Add methods here as new query needs surface.
 */
@Singleton
class DownloadQueueRepository @Inject constructor(
    private val downloadQueueDao: DownloadQueueDao
) {
    fun observeActiveDownloads(): Flow<List<DownloadQueueEntity>> =
        downloadQueueDao.observeActiveDownloads()
}
