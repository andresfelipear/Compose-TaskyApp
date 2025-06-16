package com.aarevalo.tasky.agenda.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetAttendeeResponse(
    val doesUserExist: Boolean,
    val attendee: AttendeeDto?
)
