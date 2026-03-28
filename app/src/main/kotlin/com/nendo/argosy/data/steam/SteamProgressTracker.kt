package com.nendo.argosy.data.steam

import android.util.Log
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "SteamProgressTracker"
private const val DOWNLOAD_INFO_DIR = ".DownloadInfo"
private const val BYTES_DOWNLOADED_FILE = "bytes_downloaded.txt"
private const val SPEED_WINDOW_MS = 30_000L
private const val SPEED_EMA_ALPHA = 0.3

@Singleton
class SteamProgressTracker @Inject constructor() {

    private data class SpeedSample(val timeMs: Long, val totalBytes: Long)

    private val speedSamples = CopyOnWriteArrayList<SpeedSample>()
    private var emaSpeedBps: Double = 0.0
    private var hasEmaSpeed: Boolean = false

    fun addSpeedSample(timestampMs: Long, totalBytes: Long) {
        speedSamples.add(SpeedSample(timestampMs, totalBytes))
        val cutoff = timestampMs - SPEED_WINDOW_MS
        while (speedSamples.isNotEmpty() && speedSamples.first().timeMs < cutoff) {
            speedSamples.removeAt(0)
        }
    }

    fun computeSpeed(nowMs: Long): Long {
        val cutoff = nowMs - SPEED_WINDOW_MS
        val samples = speedSamples.toTypedArray().filter { it.timeMs >= cutoff }
        if (samples.size < 2) return 0L

        val first = samples.first()
        val last = samples.last()
        val elapsedMs = last.timeMs - first.timeMs
        if (elapsedMs <= 0L) return 0L

        val bytesDelta = last.totalBytes - first.totalBytes
        if (bytesDelta <= 0L) return 0L

        val currentBps = bytesDelta.toDouble() / (elapsedMs.toDouble() / 1000.0)
        if (currentBps <= 0.0) return 0L

        emaSpeedBps = if (!hasEmaSpeed) {
            hasEmaSpeed = true
            currentBps
        } else {
            SPEED_EMA_ALPHA * currentBps + (1 - SPEED_EMA_ALPHA) * emaSpeedBps
        }

        return emaSpeedBps.toLong()
    }

    fun resetSpeedTracking() {
        speedSamples.clear()
        emaSpeedBps = 0.0
        hasEmaSpeed = false
    }

    fun persistBytes(appDirPath: String, bytes: Long) {
        try {
            val dir = File(appDirPath, DOWNLOAD_INFO_DIR)
            if (!dir.exists()) dir.mkdirs()
            File(dir, BYTES_DOWNLOADED_FILE).writeText(bytes.toString())
        } catch (e: Exception) {
            Log.e(TAG, "Failed to persist bytes: ${e.message}")
        }
    }

    fun loadPersistedBytes(appDirPath: String): Long {
        return try {
            val file = File(File(appDirPath, DOWNLOAD_INFO_DIR), BYTES_DOWNLOADED_FILE)
            if (file.exists()) file.readText().trim().toLongOrNull() ?: 0L else 0L
        } catch (e: Exception) {
            0L
        }
    }

    fun clearPersistedBytes(appDirPath: String) {
        try {
            File(File(appDirPath, DOWNLOAD_INFO_DIR), BYTES_DOWNLOADED_FILE).delete()
        } catch (_: Exception) {}
    }
}
