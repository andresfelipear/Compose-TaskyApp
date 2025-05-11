package com.aarevalo.tasky.core.data.preferences

import android.content.SharedPreferences
import com.aarevalo.tasky.core.domain.preferences.TokenPreferences

class TokenPreferencesImp(
    private val sharedTokenPreferences: SharedPreferences
): TokenPreferences {

    override fun readAccessToken(): String? {
        return sharedTokenPreferences.getString(TokenPreferences.KEY_ACCESS_TOKEN, null)
    }

    override fun readRefreshToken(): String? {
        return sharedTokenPreferences.getString(TokenPreferences.KEY_REFRESH_TOKEN, null)
    }

    override fun readAccessTokenExpirationTimestamp(): Long? {
        return sharedTokenPreferences.getLong(TokenPreferences.KEY_ACCESS_TOKEN_EXPIRATION_TIMESTAMP, 0)
    }

    override fun saveAccessToken(accessToken: String) {
        sharedTokenPreferences.edit()
            .putString(TokenPreferences.KEY_ACCESS_TOKEN, accessToken)
            .apply()
    }

    override fun saveRefreshToken(refreshToken: String) {
        sharedTokenPreferences.edit()
            .putString(TokenPreferences.KEY_REFRESH_TOKEN, refreshToken)
            .apply()
    }

    override fun saveAccessTokenExpirationTimestamp(expirationTimestamp: Long) {
        sharedTokenPreferences.edit()
            .putLong(TokenPreferences.KEY_ACCESS_TOKEN_EXPIRATION_TIMESTAMP, expirationTimestamp)
            .apply()
    }

    override fun clearAll() {
        sharedTokenPreferences.edit()
            .remove(TokenPreferences.KEY_ACCESS_TOKEN)
            .remove(TokenPreferences.KEY_REFRESH_TOKEN)
            .remove(TokenPreferences.KEY_ACCESS_TOKEN_EXPIRATION_TIMESTAMP)
            .apply()
    }

}