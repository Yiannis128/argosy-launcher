package com.nendo.argosy.libretro

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import com.swordfish.libretrodroid.GLRetroView

/**
 * Owns audio session configuration for a libretro session: audio focus
 * acquisition/abandonment and SoundTouch pitch preservation toggling for
 * fast-forward.
 */
class LibretroAudioController(
    private val context: Context,
    private val getRetroView: () -> GLRetroView
) {
    private var audioFocusRequest: AudioFocusRequest? = null

    fun requestAudioFocus() {
        val am = context.getSystemService(AudioManager::class.java) ?: return
        val request = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setOnAudioFocusChangeListener { }
            .build()
        audioFocusRequest = request
        am.requestAudioFocus(request)
    }

    fun abandonAudioFocus() {
        val request = audioFocusRequest ?: return
        val am = context.getSystemService(AudioManager::class.java) ?: return
        am.abandonAudioFocusRequest(request)
        audioFocusRequest = null
    }

    fun applyInitialAudioConfig(pitchPreservationEnabled: Boolean, audioVolume: Float) {
        val rv = getRetroView()
        rv.audioEnabled = true
        rv.pitchPreservationEnabled = pitchPreservationEnabled
        rv.audioVolume = audioVolume
    }

    fun setPitchPreservationEnabled(enabled: Boolean) {
        getRetroView().pitchPreservationEnabled = enabled
    }
}
