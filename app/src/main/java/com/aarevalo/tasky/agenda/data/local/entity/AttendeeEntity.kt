package com.aarevalo.tasky.agenda.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "attendees",
    foreignKeys = [
        ForeignKey(
            entity = EventEntity::class,
            parentColumns = ["eventId"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["eventId"])]
)
data class AttendeeEntity(
    @PrimaryKey(autoGenerate = false)
    val attendeeUserId: String,
    val fullName: String,
    val email: String,
    val eventId: String?,
    val isGoing: Boolean?,
    val remindAt: Long?,
)
