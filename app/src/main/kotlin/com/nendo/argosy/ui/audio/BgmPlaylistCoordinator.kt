package com.nendo.argosy.ui.audio

import com.nendo.argosy.data.local.entity.BgmPlaylistEntity
import com.nendo.argosy.data.music.BgmPlaylistRepository
import com.nendo.argosy.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Bridges the bgm_playlist table to AmbientAudioManager while the BGM source is
 * AMBIENT_SOURCE_PLAYLIST, and owns playlist mutations from UI and download flows.
 */
@Singleton
class BgmPlaylistCoordinator @Inject constructor(
    private val repository: BgmPlaylistRepository,
    private val ambientAudioManager: AmbientAudioManager,
    private val preferencesRepository: UserPreferencesRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var watchJob: Job? = null

    val entries: Flow<List<BgmPlaylistEntity>> = repository.observeAll()

    suspend fun activate() {
        val paths = repository.getOrderedPaths()
        withContext(Dispatchers.Main) { ambientAudioManager.setPlaylistSource(paths) }
        startWatching()
    }

    fun deactivate() {
        watchJob?.cancel()
        watchJob = null
    }

    /** Adds a track; makes the playlist the BGM source only when no source is set. */
    suspend fun add(filePath: String, displayName: String, gameFileId: Long? = null) {
        repository.add(filePath, displayName, gameFileId)
        when (preferencesRepository.preferences.first().ambientAudioUri) {
            null -> preferencesRepository.setAmbientAudioUri(AmbientAudioManager.AMBIENT_SOURCE_PLAYLIST)
            AmbientAudioManager.AMBIENT_SOURCE_PLAYLIST -> {
                val paths = repository.getOrderedPaths()
                withContext(Dispatchers.Main) {
                    ambientAudioManager.setPlaylistSource(paths)
                    ambientAudioManager.fadeIn()
                }
            }
            else -> {}
        }
    }

    suspend fun remove(filePath: String) = repository.remove(filePath)

    suspend fun removeById(id: Long) = repository.removeById(id)

    suspend fun reorder(orderedIds: List<Long>) = repository.reorder(orderedIds)

    private fun startWatching() {
        if (watchJob?.isActive == true) return
        watchJob = scope.launch {
            repository.observeAll().drop(1).collect { rows ->
                ambientAudioManager.setPlaylistSource(rows.map { it.filePath })
            }
        }
    }
}
