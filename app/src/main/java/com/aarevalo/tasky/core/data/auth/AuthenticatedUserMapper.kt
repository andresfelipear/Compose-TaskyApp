package com.aarevalo.tasky.core.data.auth

import com.aarevalo.tasky.core.domain.user.AuthenticatedUser

fun AuthenticatedUserSerializable.toAuthenticatedUser(): AuthenticatedUser {
    return AuthenticatedUser(
        accessToken = accessToken,
        refreshToken = refreshToken,
        accessTokenExpirationTimestamp = accessTokenExpirationTimestamp,
        userId = userId,
        fullName = fullName
    )
}

fun AuthenticatedUser.toAuthenticatedUserSerializable(authenticatedUser: AuthenticatedUser): AuthenticatedUserSerializable {
    return AuthenticatedUserSerializable(
        accessToken = authenticatedUser.accessToken ?: "",
        refreshToken = authenticatedUser.refreshToken ?: "",
        accessTokenExpirationTimestamp = authenticatedUser.accessTokenExpirationTimestamp ?: 0,
        userId = authenticatedUser.userId ?: "",
        fullName = authenticatedUser.fullName ?: ""
    )
}
