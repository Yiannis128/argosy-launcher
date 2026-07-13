package com.nendo.argosy.ui.audio

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "AmbientAudio"

@Singleton
class AmbientAudioManager @Inject constructor() {
    companion object {
        const val AMBIENT_SOURCE_PLAYLIST = "playlist:"
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var mediaPlayer: MediaPlayer? = null
    private var enabled = false
    private var targetVolume = 0.5f
    private var fadeAnimator: ValueAnimator? = null
    private var fadeOutCancelled = false
    private var suspended = false

    private var sourceSet = false
    private var sourcePaths: List<String> = emptyList()
    private var playlistRefresh: (suspend () -> List<String>)? = null
    private var refreshJob: Job? = null
    private var generation = 0
    private var playlist: List<String> = emptyList()
    private var playlistIndex = 0
    private var shuffle = false

    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentTrackName = MutableStateFlow<String?>(null)
    val currentTrackName: StateFlow<String?> = _currentTrackName.asStateFlow()

    fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
        Log.d(TAG, "setEnabled=$enabled")
        if (!enabled) {
            stopAndRelease()
        }
    }

    fun setVolume(volume: Int) {
        this.targetVolume = (volume / 100f).coerceIn(0f, 1f)
        Log.d(TAG, "setVolume=$volume (${this.targetVolume})")
        mediaPlayer?.setVolume(targetVolume, targetVolume)
    }

    fun setShuffle(shuffle: Boolean) {
        this.shuffle = shuffle
        Log.d(TAG, "setShuffle=$shuffle")
        if (playlist.isNotEmpty()) {
            reshufflePlaylist()
        }
    }

    /** Replaces the playback queue; refresh re-expands the playlist at each full loop. */
    fun setPlaylistSource(paths: List<String>, refresh: (suspend () -> List<String>)? = null) {
        playlistRefresh = refresh
        if (sourceSet && paths == sourcePaths) return
        Log.d(TAG, "setPlaylistSource: ${paths.size} tracks")

        generation++
        refreshJob?.cancel()

        if (!sourceSet) {
            stopAndRelease()
            sourceSet = true
            sourcePaths = paths
            playlist = if (shuffle) paths.shuffled() else paths
            playlistIndex = 0
            updateCurrentTrackName()
            if (enabled && playlist.isNotEmpty()) {
                preparePlayer(playlist[playlistIndex])
            }
            return
        }

        sourcePaths = paths
        applyPlaylistUpdate(paths)
    }

    private fun applyPlaylistUpdate(paths: List<String>) {
        val current = playlist.getOrNull(playlistIndex)
        val updated = if (shuffle) {
            val kept = playlist.filter { it in paths }
            kept + paths.filter { it !in kept }.shuffled()
        } else {
            paths
        }
        playlist = updated

        if (updated.isEmpty()) {
            Log.w(TAG, "Playlist emptied while active")
            stopAndRelease()
            playlistIndex = 0
            updateCurrentTrackName()
            return
        }

        val currentIdx = current?.let { updated.indexOf(it) } ?: -1
        if (currentIdx >= 0) {
            playlistIndex = currentIdx
            updateCurrentTrackName()
        } else {
            playlistIndex = 0
            if (enabled) {
                restartAtCurrentIndex(resume = _isPlaying.value)
            } else {
                updateCurrentTrackName()
            }
        }
    }

    private fun reshufflePlaylist() {
        if (playlist.isEmpty()) return
        playlist = playlist.shuffled()
        playlistIndex = 0
        updateCurrentTrackName()
        Log.d(TAG, "Reshuffled playlist")
    }

    private fun updateCurrentTrackName() {
        _currentTrackName.value = playlist.getOrNull(playlistIndex)?.substringAfterLast("/")
    }

