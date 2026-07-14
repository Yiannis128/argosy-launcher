package com.nendo.argosy.ui.audio

import android.net.Uri
import android.util.Log
import com.nendo.argosy.data.preferences.UserPreferencesRepository
import com.nendo.argosy.data.remote.romm.ConnectionState
import com.nendo.argosy.data.remote.romm.RomMRepository
import com.nendo.argosy.domain.usecase.music.GameThemeSource
import com.nendo.argosy.domain.usecase.music.ResolveGameThemeUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "GameThemeAudio"

/** Plays a game's title theme as a BGM override while its detail screen is the active one. */
@Singleton
class GameThemeAudioCoordinator @Inject constructor(
    private val ambientAudioManager: AmbientAudioManager,
    private val resolveGameTheme: ResolveGameThemeUseCase,
    private val romMRepository: RomMRepository,
    private val preferencesRepository: UserPreferencesRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var activeGameId: Long? = null
    private var resolveJob: Job? = null

    fun enter(gameId: Long) {
        if (activeGameId == gameId) return
        activeGameId = gameId
        resolveJob?.cancel()
        resolveJob = scope.launch {
            val source = resolveSource(gameId)
            if (activeGameId != gameId) return@launch
            if (source == null) {
                ambientAudioManager.clearOverride()
            } else {
                Log.d(TAG, "Playing theme for game $gameId: ${source.displayName}")
                ambientAudioManager.playOverride(source)
            }
        }
    }

    private suspend fun resolveSource(gameId: Long): AmbientOverrideSource? {
        val prefs = preferencesRepository.userPreferences.first()
        if (!prefs.gameDetailThemeEnabled || !prefs.ambientAudioEnabled) return null
        return when (val theme = resolveGameTheme(gameId)) {
            null -> null
            is GameThemeSource.Local -> AmbientOverrideSource.Local(theme.path, theme.title)
            is GameThemeSource.Stream -> buildRemoteSource(theme, prefs.rommToken)
        }
    }

    fun exit(gameId: Long) {
        if (activeGameId != gameId) return
        activeGameId = null
        resolveJob?.cancel()
        resolveJob = null
        ambientAudioManager.clearOverride()
    }

    private fun buildRemoteSource(
        theme: GameThemeSource.Stream,
        token: String?
    ): AmbientOverrideSource.Remote? {
        if (romMRepository.connectionState.value !is ConnectionState.Connected) return null
        val url = romMRepository.buildMediaUrlPublic(
            "/api/roms/${theme.rommFileId}/files/content/${Uri.encode(theme.fileName)}"
        )
        val headers = token?.let { mapOf("Authorization" to "Bearer $it") } ?: emptyMap()
        return AmbientOverrideSource.Remote(url, headers, theme.title)
    }
}
