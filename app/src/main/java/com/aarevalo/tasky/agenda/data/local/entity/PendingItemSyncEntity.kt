package com.aarevalo.tasky.agenda.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_item_sync")
data class PendingItemSyncEntity(
    @PrimaryKey(autoGenerate = false)
    val itemId: String,
    val userId: String,
    val itemType: String,
    val syncOperation: String,
    val itemJson: String
){
    companion object SyncOperation {
        const val CREATE = "CREATE"
        const val UPDATE = "UPDATE"
    }
}

