package com.nendo.argosy.libretro.speedrun

import android.os.SystemClock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class SpeedrunPhase { IDLE, RUNNING, PAUSED, FINISHED }

data class SpeedrunRunEnd(
    val categoryId: Long?,
    val startedAtEpochMs: Long,
    val completed: Boolean,
    val finalTimeMs: Long?,
    val splitTimesMs: List<Long?>
)

data class SpeedrunRunState(
    val armed: Boolean = false,
    val categoryId: Long? = null,
    val categoryName: String = "",
    val segments: List<String> = emptyList(),
    val phase: SpeedrunPhase = SpeedrunPhase.IDLE,
    val currentIndex: Int = 0,
    val splitTimesMs: List<Long?> = emptyList(),
    val pbSplitTimesMs: List<Long?> = emptyList(),
    val bestSegmentDurationsMs: List<Long?> = emptyList(),
    val attemptCount: Int = 0,
    val anchorRealtimeMs: Long = 0L,
    val accumulatedMs: Long = 0L,
    val finalTimeMs: Long? = null,
    val startedAtEpochMs: Long = 0L
) {
    fun elapsedAt(nowRealtimeMs: Long): Long = when (phase) {
        SpeedrunPhase.RUNNING -> accumulatedMs + (nowRealtimeMs - anchorRealtimeMs)
        SpeedrunPhase.PAUSED -> accumulatedMs
        SpeedrunPhase.FINISHED -> finalTimeMs ?: accumulatedMs
        SpeedrunPhase.IDLE -> 0L
    }

    val sumOfBestMs: Long? get() =
        if (bestSegmentDurationsMs.isNotEmpty() && bestSegmentDurationsMs.all { it != null }) {
            bestSegmentDurationsMs.filterNotNull().sum()
        } else null
}

/** RTA wall-clock timer state machine; split() starts the run when idle, LiveSplit-style. */
class SpeedrunTimerEngine(
    private val now: () -> Long = SystemClock::elapsedRealtime,
    private val epochNow: () -> Long = System::currentTimeMillis
) {
    private val _state = MutableStateFlow(SpeedrunRunState())
    val state: StateFlow<SpeedrunRunState> = _state.asStateFlow()

    var onRunEnded: ((SpeedrunRunEnd) -> Unit)? = null

    val isArmed: Boolean get() = _state.value.armed

    fun arm(
        categoryId: Long?,
        categoryName: String,
        segments: List<String>,
        pbSplitTimesMs: List<Long?> = emptyList(),
        bestSegmentDurationsMs: List<Long?> = emptyList(),
        attemptCount: Int = 0
    ) {
        if (segments.isEmpty()) return
        endAttemptIfStarted()
        _state.value = SpeedrunRunState(
            armed = true,
            categoryId = categoryId,
            categoryName = categoryName,
            segments = segments,
            splitTimesMs = List(segments.size) { null },
            pbSplitTimesMs = pbSplitTimesMs,
            bestSegmentDurationsMs = bestSegmentDurationsMs,
            attemptCount = attemptCount
        )
    }

    fun disarm() {
        endAttemptIfStarted()
        _state.value = SpeedrunRunState()
    }

    fun split() {
        val s = _state.value
        if (!s.armed) return
        when (s.phase) {
            SpeedrunPhase.IDLE -> startRun()
            SpeedrunPhase.RUNNING -> {
                val elapsed = s.elapsedAt(now())
                val times = s.splitTimesMs.toMutableList().also { it[s.currentIndex] = elapsed }
                _state.value = if (s.currentIndex == s.segments.lastIndex) {
                    s.copy(phase = SpeedrunPhase.FINISHED, splitTimesMs = times, finalTimeMs = elapsed)
                } else {
                    s.copy(currentIndex = s.currentIndex + 1, splitTimesMs = times)
                }
            }
            SpeedrunPhase.PAUSED, SpeedrunPhase.FINISHED -> Unit
        }
    }

    fun undoSplit() {
        val s = _state.value
        when (s.phase) {
            SpeedrunPhase.RUNNING -> if (s.currentIndex > 0) {
                val times = s.splitTimesMs.toMutableList().also { it[s.currentIndex - 1] = null }
                _state.value = s.copy(currentIndex = s.currentIndex - 1, splitTimesMs = times)
            }
            SpeedrunPhase.FINISHED -> {
                val times = s.splitTimesMs.toMutableList().also { it[s.segments.lastIndex] = null }
                _state.value = s.copy(
                    phase = SpeedrunPhase.RUNNING,
                    currentIndex = s.segments.lastIndex,
                    splitTimesMs = times,
                    finalTimeMs = null
                )
            }
            SpeedrunPhase.IDLE, SpeedrunPhase.PAUSED -> Unit
        }
    }

    fun skipSplit() {
        val s = _state.value
        if (s.phase != SpeedrunPhase.RUNNING || s.currentIndex >= s.segments.lastIndex) return
        _state.value = s.copy(currentIndex = s.currentIndex + 1)
    }

    fun togglePause() {
        val s = _state.value
        when (s.phase) {
            SpeedrunPhase.RUNNING -> _state.value = s.copy(
                phase = SpeedrunPhase.PAUSED,
                accumulatedMs = s.accumulatedMs + (now() - s.anchorRealtimeMs)
            )
            SpeedrunPhase.PAUSED -> _state.value = s.copy(
                phase = SpeedrunPhase.RUNNING,
                anchorRealtimeMs = now()
            )
            SpeedrunPhase.IDLE, SpeedrunPhase.FINISHED -> Unit
        }
    }

    fun onGameReset(startOnReset: Boolean) {
        val s = _state.value
        if (!s.armed || !startOnReset) return
        startRun()
    }

    fun resetRun() {
        val s = _state.value
        if (!s.armed) return
        endAttemptIfStarted()
        _state.value = s.copy(
            phase = SpeedrunPhase.IDLE,
            currentIndex = 0,
            splitTimesMs = List(s.segments.size) { null },
            accumulatedMs = 0L,
            finalTimeMs = null
        )
    }

    private fun startRun() {
        endAttemptIfStarted()
        val s = _state.value
        _state.value = s.copy(
            phase = SpeedrunPhase.RUNNING,
            currentIndex = 0,
            splitTimesMs = List(s.segments.size) { null },
            anchorRealtimeMs = now(),
            accumulatedMs = 0L,
            finalTimeMs = null,
            startedAtEpochMs = epochNow(),
            attemptCount = s.attemptCount + 1
        )
    }

    private fun endAttemptIfStarted() {
        val s = _state.value
        if (!s.armed || s.phase == SpeedrunPhase.IDLE) return
        onRunEnded?.invoke(
            SpeedrunRunEnd(
                categoryId = s.categoryId,
                startedAtEpochMs = s.startedAtEpochMs,
                completed = s.phase == SpeedrunPhase.FINISHED,
                finalTimeMs = if (s.phase == SpeedrunPhase.FINISHED) s.finalTimeMs else null,
                splitTimesMs = s.splitTimesMs
            )
        )
    }
}
