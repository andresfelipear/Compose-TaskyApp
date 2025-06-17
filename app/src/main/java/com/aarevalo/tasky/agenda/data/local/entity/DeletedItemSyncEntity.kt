package com.aarevalo.tasky.agenda.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deleted_item_sync")
data class DeletedItemSyncEntity(
    @PrimaryKey(autoGenerate = false)
    val itemId: String,
    val userId: String,
    val itemType: String,
)
