package com.aarevalo.tasky.agenda.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterRequest(
    @Json(name = "fullName")
    val fullName: String,
    @Json(name = "email")
    val email: String,
    @Json(name = "password")
    val password: String,
)