package com.nendo.argosy.data.netplay

import android.util.Log
import com.nendo.argosy.libretro.CheatSessionManager
import com.nendo.argosy.libretro.RetroAchievementsSessionManager
import com.swordfish.libretrodroid.GLRetroView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NetplaySessionRules(
    private val retroView: GLRetroView,
    private val raSessionManager: () -> RetroAchievementsSessionManager?,
    private val onFastForwardRelease: (() -> Unit)? = null
) {

    var cheatSessionManager: CheatSessionManager? = null

    enum class Role { Host, Guest }

    data class ApplyContext(val role: Role)

    private val _isInNetplaySession = MutableStateFlow(false)
    val isInNetplaySession: StateFlow<Boolean> = _isInNetplaySession.asStateFlow()

    private var savedBlackFrameInsertion: Boolean? = null
    private var applied: Boolean = false

    suspend fun apply(context: ApplyContext) {
        if (applied) {
            Log.w(TAG, "apply() called while already active; ignoring")
            return
        }
        if (context.role == Role.Host) {
            cheatSessionManager?.disableAllAndCycleForNetplay()
        }
        raSessionManager()?.pause()
        savedBlackFrameInsertion = retroView.blackFrameInsertion
        retroView.blackFrameInsertion = false
        retroView.frameSpeed = 1
        onFastForwardRelease?.invoke()
        applied = true
        _isInNetplaySession.value = true
        Log.d(TAG, "netplay online-mode rules applied (role=${context.role})")
    }

    fun release() {
        if (!applied) return
        raSessionManager()?.resume()
        savedBlackFrameInsertion?.let { retroView.blackFrameInsertion = it }
        savedBlackFrameInsertion = null
        applied = false
        _isInNetplaySession.value = false
        Log.d(TAG, "netplay online-mode rules released")
    }

    companion object {
        private const val TAG = "NetplaySessionRules"
    }
}
