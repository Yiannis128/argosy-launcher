package com.nendo.argosy.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "state_tombstones",
    indices = [
        Index(value = ["rommSaveId"], unique = true),
        Index("gameId")
    ]
)
data class StateTombstoneEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val gameId: Long,
    val rommSaveId: Long,
    val createdAt: Instant
)
