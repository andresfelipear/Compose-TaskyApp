package com.aarevalo.tasky.agenda.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EventDto(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String,
    @Json(name = "from") val from: String,
    @Json(name = "to") val to: String,
    @Json(name = "remindAt") val remindAt: String,
    @Json(name = "hostId") val hostId: String,
    @Json(name = "isUserEventCreator") val isUserEventCreator: Boolean,
    @Json(name = "attendees") val attendees: List<AttendeeDto>,
    @Json(name = "photoKeys") val photos: List<PhotoDto>
)
