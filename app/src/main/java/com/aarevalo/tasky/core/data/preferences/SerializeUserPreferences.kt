package com.aarevalo.tasky.core.data.preferences

import androidx.datastore.core.DataStore
import com.aarevalo.tasky.core.domain.preferences.UserPreferences
import com.aarevalo.tasky.core.domain.preferences.UserPreferencesData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class SerializeUserPreferences(
    private val dataStore: DataStore<UserPreferencesData>,
): UserPreferences {

    override fun loadEmail(): String? = runBlocking {
        dataStore.data.map {
            it.email
        }
            .first()
    }

    override fun loadFullName(): String? = runBlocking {
        dataStore.data.map {
            it.fullName
        }
            .first()
    }

    override fun loadUserId(): String? = runBlocking {
        dataStore.data.map {
            it.userId
        }
            .first()
    }

    override fun saveEmail(email: String): Unit = runBlocking {
        dataStore.updateData { preferences ->
            preferences.copy(email = email)
        }
    }

    override fun saveFullName(fullName: String): Unit = runBlocking {
        dataStore.updateData { preferences ->
            preferences.copy(fullName = fullName)
        }
    }

    override fun saveUserId(userId: String): Unit = runBlocking {
        dataStore.updateData { preferences ->
            preferences.copy(userId = userId)
        }
    }

    override fun clearAll(): Unit = runBlocking {
        dataStore.updateData { preferences ->
            preferences.copy(
                userId = null,
                fullName = null,
                email = null
            )
        }
    }
}