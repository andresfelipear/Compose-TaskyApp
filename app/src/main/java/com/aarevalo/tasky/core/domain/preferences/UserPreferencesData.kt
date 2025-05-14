package com.aarevalo.tasky.core.domain.preferences

import kotlinx.serialization.Serializable

@Serializable
data class UserPreferencesData(
    val userId: String? = null,
    val fullName: String? = null,
    val email: String? = null
)
