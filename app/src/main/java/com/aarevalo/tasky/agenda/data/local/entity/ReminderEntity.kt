package com.aarevalo.tasky.agenda.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = false)
    val reminderId: String,
    val title: String,
    val description: String,
    val time: Time,
)
