package com.aarevalo.tasky.agenda.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TaskDto(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String,
    @Json(name = "time") val time: String,
    @Json(name = "remindAt") val remindAt: String,
    @Json(name = "updatedAt") val updatedAt: String,
    @Json(name = "isDone") val isDone: Boolean,
)
