package com.nendo.argosy.data.steam

import com.nendo.argosy.data.local.dao.GameDao
import com.nendo.argosy.ui.components.SteamDownloadLocationPrompt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SteamDownloadPromptController @Inject constructor(
    private val gameDao: GameDao,
    private val steamContentManager: SteamContentManager
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _prompt = MutableStateFlow<SteamDownloadLocationPrompt?>(null)
    val prompt: StateFlow<SteamDownloadLocationPrompt?> = _prompt.asStateFlow()

    private val _focusIndex = MutableStateFlow(0)
    val focusIndex: StateFlow<Int> = _focusIndex.asStateFlow()

    fun requestSteamDownload(gameId: Long) {
        scope.launch {
            val game = gameDao.getById(gameId) ?: return@launch
            if (game.steamAppId == null) return@launch
            _focusIndex.value = 0
            _prompt.value = SteamDownloadLocationPrompt(
                gameId = gameId,
                title = game.title,
                coverPath = game.coverPath
            )
        }
    }

    fun moveFocus(delta: Int) {
        val next = (_focusIndex.value + delta).coerceIn(0, 1)
        _focusIndex.value = next
    }

    fun setFocus(index: Int) {
        _focusIndex.value = index.coerceIn(0, 1)
    }

    fun confirmDownloadToSd() {
        val p = _prompt.value ?: return
        scope.launch {
            val game = gameDao.getById(p.gameId) ?: return@launch
            val steamAppId = game.steamAppId ?: return@launch
            if (game.isManagedByGn) {
                gameDao.updateManagedByGn(p.gameId, false)
            }
            steamContentManager.queueDownloadOptimistic(steamAppId, game.title, game.coverPath)
            _prompt.value = null
        }
    }

    fun confirmManagedByGn() {
        val p = _prompt.value ?: return
        scope.launch {
            gameDao.updateManagedByGn(p.gameId, true)
            _prompt.value = null
        }
    }

    fun dismiss() {
        _prompt.value = null
    }
}
