package com.aarevalo.tasky.core.data.preferences

import androidx.datastore.core.DataStore
import com.aarevalo.tasky.core.data.auth.AuthenticatedUserSerializable
import com.aarevalo.tasky.core.data.auth.toAuthenticatedUser
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
                accessToken = session?.accessToken ?: "",
                refreshToken = session?.refreshToken ?: "",
                accessTokenExpirationTimestamp = session?.accessTokenExpirationTimestamp ?: 0,
                userId = session?.userId ?: "",
                fullName = session?.fullName ?: ""
            )
        }
    }
}