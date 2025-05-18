package com.aarevalo.tasky.core.data.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthenticatedUserSerializable(
    val accessToken: String = "",
    val refreshToken: String = "",
    val accessTokenExpirationTimestamp: Long = 0,
    val userId: String = "",
    val fullName: String = ""
)


