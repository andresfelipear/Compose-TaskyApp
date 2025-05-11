package com.aarevalo.tasky.core.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AccessTokenResponse(
    @Json(name = "accessToken")
    val accessToken: String,
    @Json(name = "expirationTimestamp")
    val expirationTimestamp: Long
)
