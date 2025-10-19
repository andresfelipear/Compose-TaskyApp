package com.aarevalo.tasky.agenda.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EventCreateRequest(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String,
    @Json(name = "from") val from: String,
    @Json(name = "to") val to: String,
    @Json(name = "remindAt") val remindAt: String,
    @Json(name = "attendeeIds") val attendeeIds: List<String>,
    @Json(name = "photoKeys") val photoKeys: List<String>,
    @Json(name = "updatedAt") val updatedAt: String,
)
