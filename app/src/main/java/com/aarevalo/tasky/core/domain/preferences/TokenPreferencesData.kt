package com.aarevalo.tasky.core.domain.preferences

import kotlinx.serialization.Serializable

@Serializable
data class TokenPreferencesData(
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val accessTokenExpirationTimestamp: Long = 0
)