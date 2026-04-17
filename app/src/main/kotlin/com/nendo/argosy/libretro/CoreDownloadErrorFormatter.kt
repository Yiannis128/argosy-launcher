package com.nendo.argosy.libretro

fun formatCoreDownloadError(message: String): String {
    val abi = LibretroBuildbot.deviceAbi
    return when {
        "HTTP 404" in message -> "Core is not published for $abi. This core may not be available on 32-bit devices."
        "HTTP 5" in message -> "Libretro buildbot server error. Try again later."
        "corrupted" in message -> "Downloaded core file is corrupted. Try again."
        "timed out" in message.lowercase() || "timeout" in message.lowercase() ->
            "Download timed out. Check your network connection."
        else -> message
    }
}
