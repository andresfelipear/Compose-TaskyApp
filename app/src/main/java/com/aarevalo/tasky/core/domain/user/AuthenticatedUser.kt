package com.aarevalo.tasky.core.domain.user

data class AuthenticatedUser(
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpirationTimestamp: Long,
    val userId: String,
    val fullName: String
)
