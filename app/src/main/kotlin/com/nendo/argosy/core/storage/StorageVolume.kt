package com.nendo.argosy.core.storage

enum class StorageVolumeType {
    INTERNAL,
    SD_CARD,
    USB,
    UNKNOWN
}

data class StorageVolume(
    val id: String,
    val displayName: String,
    val path: String,
    val type: StorageVolumeType,
    val availableBytes: Long = 0L,
    val totalBytes: Long = 0L
)
