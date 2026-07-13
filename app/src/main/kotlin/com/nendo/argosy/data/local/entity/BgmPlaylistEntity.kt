package com.nendo.argosy.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bgm_playlist",
    indices = [
        Index(value = ["filePath"], unique = true)
    ]
)
data class BgmPlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val position: Int,
    val filePath: String,
    val displayName: String,
    val gameFileId: Long? = null
)
