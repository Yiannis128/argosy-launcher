package com.nendo.argosy.libretro

import android.os.Build
import android.util.Log

private const val TAG = "LibretroBuildbot"

object LibretroBuildbot {
    private val SUPPORTED_ABIS = setOf("arm64-v8a", "armeabi-v7a", "x86_64", "x86")

    val detectedAbi: String by lazy {
        val allAbis = Build.SUPPORTED_ABIS.toList()
        val selected = allAbis.firstOrNull { it in SUPPORTED_ABIS } ?: "arm64-v8a"
        Log.i(TAG, "Device ABIs: $allAbis, detected: $selected")
        selected
    }

    var abiOverride: String? = null
        set(value) {
            field = value
            Log.i(TAG, "ABI override set to: ${value ?: "(none, using detected: $detectedAbi)"}")
        }

    val deviceAbi: String
        get() = abiOverride ?: detectedAbi

    val baseUrl: String
        get() = "https://buildbot.libretro.com/nightly/android/latest/$deviceAbi"
}
