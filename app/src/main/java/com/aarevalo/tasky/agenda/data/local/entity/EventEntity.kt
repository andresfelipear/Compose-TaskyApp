package com.aarevalo.tasky.agenda.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val title: String,
    val description: String,
    val from: Long,
    val to: Long,
    val reminderAt: Long,
    val host: String,
    val isUserEventCreator: Boolean,
    val photoKeys: List<String>
)
