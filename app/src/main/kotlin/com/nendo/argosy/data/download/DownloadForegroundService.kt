package com.nendo.argosy.data.download

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.nendo.argosy.MainActivity
import com.nendo.argosy.R
import dagger.hilt.android.AndroidEntryPoint
import com.nendo.argosy.util.SafeCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DownloadForegroundService : Service() {

    @Inject
    lateinit var downloadManager: DownloadManager

    @Inject
    lateinit var steamContentManager: com.nendo.argosy.data.steam.SteamContentManager

    private val serviceScope = SafeCoroutineScope(Dispatchers.Main, "DownloadForegroundService")
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onCreate() {
        super.onCreate()
        acquireWakeLock()
        startForegroundWithNotification("Preparing download...", 0, 0)
        observeDownloadState()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopSelf()
            return START_NOT_STICKY
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        releaseWakeLock()
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun acquireWakeLock() {
        val powerManager = getSystemService(PowerManager::class.java)
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            WAKELOCK_TAG
        ).apply {
            acquire(MAX_WAKELOCK_DURATION_MS)
        }
    }

    private fun releaseWakeLock() {
        wakeLock?.let {
            if (it.isHeld) it.release()
        }
        wakeLock = null
    }

    private fun observeDownloadState() {
        serviceScope.launch {
            downloadManager.state.collectLatest { state ->
                val active = state.activeDownloads
                val queued = state.queue.filter { it.state == DownloadState.QUEUED }

                // Don't stop if Steam has active work
                val steamActive = steamContentManager.downloadState.value
                val steamBusy = steamActive !is com.nendo.argosy.data.steam.SteamDownloadState.Idle &&
                    steamActive !is com.nendo.argosy.data.steam.SteamDownloadState.Completed &&
                    steamActive !is com.nendo.argosy.data.steam.SteamDownloadState.Failed

                if (active.isEmpty() && queued.isEmpty() && !steamBusy) {
                    stopSelf()
                    return@collectLatest
                }

                val currentDownload = active.firstOrNull()
                if (currentDownload != null) {
                    val isExtracting = currentDownload.state == DownloadState.EXTRACTING
                    val title = when (currentDownload.state) {
                        DownloadState.EXTRACTING -> "Extracting: ${currentDownload.displayTitle}"
                        else -> "Downloading: ${currentDownload.displayTitle}"
                    }
                    if (isExtracting) {
                        updateNotification(title, 0, 0)
                    } else {
                        val progressPercent = (currentDownload.progressPercent * 100).toInt()
                        updateNotification(title, progressPercent, 100)
                    }
                } else if (!steamBusy) {
                    val nextQueued = queued.firstOrNull()
                    val message = nextQueued?.let { "Queued: ${it.displayTitle}" }
                        ?: "Download pending..."
                    updateNotification(message, 0, 0)
                }
            }
        }

        // Steam download notifications
        serviceScope.launch {
            steamContentManager.activeDownload.collect { steamDownload ->
                if (steamDownload == null) return@collect
                when (steamDownload.state) {
                    is com.nendo.argosy.data.steam.SteamDownloadState.Preparing ->
                        updateNotification("Preparing: ${steamDownload.gameName}", 0, 0)
                    is com.nendo.argosy.data.steam.SteamDownloadState.Downloading -> {
                        val pct = (steamDownload.progress * 100).toInt()
                        updateNotification("Downloading: ${steamDownload.gameName}", pct, 100)
                    }
                    is com.nendo.argosy.data.steam.SteamDownloadState.Moving ->
                        updateNotification("Moving: ${steamDownload.gameName}", 0, 0)
                    else -> {}
                }
            }
        }
    }

    private fun startForegroundWithNotification(
        contentText: String,
        progress: Int,
        maxProgress: Int
    ) {
        val notification = buildNotification(contentText, progress, maxProgress)
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun updateNotification(
        contentText: String,
        progress: Int,
        maxProgress: Int
    ) {
        val notification = buildNotification(contentText, progress, maxProgress)
        val manager = getSystemService(android.app.NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(
        contentText: String,
        progress: Int,
        maxProgress: Int
    ) = NotificationCompat.Builder(this, DownloadNotificationChannel.CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_helm)
        .setContentTitle(getString(R.string.app_name))
        .setContentText(contentText)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setOngoing(true)
        .setOnlyAlertOnce(true)
        .setSilent(true)
        .setProgress(maxProgress, progress, progress == 0 && maxProgress == 0)
        .setContentIntent(createContentIntent())
        .build()

    private fun createContentIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    companion object {
        private const val NOTIFICATION_ID = 0x2000
        private const val ACTION_STOP = "com.nendo.argosy.STOP_DOWNLOAD_SERVICE"
        private const val WAKELOCK_TAG = "argosy:download_wakelock"
        private const val MAX_WAKELOCK_DURATION_MS = 60 * 60 * 1000L

        fun start(context: Context) {
            val intent = Intent(context, DownloadForegroundService::class.java)
            context.startForegroundService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, DownloadForegroundService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }
}
