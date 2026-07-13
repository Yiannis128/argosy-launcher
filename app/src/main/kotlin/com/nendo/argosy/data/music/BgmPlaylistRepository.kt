package com.nendo.argosy.data.music

import com.nendo.argosy.core.media.AudioFileTypes
import com.nendo.argosy.data.local.dao.BgmPlaylistDao
import com.nendo.argosy.data.local.entity.BgmPlaylistEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BgmPlaylistRepository @Inject constructor(
    private val bgmPlaylistDao: BgmPlaylistDao
) {
    fun observeAll(): Flow<List<BgmPlaylistEntity>> = bgmPlaylistDao.observeAll()

    /** Appends a track at the end of the playlist; no-op if the path is already present. */
    suspend fun addFile(filePath: String, displayName: String, gameFileId: Long? = null): Boolean =
        withContext(Dispatchers.IO) {
            if (bgmPlaylistDao.exists(filePath)) return@withContext false
            val position = (bgmPlaylistDao.getMaxPosition() ?: -1) + 1
            bgmPlaylistDao.insert(
                BgmPlaylistEntity(
                    position = position,
                    filePath = filePath,
                    displayName = displayName,
                    gameFileId = gameFileId,
                    entryType = BgmPlaylistEntity.TYPE_FILE
                )
            )
            true
        }

    /** Appends a folder entry whose tracks are expanded at playback time; no-op if already present. */
    suspend fun addFolder(folderPath: String): Boolean = withContext(Dispatchers.IO) {
        if (bgmPlaylistDao.exists(folderPath)) return@withContext false
        val position = (bgmPlaylistDao.getMaxPosition() ?: -1) + 1
        bgmPlaylistDao.insert(
            BgmPlaylistEntity(
                position = position,
                filePath = folderPath,
                displayName = folderPath.trimEnd('/').substringAfterLast('/'),
                gameFileId = null,
                entryType = BgmPlaylistEntity.TYPE_FOLDER
            )
        )
        true
    }

    /** Expands playlist entries in position order into playable file paths, walking folder entries live. */
    suspend fun resolvePlaybackPaths(): List<String> = withContext(Dispatchers.IO) {
        val collected = LinkedHashSet<String>()
        bgmPlaylistDao.getAll().forEach { entry ->
            when (entry.entryType) {
                BgmPlaylistEntity.TYPE_FOLDER -> {
                    val folder = File(entry.filePath)
                    if (folder.isDirectory) {
                        folder.walkTopDown()
                            .filter { it.isFile && it.extension.lowercase() in AudioFileTypes.EXTENSIONS }
                            .map { it.absolutePath }
                            .sorted()
                            .forEach { collected.add(it) }
                    }
                }
                else -> {
                    if (File(entry.filePath).isFile) collected.add(entry.filePath)
                }
            }
        }
        collected.toList()
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
