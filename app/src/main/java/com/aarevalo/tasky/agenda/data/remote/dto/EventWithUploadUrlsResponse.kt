package com.aarevalo.tasky.agenda.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EventWithUploadUrlsResponse(
    @Json(name = "event") val event: EventDto,
    @Json(name = "uploadUrls") val uploadUrls: List<UploadUrlDto>
)

@JsonClass(generateAdapter = true)
data class UploadUrlDto(
    @Json(name = "photoKey") val photoKey: String,
    @Json(name = "uploadKey") val uploadKey: String,
    @Json(name = "url") val url: String
)
