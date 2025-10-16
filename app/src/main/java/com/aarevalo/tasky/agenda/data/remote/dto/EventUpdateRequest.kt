package com.aarevalo.tasky.agenda.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EventUpdateRequest(
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String,
    @Json(name = "from") val from: String,
    @Json(name = "to") val to: String,
    @Json(name = "remindAt") val remindAt: String,
    @Json(name = "attendeeIds") val attendeeIds: List<String>,
    @Json(name = "newPhotoKeys") val newPhotoKeys: List<String>,
    @Json(name = "deletedPhotoKeys") val deletedPhotoKeys: List<String>,
    @Json(name = "isGoing") val isGoing: Boolean,
    @Json(name = "updatedAt") val updatedAt: String,
)
