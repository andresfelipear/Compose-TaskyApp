package com.aarevalo.tasky.agenda.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SyncAgendaRequest(
    @Json(name = "deletedEventIds") val deletedEventIds: List<String>,
    @Json(name = "deletedTaskIds") val deletedTaskIds: List<String>,
    @Json(name = "deletedReminderIds") val deletedReminderIds: List<String>,
)
