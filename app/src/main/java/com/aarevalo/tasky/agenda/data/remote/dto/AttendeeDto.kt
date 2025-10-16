package com.aarevalo.tasky.agenda.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AttendeeDto(
    @Json(name = "userId") val userId: String,
    @Json(name = "username") val fullName: String,
    @Json(name = "email") val email: String,
    @Json(name = "eventId") val eventId: String?,
    @Json(name = "isGoing") val isGoing: Boolean?,
    @Json(name = "remindAt") val remindAt: String?,
)
