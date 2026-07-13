package com.nendo.argosy.ui.audio

import android.util.Log
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
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "BgmPlaylistCoordinator"

/**
 * Bridges the bgm_playlist table (the single BGM source) to AmbientAudioManager
 * and owns playlist mutations from UI and download flows.
 */
@Singleton
class BgmPlaylistCoordinator @Inject constructor(
    private val repository: BgmPlaylistRepository,
    private val ambientAudioManager: AmbientAudioManager,
    private val preferencesRepository: UserPreferencesRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var watchJob: Job? = null
    private var activated = false

    val entries: Flow<List<BgmPlaylistEntity>> = repository.observeAll()

    suspend fun activate() {
        if (activated) return
        activated = true
        importLegacySource()
        pushPlaylist()
        startWatching()
    }

    /** Adds a downloaded track as a file entry and fades it in when BGM is enabled. */
    suspend fun add(filePath: String, displayName: String, gameFileId: Long? = null) {
        if (repository.addFile(filePath, displayName, gameFileId)) {
            pushPlaylist()
            withContext(Dispatchers.Main) { ambientAudioManager.fadeIn() }
        }
    }

    /** Adds a local path picked in the file browser, as a folder or file entry by type. */
    suspend fun addLocalPath(path: String) {
        val added = withContext(Dispatchers.IO) {
            val file = File(path)
            if (file.isDirectory) repository.addFolder(path)
            else repository.addFile(path, file.nameWithoutExtension)
        }
        if (added) {
            pushPlaylist()
            withContext(Dispatchers.Main) { ambientAudioManager.fadeIn() }
        }
    }

    suspend fun remove(filePath: String) = repository.remove(filePath)

    suspend fun removeById(id: Long) = repository.removeById(id)

    suspend fun reorder(orderedIds: List<Long>) = repository.reorder(orderedIds)

    private suspend fun pushPlaylist() {
        val paths = repository.resolvePlaybackPaths()
        withContext(Dispatchers.Main) {
            ambientAudioManager.setPlaylistSource(paths) { repository.resolvePlaybackPaths() }
        }
    }

    private suspend fun importLegacySource() = withContext(Dispatchers.IO) {
        val uri = preferencesRepository.preferences.first().ambientAudioUri ?: return@withContext
        if (uri == AmbientAudioManager.AMBIENT_SOURCE_PLAYLIST) return@withContext
        if (uri.startsWith("/")) {
            val file = File(uri)
            when {
                file.isDirectory -> repository.addFolder(uri)
                file.isFile -> repository.addFile(uri, file.nameWithoutExtension)
                else -> Log.w(TAG, "Skipping legacy BGM import, path missing: $uri")
            }
        } else {
            Log.w(TAG, "Skipping legacy BGM import, unsupported uri: $uri")
        }
        preferencesRepository.setAmbientAudioUri(AmbientAudioManager.AMBIENT_SOURCE_PLAYLIST)
    }

    private fun startWatching() {
        if (watchJob?.isActive == true) return
        watchJob = scope.launch {
            repository.observeAll().drop(1).collect { pushPlaylist() }
        }
    }
}
