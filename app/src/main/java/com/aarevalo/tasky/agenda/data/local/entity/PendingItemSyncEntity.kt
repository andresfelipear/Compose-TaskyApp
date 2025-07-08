package com.aarevalo.tasky.agenda.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aarevalo.tasky.agenda.domain.model.AgendaItemType

@Entity(tableName = "pending_item_sync")
data class PendingItemSyncEntity(
    @PrimaryKey(autoGenerate = false)
    val itemId: String,
    val userId: String,
    val isGoing: Boolean,
    val deletedPhotoKeys: List<String>,
    val itemType: AgendaItemType,
    val syncOperation: SyncOperation,
    val itemJson: String
)

enum class SyncOperation {
    CREATE,
    UPDATE,
    DELETE
}

