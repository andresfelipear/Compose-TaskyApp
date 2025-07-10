package com.aarevalo.tasky.agenda.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReminderDto(
    val id: String,
    val title: String,
    val description: String,
    val time: Long,
    val remindAt: Long,
)
