package com.aarevalo.tasky.agenda.data.util

import android.content.Context
import android.net.Uri
import com.aarevalo.tasky.agenda.domain.util.PhotoByteLoader
import com.aarevalo.tasky.core.domain.util.DispatcherProvider
import kotlinx.coroutines.withContext

class AndroidPhotoByteLoader(
    private val context: Context,
    private val dispatcher: DispatcherProvider
) : PhotoByteLoader {
    override suspend fun getBytes(uri: String): ByteArray? {
        return withContext(dispatcher.io) {
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