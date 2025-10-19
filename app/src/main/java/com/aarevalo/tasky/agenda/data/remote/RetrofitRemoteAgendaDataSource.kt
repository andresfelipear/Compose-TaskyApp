package com.aarevalo.tasky.agenda.data.remote

import com.aarevalo.tasky.agenda.data.remote.api.TaskyAgendaApi
import com.aarevalo.tasky.agenda.data.remote.dto.SyncAgendaRequest
import com.aarevalo.tasky.agenda.data.remote.mappers.toAgendaItem
import com.aarevalo.tasky.agenda.data.remote.mappers.toAttendee
import com.aarevalo.tasky.agenda.data.remote.mappers.toEventCreateRequest
import com.aarevalo.tasky.agenda.data.remote.mappers.toEventUpdateRequest
import com.aarevalo.tasky.agenda.data.remote.mappers.toReminderDto
import com.aarevalo.tasky.agenda.data.remote.mappers.toTaskDto
import com.aarevalo.tasky.agenda.domain.RemoteAgendaDataSource
import com.aarevalo.tasky.agenda.domain.model.AgendaItem
import com.aarevalo.tasky.agenda.domain.model.AgendaItemType
import com.aarevalo.tasky.agenda.domain.model.Attendee
import com.aarevalo.tasky.agenda.domain.model.EventPhoto
import com.aarevalo.tasky.core.domain.preferences.SessionStorage
import com.aarevalo.tasky.agenda.domain.util.PhotoByteLoader
import com.aarevalo.tasky.agenda.presentation.agenda_detail.AgendaItemDetails
import com.aarevalo.tasky.core.data.networking.makeApiCall
import com.aarevalo.tasky.core.domain.util.DataError
import com.aarevalo.tasky.core.domain.util.EmptyResult
import com.aarevalo.tasky.core.domain.util.Result
import com.aarevalo.tasky.core.util.getUtcTimestampFromLocalDate
import com.aarevalo.tasky.core.util.millisToIsoInstantString
import com.aarevalo.tasky.agenda.data.remote.dto.LogoutRequest
import com.aarevalo.tasky.agenda.data.remote.dto.ConfirmUploadRequest
import com.aarevalo.tasky.agenda.data.remote.dto.EventWithUploadUrlsResponse
import com.aarevalo.tasky.agenda.data.remote.dto.UploadUrlDto
import com.aarevalo.tasky.agenda.presentation.agenda_detail.asEventDetails
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import java.time.ZonedDateTime
import java.time.LocalDate
import javax.inject.Inject

