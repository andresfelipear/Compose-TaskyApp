package com.aarevalo.tasky.core.data.preferences

import androidx.datastore.core.Serializer
import com.aarevalo.tasky.core.data.auth.AuthenticatedUserSerializable
import com.aarevalo.tasky.core.data.encrypt.Crypto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import java.util.Base64

object EncryptAuthenticatedUser : Serializer<AuthenticatedUserSerializable> {
    override val defaultValue: AuthenticatedUserSerializable
        get() = AuthenticatedUserSerializable()

    override suspend fun readFrom(input: InputStream): AuthenticatedUserSerializable {
        val encryptedBytes = withContext(Dispatchers.IO) {
            input.use { it.readBytes() }
        }

        val encryptedBytesDecoded = Base64.getDecoder()
            .decode(encryptedBytes)
        val decryptedBytes = Crypto.decrypt(encryptedBytesDecoded)
        val decodedJsonString = decryptedBytes.decodeToString()

        return Json.decodeFromString(decodedJsonString)
    }

    override suspend fun writeTo(
        t: AuthenticatedUserSerializable,
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