package com.aarevalo.tasky.core.data.preferences

import androidx.datastore.core.Serializer
import com.aarevalo.tasky.core.domain.preferences.UserPreferencesData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object UserPreferencesSerializer : Serializer<UserPreferencesData> {
    override val defaultValue: UserPreferencesData
        get() = UserPreferencesData()

    override suspend fun readFrom(input: InputStream): UserPreferencesData {
        return try {
            val bytes = withContext(Dispatchers.IO) {
                input.use { it.readBytes() }
            }

            if (bytes.isEmpty()) {
                defaultValue
            } else {
                Json.decodeFromString(bytes.decodeToString())
            }
        } catch (e: Exception) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: UserPreferencesData, output: OutputStream) {
        withContext(Dispatchers.IO) {
            val jsonString = Json.encodeToString(UserPreferencesData.serializer(), t)
            output.use {
                it.write(jsonString.toByteArray())
            }
        }
    }
}