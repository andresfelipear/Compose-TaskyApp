package com.aarevalo.tasky.agenda.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = false)
    val eventId: String,
    val title: String,
    val description: String,
    val fromTimestamp: Long,
    val toTimestamp: Long,
    val reminderAt: Long,
    val host: String,
    val isUserEventCreator: Boolean,
    val photoKeys: List<String>
)
