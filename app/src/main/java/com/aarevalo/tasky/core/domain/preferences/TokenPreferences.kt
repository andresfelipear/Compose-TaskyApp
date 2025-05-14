package com.aarevalo.tasky.core.domain.preferences

interface TokenPreferences {
    fun readAccessToken(): String?
    fun readRefreshToken(): String?
    fun saveAccessToken(accessToken: String)
    fun saveRefreshToken(refreshToken: String)
    fun saveAccessTokenExpirationTimestamp(expirationTimestamp: Long)
    fun readAccessTokenExpirationTimestamp(): Long?
    fun clearAll()
}