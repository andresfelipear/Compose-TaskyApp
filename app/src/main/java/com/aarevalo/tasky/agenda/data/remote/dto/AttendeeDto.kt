package com.aarevalo.tasky.agenda.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AttendeeDto(
    val userId: String,
    val fullName: String,
    val email: String,
    val eventId: String,
    val isGoing: Boolean,
    val remindAt: Long,
)
