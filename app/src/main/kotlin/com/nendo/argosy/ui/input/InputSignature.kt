package com.nendo.argosy.ui.input

import android.view.KeyEvent
import android.view.MotionEvent

/**
 * Identity of a single raw Android input event. Two copies of the same physical event
 * (e.g. delivered through both the primary and companion activity dispatch paths)
 * will produce equal signatures, which allows [InputDedupBuffer] to drop the duplicate.
 *
 * [eventTime] is the monotonic uptime (ms) assigned by Android when the event was
 * created; the remaining fields protect against coincidental ties (multiple gamepads,
 * ACTION_UP/ACTION_DOWN arriving at the same tick, or synthetic events).
 */
data class InputSignature(
    val eventTime: Long,
    val deviceId: Int,
    val keyCode: Int,
    val action: Int
) {
    companion object {
        fun of(event: KeyEvent): InputSignature = InputSignature(
            eventTime = event.eventTime,
            deviceId = event.deviceId,
            keyCode = event.keyCode,
            action = event.action
        )

        fun of(event: MotionEvent): InputSignature = InputSignature(
            eventTime = event.eventTime,
            deviceId = event.deviceId,
            keyCode = 0,
            action = event.actionMasked
        )
    }
}
