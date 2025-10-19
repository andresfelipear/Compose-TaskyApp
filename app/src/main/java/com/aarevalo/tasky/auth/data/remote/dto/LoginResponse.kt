package com.aarevalo.tasky.auth.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "accessToken")
    val accessToken: String,
    @Json(name = "refreshToken")
    val refreshToken: String,
    @Json(name = "username")
    val fullName: String,
    val userId: String,
    @Json(name = "accessTokenExpirationTimestamp")
    val accessTokenExpirationTimestamp: Long
)
