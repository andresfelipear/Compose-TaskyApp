package com.aarevalo.tasky.agenda.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EventRequest(
    val id: String,
    val title: String,
    val description: String,
    val from: Long,
    val to: Long,
    val reminderAt: Long,
    val attendeeIds: List<String>,
    val deletedPhotoKeys: List<String>? = null,
    val isGoing: Boolean? = null
)
