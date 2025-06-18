package com.aarevalo.tasky.agenda.data.util

import android.content.Context
import android.net.Uri
import com.aarevalo.tasky.agenda.domain.util.PhotoByteLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AndroidPhotoByteLoader(private val context: Context) : PhotoByteLoader {
    override suspend fun getBytes(uri: String): ByteArray? {
        return withContext(Dispatchers.IO) {
            try {
                val contentUri = Uri.parse(uri)
                context.contentResolver.openInputStream(contentUri)?.use { inputStream ->
                    inputStream.readBytes()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}