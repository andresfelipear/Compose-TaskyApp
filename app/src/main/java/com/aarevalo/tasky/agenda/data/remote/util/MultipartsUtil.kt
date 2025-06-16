package com.aarevalo.tasky.agenda.data.remote.util

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

object MultipartUtils {

    fun createJsonRequestBody(json: String): MultipartBody.Part {
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = json.toRequestBody(mediaType)
        return MultipartBody.Part.createFormData("create_event_request", null, requestBody)
    }

    fun createFilePart(
        fieldName: String, // e.g., "photo0", "photo1"
        file: File,
        mediaType: String // e.g., "image/jpeg", "image/png"
    ): MultipartBody.Part {
        val requestBody = file.asRequestBody(mediaType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(fieldName, file.name, requestBody)
    }

    fun createByteArrayPart(
        fieldName: String, // e.g., "photo0", "photo1"
        bytes: ByteArray,
        filename: String, // e.g., "my_photo.jpg"
        mediaType: String // e.g., "image/jpeg", "image/png"
    ): MultipartBody.Part {
        val requestBody = bytes.toRequestBody(mediaType.toMediaTypeOrNull(), 0, bytes.size)
        return MultipartBody.Part.createFormData(fieldName, filename, requestBody)
    }
}