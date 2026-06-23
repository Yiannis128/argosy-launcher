package com.nendo.argosy.data.repository

import android.util.Log
import androidx.room.withTransaction
import com.nendo.argosy.data.cache.ImageCacheManager
import com.nendo.argosy.data.local.ALauncherDatabase
import com.nendo.argosy.data.model.GameSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "DatabaseAdminRepository"

@Singleton
class DatabaseAdminRepository @Inject constructor(
    private val database: ALauncherDatabase,
    private val imageCacheManager: ImageCacheManager
) {
    suspend fun purgeAll() = withContext(Dispatchers.IO) {
        database.clearAllTables()
        imageCacheManager.clearCache()
    }

    /**
     * Wipes all RomM-sourced library content (games, platforms, collections, saves/states sync rows)
     * so a server switch starts clean. Preserves Steam/Android/local games, input config, cores, and settings.
     */
    suspend fun purgeRomMLibrary() = withContext(Dispatchers.IO) {
        val sources = listOf(GameSource.ROMM_REMOTE, GameSource.ROMM_SYNCED)
        val sourceNames = sources.map { it.name }

        for (game in database.gameDao().getDownloadedBySources(sources)) {
            val path = game.localPath ?: continue
            try {
                val file = File(path)
                if (file.exists()) {
                    if (file.isDirectory) file.deleteRecursively() else file.delete()
                }
            } catch (e: Exception) {
                Log.e(TAG, "purgeRomMLibrary: failed to delete $path: ${e.message}")
            }
        }

        database.withTransaction {
            database.saveSyncDao().deleteByGameSources(sourceNames)
            database.saveCacheDao().deleteByGameSources(sourceNames)
            database.stateCacheDao().deleteByGameSources(sourceNames)
            database.stateTombstoneDao().deleteByGameSources(sourceNames)
            database.pendingConflictDao().deleteByGameSources(sourceNames)
            database.pendingSyncQueueDao().deleteByGameSources(sourceNames)
            database.playSessionDao().deleteByGameSources(sourceNames)
            database.downloadQueueDao().deleteByGameSources(sourceNames)
            database.collectionDao().deleteRomMSynced()
            database.gameDao().deleteBySources(sources)
            database.platformDao().deleteEmptyPlatforms()
            database.pinnedCollectionDao().deleteOrphaned()
        }

        imageCacheManager.clearCache()
    }
}
