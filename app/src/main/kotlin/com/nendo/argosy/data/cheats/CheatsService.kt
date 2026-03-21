package com.nendo.argosy.data.cheats

import com.nendo.argosy.BuildConfig
import com.nendo.argosy.util.Logger
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "CheatsService"

@Singleton
class CheatsService @Inject constructor(
    private val requestSigner: CheatsRequestSigner,
    private val moshi: Moshi
) {
    private val baseUrl: String = BuildConfig.TITLEDB_API_URL

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    private val resultAdapter by lazy {
        moshi.adapter(CheatsLookupResult::class.java)
    }

    fun isConfigured(): Boolean = requestSigner.isConfigured() && baseUrl.isNotBlank()

    suspend fun lookupByName(
        name: String,
        platform: String,
        deviceToken: String,
        region: String? = null,
        version: String? = null
    ): CheatsLookupResult? = withContext(Dispatchers.IO) {
        if (!isConfigured()) {
            Logger.debug(TAG, "CheatsDB not configured, skipping lookup")
            return@withContext null
        }

        val signed = requestSigner.sign(deviceToken, name)
        if (signed == null) {
            Logger.debug(TAG, "Failed to sign request")
            return@withContext null
        }

        val encodedName = URLEncoder.encode(name, "UTF-8")
        val urlBuilder = StringBuilder("$baseUrl/api/v2/cheats/lookup?name=$encodedName&platform=$platform")
        if (!region.isNullOrBlank()) {
            urlBuilder.append("&region=").append(URLEncoder.encode(region, "UTF-8"))
        }
        if (!version.isNullOrBlank()) {
            urlBuilder.append("&version=").append(URLEncoder.encode(version, "UTF-8"))
        }

        val request = Request.Builder()
            .url(urlBuilder.toString())
            .header("X-Device-Token", signed.deviceToken)
            .header("X-Timestamp", signed.timestamp.toString())
            .header("X-Signature", signed.signature)
            .header("X-FP-Hash", signed.fpHash)
            .get()
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    if (response.code == 404) {
                        Logger.debug(TAG, "No cheats found for name=$name, platform=$platform")
                    } else {
                        Logger.debug(TAG, "Lookup failed: ${response.code} for name=$name, platform=$platform")
                    }
                    return@withContext null
                }

                val body = response.body?.string()
                if (body == null) {
                    Logger.debug(TAG, "Empty response body")
                    return@withContext null
                }

                val result = resultAdapter.fromJson(body)
                val totalCheats = result?.variants?.sumOf { it.cheatCount } ?: 0
                Logger.debug(TAG, "Cheats lookup: game=${result?.gameName}, variants=${result?.variants?.size}, totalCheats=$totalCheats, score=${result?.score}")
                result
            }
        } catch (e: Exception) {
            Logger.error(TAG, "Cheats lookup error for name=$name: ${e.message}")
            null
        }
    }
}
