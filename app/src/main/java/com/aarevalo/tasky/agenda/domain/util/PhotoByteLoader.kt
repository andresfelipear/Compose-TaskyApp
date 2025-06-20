package com.aarevalo.tasky.agenda.domain.util

interface PhotoByteLoader {
    suspend fun getBytes(uri: String): ByteArray?
}