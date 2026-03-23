package com.nendo.argosy.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "steam_licenses",
    foreignKeys = [
        ForeignKey(
            entity = SteamAccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("accountId"),
        Index(value = ["packageId", "accountId"], unique = true)
    ]
)
data class SteamLicenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val accountId: Long,
    val packageId: Int,
    val appIds: String,
    val licenseType: Int,
    val createdAt: Instant = Instant.now()
)
