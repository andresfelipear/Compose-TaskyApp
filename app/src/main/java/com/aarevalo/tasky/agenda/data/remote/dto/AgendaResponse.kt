package com.aarevalo.tasky.agenda.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AgendaResponse(
    @Json(name = "events") val events: List<EventDto>,
    @Json(name = "tasks") val tasks: List<TaskDto>,
    @Json(name = "reminders") val reminders: List<ReminderDto>
)
