package com.aarevalo.tasky.agenda.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ConfirmUploadRequest(
    @Json(name = "uploadedKeys") val uploadedKeys: List<String>
)
