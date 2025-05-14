package com.aarevalo.tasky.core.data.preferences

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.core.Serializer
import com.aarevalo.tasky.core.data.encrypt.Crypto
import com.aarevalo.tasky.core.domain.preferences.TokenPreferencesData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import java.util.Base64

object TokenPreferencesSerializer : Serializer<TokenPreferencesData> {
    override val defaultValue: TokenPreferencesData
        get() = TokenPreferencesData()

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun readFrom(input: InputStream): TokenPreferencesData {
        val encryptedBytes = withContext(Dispatchers.IO) {
            input.use { it.readBytes() }
        }

        return try {
            if(encryptedBytes.isEmpty()) {
                defaultValue
            } else {
                val encryptedBytesDecoded = Base64.getDecoder()
                    .decode(encryptedBytes)
                val decryptedBytes = Crypto.decrypt(encryptedBytesDecoded)
                val decodedJsonString = decryptedBytes.decodeToString()
                Json.decodeFromString(decodedJsonString)
            }
        } catch(e: Exception) {
            defaultValue
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun writeTo(
        t: TokenPreferencesData,
        output: OutputStream
    ) {
        val json = Json.encodeToString(t)
        val encryptedBytes = Crypto.encrypt(json.toByteArray())
        val encryptedBytesBase64 = Base64.getEncoder()
            .encode(encryptedBytes)

        withContext(Dispatchers.IO) {
            output.use {
                it.write(encryptedBytesBase64)
            }
        }
    }
}
