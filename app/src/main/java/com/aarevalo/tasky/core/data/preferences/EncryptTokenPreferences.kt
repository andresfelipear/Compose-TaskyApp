package com.aarevalo.tasky.core.data.preferences

import androidx.datastore.core.DataStore
import com.aarevalo.tasky.core.domain.preferences.TokenPreferences
import com.aarevalo.tasky.core.domain.preferences.TokenPreferencesData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking


class EncryptTokenPreferences(
    private val dataStore: DataStore<TokenPreferencesData>,
) : TokenPreferences {


    override fun readAccessToken(): String? = runBlocking {
        dataStore.data.map {
            it.accessToken
        }
            .first()
    }

    override fun readRefreshToken(): String? = runBlocking {
        dataStore.data.map {
            it.refreshToken
        }
            .first()
    }

    override fun readAccessTokenExpirationTimestamp(): Long? = runBlocking {
        dataStore.data.map {
            it.accessTokenExpirationTimestamp
        }
            .first()
    }

    override fun saveAccessToken(accessToken: String): Unit = runBlocking {
        dataStore.updateData { preferences ->
            preferences.copy(accessToken = accessToken)
        }
    }

    override fun saveRefreshToken(refreshToken: String): Unit = runBlocking {
        dataStore.updateData { preferences ->
            preferences.copy(refreshToken = refreshToken)
        }
    }

    override fun saveAccessTokenExpirationTimestamp(expirationTimestamp: Long): Unit = runBlocking {
        dataStore.updateData { preferences ->
            preferences.copy(accessTokenExpirationTimestamp = expirationTimestamp)
        }
    }

    override fun clearAll(): Unit = runBlocking {
        dataStore.updateData { preferences ->
            preferences.copy(
                accessToken = null,
                refreshToken = null,
                accessTokenExpirationTimestamp = 0
            )
        }
    }
}
