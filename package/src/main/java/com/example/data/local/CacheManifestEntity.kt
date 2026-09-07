package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cache_manifest")
data class CacheManifestEntity(
    @PrimaryKey val manifestId: String = "primary_manifest",
    val contentVersion: String = "2.4.0",
    val dbSchemaVersion: Int = 6,
    val lastUpdatedMillis: Long = System.currentTimeMillis(),
    val updateNotes: String = "Initial 2004 Sport Trac CAD & Factory Manual Pack v2.4.0",
    val hasPendingUpgrade: Boolean = false,
    val latestAvailableVersion: String = "2.5.0"
)
