package com.aarevalo.tasky.core.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AccessTokenRequest(
    @Json(name = "refreshToken")
    val refreshToken: String,
    @Json(name = "userId")
    val userId: String
)