    private fun preparePlayer(path: String) {
        if (!validatePath(path)) {
            Log.w(TAG, "Audio file not accessible: $path")
            playNextTrack()
            return
        }

        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(audioAttributes)
                setDataSource(path)
                setVolume(0f, 0f)
                setOnPreparedListener {
                    Log.d(TAG, "MediaPlayer prepared: ${path.substringAfterLast("/")}")
                }
                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "MediaPlayer error: what=$what extra=$extra")
                    playNextTrack()
                    true
                }
                setOnCompletionListener {
                    Log.d(TAG, "Track completed")
                    playNextTrack()
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to prepare MediaPlayer", e)
            mediaPlayer = null
            playNextTrack()
        }
    }

    private fun playNextTrack() {
        if (playlist.isEmpty()) return

        val wasPlaying = _isPlaying.value
        mediaPlayer?.release()
        mediaPlayer = null

        playlistIndex++
        if (playlistIndex >= playlist.size) {
            refreshAndRestart(resume = wasPlaying)
            return
        }

        Log.d(TAG, "Playing next track: ${playlist[playlistIndex].substringAfterLast("/")}")
        restartAtCurrentIndex(resume = wasPlaying)
    }

    private fun refreshAndRestart(resume: Boolean) {
        val gen = generation
        val refresh = playlistRefresh
        refreshJob?.cancel()
        refreshJob = scope.launch {
            val fresh = withContext(Dispatchers.IO) {
                refresh?.invoke() ?: sourcePaths.filter { File(it).canRead() }
            }
            if (gen != generation) return@launch
            sourcePaths = fresh
            playlist = if (shuffle) fresh.shuffled() else fresh
            playlistIndex = 0
            updateCurrentTrackName()
            if (playlist.isEmpty()) {
                Log.w(TAG, "No more tracks in playlist")
                _isPlaying.value = false
                return@launch
            }
            Log.d(TAG, "Playlist loop refreshed: ${playlist.size} tracks")
            restartAtCurrentIndex(resume = resume)
        }
    }

    private fun restartAtCurrentIndex(resume: Boolean) {
        mediaPlayer?.release()
        mediaPlayer = null
        updateCurrentTrackName()
        val track = playlist.getOrNull(playlistIndex) ?: return
        preparePlayer(track)
        if (resume && enabled && !suspended) {
            mediaPlayer?.setOnPreparedListener {
                try {
                    it.setVolume(targetVolume, targetVolume)
                    it.start()
                    _isPlaying.value = true
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to start next track", e)
                }
            }
        }
    }

    private fun validatePath(path: String): Boolean {
        return try {
            File(path).canRead()
        } catch (e: Exception) {
            Log.w(TAG, "Path validation failed: ${e.message}")
            false
        }
    }

    fun suspend() {
        suspended = true
        fadeOut()
        Log.d(TAG, "suspended - awaiting user input to resume")
    }

    fun resumeFromSuspend() {
        if (suspended) {
            suspended = false
            Log.d(TAG, "resumed from suspend")
            fadeIn()
        }
    }

    fun fadeIn(durationMs: Long = 500) {
        if (suspended) {
            Log.d(TAG, "fadeIn skipped: suspended (awaiting user input)")
            return
        }
        if (!enabled) {
            Log.d(TAG, "fadeIn skipped: disabled")
            return
        }

        if (mediaPlayer == null && playlist.isNotEmpty()) {
            preparePlayer(playlist[playlistIndex])
        }

        val player = mediaPlayer ?: return

        fadeOutCancelled = true
        fadeAnimator?.cancel()

        try {
            player.setVolume(0f, 0f)
            if (!player.isPlaying) {
                player.start()
            }
            _isPlaying.value = true

            fadeAnimator = ValueAnimator.ofFloat(0f, targetVolume).apply {
                duration = durationMs
                addUpdateListener { animator ->
                    val vol = animator.animatedValue as Float
                    mediaPlayer?.setVolume(vol, vol)
                }
                start()
            }
            Log.d(TAG, "fadeIn started")
        } catch (e: Exception) {
            Log.e(TAG, "fadeIn failed", e)
        }
    }

    fun fadeOut(durationMs: Long = 500, onComplete: () -> Unit = {}) {
        val player = mediaPlayer
        if (player == null || !player.isPlaying) {
            onComplete()
            return
        }

        fadeOutCancelled = false
        fadeAnimator?.cancel()

        fadeAnimator = ValueAnimator.ofFloat(targetVolume, 0f).apply {
            duration = durationMs
            addUpdateListener { animator ->
                val vol = animator.animatedValue as Float
                mediaPlayer?.setVolume(vol, vol)
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    if (!fadeOutCancelled) {
                        pauseInternal()
                    }
                    onComplete()
                }
            })
            start()
        }
        Log.d(TAG, "fadeOut started")
    }

    private fun pauseInternal() {
        try {
            mediaPlayer?.pause()
            _isPlaying.value = false
            Log.d(TAG, "paused")
        } catch (e: Exception) {
            Log.e(TAG, "pause failed", e)
        }
    }

    private fun stopAndRelease() {
        fadeAnimator?.cancel()
        fadeAnimator = null

        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            Log.e(TAG, "stopAndRelease error", e)
        }
        mediaPlayer = null
        _isPlaying.value = false
        Log.d(TAG, "stopped and released")
    }

    fun release() {
        generation++
        refreshJob?.cancel()
        refreshJob = null
        stopAndRelease()
        enabled = false
        sourceSet = false
        sourcePaths = emptyList()
        playlistRefresh = null
        playlist = emptyList()
        playlistIndex = 0
        _currentTrackName.value = null
    }
}
