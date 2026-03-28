package com.nendo.argosy.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "steam_completed_files",
    indices = [
        Index(value = ["appId"]),
        Index(value = ["appId", "depotId", "fileName"], unique = true)
    ]
)
data class SteamCompletedFileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val appId: Long,
    val depotId: Int,
    val manifestId: Long,
    val fileName: String
)
