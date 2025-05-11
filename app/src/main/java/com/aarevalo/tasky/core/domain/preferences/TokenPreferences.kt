package com.aarevalo.tasky.core.domain.preferences

interface TokenPreferences {
    fun readAccessToken(): String?
    fun readRefreshToken(): String?
    fun saveAccessToken(accessToken: String)
    fun saveRefreshToken(refreshToken: String)
    fun saveAccessTokenExpirationTimestamp(expirationTimestamp: Long)
    fun readAccessTokenExpirationTimestamp(): Long?
    fun clearAll()

    companion object{
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
        const val KEY_ACCESS_TOKEN_EXPIRATION_TIMESTAMP = "access_token_expiration_timestamp"
    }
}