package com.aarevalo.tasky.core.data.auth

import com.aarevalo.tasky.core.domain.user.AuthenticatedUser
import kotlinx.serialization.Serializable

@Serializable
data class AuthenticatedUserSerializable(
    val accessToken: String = "",
    val refreshToken: String = "",
    val accessTokenExpirationTimestamp: Long = 0,
    val userId: String = "",
    val fullName: String = ""
){

    fun toAuthenticatedUser(): AuthenticatedUser {
        return AuthenticatedUser(
            accessToken = accessToken,
            refreshToken = refreshToken,
            accessTokenExpirationTimestamp = accessTokenExpirationTimestamp,
            userId = userId,
            fullName = fullName
        )
    }

    companion object{
        fun fromAuthenticatedUser(authenticatedUser: AuthenticatedUser): AuthenticatedUserSerializable{
            return AuthenticatedUserSerializable(
                accessToken = authenticatedUser.accessToken ?: "",
                refreshToken = authenticatedUser.refreshToken ?: "",
                accessTokenExpirationTimestamp = authenticatedUser.accessTokenExpirationTimestamp ?: 0,
                userId = authenticatedUser.userId ?: "",
                fullName = authenticatedUser.fullName ?: ""
            )
        }
    }
}


