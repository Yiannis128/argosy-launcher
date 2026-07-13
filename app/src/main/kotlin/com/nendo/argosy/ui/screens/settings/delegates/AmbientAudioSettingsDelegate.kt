package com.nendo.argosy.ui.screens.settings.delegates

import com.nendo.argosy.data.local.entity.BgmPlaylistEntity
import com.nendo.argosy.data.preferences.UserPreferencesRepository
import com.nendo.argosy.ui.audio.AmbientAudioManager
import com.nendo.argosy.ui.audio.BgmPlaylistCoordinator
import com.nendo.argosy.ui.screens.settings.AmbientAudioState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class AmbientAudioSettingsDelegate @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository,
    private val ambientAudioManager: AmbientAudioManager,
    private val playlistCoordinator: BgmPlaylistCoordinator
) {
    private val _state = MutableStateFlow(AmbientAudioState())
    val state: StateFlow<AmbientAudioState> = _state.asStateFlow()

    private val _openPlaylistManagerEvent = MutableSharedFlow<Unit>()
    val openPlaylistManagerEvent: SharedFlow<Unit> = _openPlaylistManagerEvent.asSharedFlow()

    private val _openMusicBrowserEvent = MutableSharedFlow<Unit>()
    val openMusicBrowserEvent: SharedFlow<Unit> = _openMusicBrowserEvent.asSharedFlow()

    private val _openAddMusicBrowserEvent = MutableSharedFlow<Unit>()
    val openAddMusicBrowserEvent: SharedFlow<Unit> = _openAddMusicBrowserEvent.asSharedFlow()

    fun initFlowCollection(scope: CoroutineScope) {
        scope.launch {
            ambientAudioManager.currentTrackName.collect { trackName ->
                _state.update { it.copy(currentTrackName = trackName) }
            }
        }
        scope.launch {
            playlistCoordinator.entries.collect { entries ->
                val trackCount = entries.count { it.entryType != BgmPlaylistEntity.TYPE_FOLDER && it.enabled }
                _state.update { it.copy(playlistEntryCount = trackCount) }
            }
        }
    }

    fun updateState(newState: AmbientAudioState) {
        _state.value = newState
    }

    fun setEnabled(scope: CoroutineScope, enabled: Boolean) {
        scope.launch {
            preferencesRepository.setAmbientAudioEnabled(enabled)
            ambientAudioManager.setEnabled(enabled)
            _state.update { it.copy(enabled = enabled) }

            if (enabled) {
                ambientAudioManager.fadeIn()
            } else {
                ambientAudioManager.fadeOut()
            }
        }
    }

    fun setVolume(scope: CoroutineScope, volume: Int) {
        scope.launch {
            preferencesRepository.setAmbientAudioVolume(volume)
            ambientAudioManager.setVolume(volume)
            _state.update { it.copy(volume = volume) }
        }
    }

    fun adjustVolume(scope: CoroutineScope, delta: Int) {
        adjustInList(_state.value.volume, VolumeLevels.AMBIENT_AUDIO, delta)?.let { setVolume(scope, it) }
    }

    fun setShuffle(scope: CoroutineScope, shuffle: Boolean) {
        scope.launch {
            preferencesRepository.setAmbientAudioShuffle(shuffle)
            ambientAudioManager.setShuffle(shuffle)
            _state.update { it.copy(shuffle = shuffle) }
        }
    }

    fun addPlaylistEntry(scope: CoroutineScope, path: String) {
        scope.launch {
            playlistCoordinator.addLocalPath(path)
        }
    }

    fun openPlaylistManager(scope: CoroutineScope) {
        scope.launch {
            _openPlaylistManagerEvent.emit(Unit)
        }
    }

    fun openAddMusicBrowser(scope: CoroutineScope) {
        scope.launch {
            _openAddMusicBrowserEvent.emit(Unit)
        }
    }

    fun openMusicBrowser(scope: CoroutineScope) {
        scope.launch {
            _openMusicBrowserEvent.emit(Unit)
        }
    }
}
