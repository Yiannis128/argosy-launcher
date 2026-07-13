package com.nendo.argosy.data.music

import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import com.nendo.argosy.data.preferences.StoragePreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/** Resolves and manages the public Music/RomM directory where soundtrack files live. */
@Singleton
class MusicDirectoryManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val storagePreferences: StoragePreferencesRepository
) {

    suspend fun resolveMusicDir(): File {
        val override = storagePreferences.preferences.first().musicStoragePath
        return if (override != null) {
            File(override)
        } else {
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
                "RomM"
            )
        }
    }

    suspend fun targetFileFor(
        platformName: String,
        gameName: String,
        trackNumber: Int?,
        title: String?,
        fileName: String
    ): File {
        val extension = fileName.substringAfterLast('.', "")
        val baseTitle = sanitize(title ?: fileName.substringBeforeLast('.'))
        val baseName = if (trackNumber != null) {
            "%02d - %s".format(trackNumber, baseTitle)
        } else {
            baseTitle
        }
        val targetName = if (extension.isNotEmpty()) "$baseName.$extension" else baseName
        val gameDir = File(File(resolveMusicDir(), sanitize(platformName)), sanitize(gameName))
        return File(gameDir, targetName)
    }

    fun moveIntoMusic(source: File, target: File): Boolean {
        return try {
            target.parentFile?.mkdirs()
            if (target.exists()) target.delete()
            if (source.renameTo(target)) {
                true
            } else {
                source.copyTo(target, overwrite = true)
                source.delete()
                true
            }
        } catch (_: Exception) {
            false
        }
    }

    fun scanFile(file: File) {
        MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), null, null)
    }

    private fun sanitize(name: String): String =
        name.replace(INVALID_CHARS, "_").trim().ifEmpty { "track" }

    companion object {
        private val INVALID_CHARS = Regex("[\\\\/:*?\"<>|;=]")
    }
}
