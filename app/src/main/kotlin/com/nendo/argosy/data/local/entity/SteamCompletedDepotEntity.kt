package com.nendo.argosy.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "steam_completed_depots",
    indices = [
        Index(value = ["appId", "depotId"], unique = true)
    ]
)
data class SteamCompletedDepotEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val appId: Long,
    val depotId: Int,
    val manifestId: Long
)
