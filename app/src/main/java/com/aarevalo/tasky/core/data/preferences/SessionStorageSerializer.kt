package com.aarevalo.tasky.core.data.preferences

import androidx.datastore.core.DataStore
import com.aarevalo.tasky.core.data.auth.AuthenticatedUserSerializable
import com.aarevalo.tasky.core.domain.preferences.SessionStorage
import com.aarevalo.tasky.core.domain.user.AuthenticatedUser
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionStorageSerializer @Inject constructor(
    private val dataStore: DataStore<AuthenticatedUserSerializable>
): SessionStorage{
    override suspend fun getSession(): AuthenticatedUser? {
        return dataStore.data.map {
            it.toAuthenticatedUser()
        }
            .firstOrNull()
    }

    override suspend fun setSession(session: AuthenticatedUser?) {
        dataStore.updateData { preferences ->
            preferences.copy(
                accessToken = session?.accessToken ?: preferences.accessToken,
                refreshToken = session?.refreshToken ?: preferences.refreshToken,
                accessTokenExpirationTimestamp = session?.accessTokenExpirationTimestamp ?: preferences.accessTokenExpirationTimestamp,
                userId = session?.userId ?: preferences.userId,
                fullName = session?.fullName ?: preferences.fullName
            )
        }
    }
}