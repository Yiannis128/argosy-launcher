package com.nendo.argosy.data.repository

import android.util.Log
import com.nendo.argosy.data.remote.romm.RomMConnectionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "ScreenshotUploader"

@Singleton
class ScreenshotUploader @Inject constructor(
    private val connectionManager: RomMConnectionManager
) {
    sealed class Result {
        data object Success : Result()
        data object NotSupported : Result()
        data object NotConnected : Result()
        data class Error(val message: String) : Result()
    }

    suspend fun upload(file: File, rommId: Long): Result = withContext(Dispatchers.IO) {
        val api = connectionManager.getApi() ?: return@withContext Result.NotConnected
        if (!connectionManager.isConnected()) return@withContext Result.NotConnected
        if (!connectionManager.getCapabilities().supportsScreenshotUpload) {
            return@withContext Result.NotSupported
        }

        try {
            val mediaType = when (file.extension.lowercase()) {
                "jpg", "jpeg" -> "image/jpeg"
                "webp" -> "image/webp"
                else -> "image/png"
            }
            val body = file.asRequestBody(mediaType.toMediaType())
            val part = MultipartBody.Part.createFormData("screenshotFile", file.name, body)
            val response = api.uploadScreenshot(rommId, part)
            if (response.isSuccessful) {
                Log.i(TAG, "Uploaded screenshot ${file.name} for rommId=$rommId (id=${response.body()?.id})")
                Result.Success
            } else {
                Log.e(TAG, "Screenshot upload failed for rommId=$rommId: ${response.code()}")
                Result.Error("Upload failed: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Screenshot upload exception for rommId=$rommId", e)
            Result.Error(e.message ?: "Upload failed")
        }
    }
}