class RetrofitRemoteAgendaDataSource @Inject constructor(
    private val api: TaskyAgendaApi,
    private val photoByteLoader: PhotoByteLoader,
): RemoteAgendaDataSource {

    override suspend fun fetchFullAgenda(): Result<List<AgendaItem>, DataError.Network> {
        return makeApiCall(
            apiCall = { api.getFullAgenda() },
            mapper = { fullAgendaResponse ->
                fullAgendaResponse.events.map { it.toAgendaItem() } +
                    fullAgendaResponse.tasks.map { it.toAgendaItem() } +
                    fullAgendaResponse.reminders.map { it.toAgendaItem() }
            }
        )
    }

    override suspend fun fetchAgendaItems(
        date: LocalDate
    ): Result<List<AgendaItem>, DataError.Network> {
        return makeApiCall(
            apiCall = { api.getAgenda(millisToIsoInstantString(getUtcTimestampFromLocalDate(date))) },
            mapper = { agendaResponse ->
                agendaResponse.events.map { it.toAgendaItem() } +
                    agendaResponse.tasks.map { it.toAgendaItem() } +
                    agendaResponse.reminders.map { it.toAgendaItem() }
            }
        )
    }

    override suspend fun fetchAgendaItem(agendaItemId: String, type: AgendaItemType): Result<AgendaItem?, DataError.Network> {
        return when (type) {
            AgendaItemType.EVENT -> makeApiCall(apiCall = {
                api.getEvent(agendaItemId) }, mapper = { it.toAgendaItem() }
            )
            AgendaItemType.TASK -> makeApiCall(
                apiCall = { api.getTask(agendaItemId) }, mapper = { it.toAgendaItem() }
            )
            AgendaItemType.REMINDER -> makeApiCall(
                apiCall = { api.getReminder(agendaItemId) }, mapper = { it.toAgendaItem() }
            )
        }
    }

    override suspend fun createAgendaItem(agendaItem: AgendaItem): Result<AgendaItem?, DataError.Network> {
        return when (agendaItem.details) {
            is AgendaItemDetails.Event -> {
                val eventRequest = agendaItem.toEventCreateRequest()
                val createResult = makeApiCall(
                    apiCall = { api.createEvent(eventRequest) },
                    mapper = { it }
                )
                when (createResult) {
                    is Result.Error -> createResult
                    is Result.Success -> {
                        val created: EventWithUploadUrlsResponse = createResult.data
                        val uploadKeys = uploadPhotos(created.uploadUrls, agendaItem)
                        if (uploadKeys.isNotEmpty()) {
                            val confirmResult = makeApiCall(
                                apiCall = { api.confirmUpload(created.event.id, ConfirmUploadRequest(uploadKeys)) },
                                mapper = { it.event.toAgendaItem() }
                            )
                            return confirmResult
                        }
                        Result.Success(created.event.toAgendaItem())
                    }
                }
            }
            is AgendaItemDetails.Task -> {
                Timber.d("Creating task remotely! API call")
                makeApiCall(apiCall = { api.createTask(agendaItem.toTaskDto()) }, mapper = { null })
            }
            is AgendaItemDetails.Reminder -> {
                makeApiCall(apiCall = { api.createReminder(agendaItem.toReminderDto()) }, mapper = { null })
            }
        }
    }

    override suspend fun updateAgendaItem(agendaItem: AgendaItem, deletedPhotoKeys: List<String>, isGoing: Boolean): Result<AgendaItem?, DataError.Network> {
        return when (agendaItem.details) {
            is AgendaItemDetails.Event -> {
                val eventRequest = agendaItem.toEventUpdateRequest().copy(
                    deletedPhotoKeys = deletedPhotoKeys,
                    isGoing = isGoing
                )
                val updateResult = makeApiCall(
                    apiCall = { api.updateEvent(agendaItem.id, eventRequest) },
                    mapper = { it }
                )
                when (updateResult) {
                    is Result.Error -> updateResult
                    is Result.Success -> {
                        val updated: EventWithUploadUrlsResponse = updateResult.data
                        val uploadKeys = uploadPhotos(updated.uploadUrls, agendaItem)
                        if (uploadKeys.isNotEmpty()) {
                            val confirmResult = makeApiCall(
                                apiCall = { api.confirmUpload(updated.event.id, ConfirmUploadRequest(uploadKeys)) },
                                mapper = { it.event.toAgendaItem() }
                            )
                            return confirmResult
                        }
                        Result.Success(updated.event.toAgendaItem())
                    }
                }
            }
            is AgendaItemDetails.Task -> {
                makeApiCall(apiCall = { api.updateTask(agendaItem.toTaskDto()) }, mapper = { null })
            }
            is AgendaItemDetails.Reminder -> {
                makeApiCall(apiCall = { api.updateReminder(agendaItem.toReminderDto()) }, mapper = { null })
            }
        }
    }

    override suspend fun fetchAttendee(email: String): Result<Attendee?, DataError.Network> {
        return makeApiCall(
            apiCall = { api.getAttendee(email) },
            mapper = { attendeeResponse ->
                Attendee(
                    userId = attendeeResponse.userId,
                    eventId = "",
                    fullName = attendeeResponse.fullName,
                    email = attendeeResponse.email,
                    isGoing = true,
                    remindAt = ZonedDateTime.now()
                )
            }
        )
    }

    override suspend fun deleteAttendee(eventId: String): EmptyResult<DataError.Network> {
        return makeApiCall(apiCall = { api.deleteAttendee(eventId) })
    }

    override suspend fun syncAgenda(
        deletedEventIds: List<String>,
        deletedTaskIds: List<String>,
        deletedReminderIds: List<String>
    ): EmptyResult<DataError.Network> {
        return makeApiCall(
            apiCall = {
                api.syncAgenda(
                    SyncAgendaRequest(
                        deletedEventIds = deletedEventIds,
                        deletedTaskIds = deletedTaskIds,
                        deletedReminderIds = deletedReminderIds
                    )
                )
            }
        )
    }

    override suspend fun deleteAgendaItem(agendaItemId: String, itemType: AgendaItemType): EmptyResult<DataError.Network> {
        return when (itemType) {
            AgendaItemType.EVENT -> makeApiCall(
                apiCall = { api.deleteEvent(agendaItemId) }
            )
            AgendaItemType.TASK -> makeApiCall(
                apiCall = { api.deleteTask(agendaItemId) }
            )
            AgendaItemType.REMINDER -> makeApiCall(
                apiCall = { api.deleteReminder(agendaItemId) }
            )
        }
    }

    override suspend fun logout(
        refreshToken: String
    ): EmptyResult<DataError.Network> {
        return makeApiCall(apiCall = {
            api.logout(LogoutRequest(refreshToken))
        })
    }

    private suspend fun uploadPhotos(uploadUrls: List<UploadUrlDto>, agendaItem: AgendaItem): List<String> {
        if (uploadUrls.isEmpty()) return emptyList()
        val client = OkHttpClient()
        val uploadedKeys = mutableListOf<String>()
        uploadUrls.forEach { upload ->
            val localPhoto = agendaItem.details.asEventDetails?.photos?.firstOrNull { it.key == upload.photoKey }
            if (localPhoto is EventPhoto.Local) {
                val bytes = photoByteLoader.getBytes(localPhoto.uri)
                if (bytes != null) {
                    val body = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                    val request = Request.Builder().url(upload.url).put(body).build()
                    try {
                        client.newCall(request).execute().use { resp ->
                            if (resp.isSuccessful) {
                                uploadedKeys.add(upload.uploadKey)
                            } else {
                                Timber.w("Photo upload failed for key=%s, code=%s", upload.photoKey, resp.code)
                            }
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Photo upload exception for key=%s", upload.photoKey)
                    }
                }
            }
        }
        return uploadedKeys
    }
}