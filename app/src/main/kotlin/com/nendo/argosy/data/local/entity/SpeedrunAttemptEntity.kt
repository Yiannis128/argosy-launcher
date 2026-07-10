package com.nendo.argosy.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "speedrun_attempts",
    foreignKeys = [
        ForeignKey(
            entity = SpeedrunCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("categoryId")
    ]
)
data class SpeedrunAttemptEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val categoryId: Long,
    val startedAt: Long,
    val completed: Boolean,
    val finalTimeMs: Long? = null,
    /** JSON array of cumulative split times in ms; JSON null for skipped/unreached segments. */
    val splitTimesJson: String
)
