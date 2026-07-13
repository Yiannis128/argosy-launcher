package com.nendo.argosy.data.music

import com.nendo.argosy.data.local.dao.BgmPlaylistDao
import com.nendo.argosy.data.local.entity.BgmPlaylistEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BgmPlaylistRepository @Inject constructor(
    private val bgmPlaylistDao: BgmPlaylistDao
) {
    fun observeAll(): Flow<List<BgmPlaylistEntity>> = bgmPlaylistDao.observeAll()

    suspend fun getOrderedPaths(): List<String> = withContext(Dispatchers.IO) {
        bgmPlaylistDao.getAll().map { it.filePath }
    }

    /** Appends a track at the end of the playlist; no-op if the path is already present. */
    suspend fun add(filePath: String, displayName: String, gameFileId: Long? = null): Boolean =
        withContext(Dispatchers.IO) {
            if (bgmPlaylistDao.exists(filePath)) return@withContext false
            val position = (bgmPlaylistDao.getMaxPosition() ?: -1) + 1
            bgmPlaylistDao.insert(
                BgmPlaylistEntity(
                    position = position,
                    filePath = filePath,
                    displayName = displayName,
                    gameFileId = gameFileId
                )
            )
            true
        }

    suspend fun remove(filePath: String) = withContext(Dispatchers.IO) {
        bgmPlaylistDao.deleteByPath(filePath)
    }

    suspend fun removeById(id: Long) = withContext(Dispatchers.IO) {
        bgmPlaylistDao.deleteById(id)
    }

    suspend fun reorder(orderedIds: List<Long>) = withContext(Dispatchers.IO) {
        bgmPlaylistDao.updatePositions(orderedIds)
    }
}
