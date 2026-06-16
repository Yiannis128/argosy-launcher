package com.nendo.argosy.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "core_version_history",
    indices = [Index("coreId"), Index("archivedAt")]
)
data class CoreVersionHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val coreId: String,
    val version: String,
    val hash: String,
    val size: Long,
    val fileName: String,
    val archivedAt: Instant
)
