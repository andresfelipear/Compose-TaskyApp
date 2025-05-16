package com.aarevalo.tasky.core.domain.user

data class AuthenticatedUser(
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val accessTokenExpirationTimestamp: Long? = null,
    val userId: String? = null,
    val fullName: String? = null
)
