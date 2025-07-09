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
import com.aarevalo.tasky.agenda.domain.util.PhotoByteLoader
import com.aarevalo.tasky.agenda.presentation.agenda_detail.AgendaItemDetails
import com.aarevalo.tasky.core.data.networking.makeApiCall
import com.aarevalo.tasky.core.domain.util.DataError
import com.aarevalo.tasky.core.domain.util.EmptyResult
import com.aarevalo.tasky.core.domain.util.Result
import com.aarevalo.tasky.core.util.getUtcTimestampFromLocalDate
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

class RetrofitRemoteAgendaDataSource @Inject constructor(
    private val api: TaskyAgendaApi,
    private val photoByteLoader: PhotoByteLoader
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
            apiCall = { api.getAgenda(getUtcTimestampFromLocalDate(date)) },
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
                val photoParts = mutableListOf<MultipartBody.Part>()

                agendaItem.details.photos.forEachIndexed { index, eventPhoto ->
                    if (eventPhoto is EventPhoto.Local) {
                        val photoBytes = photoByteLoader.getBytes(eventPhoto.uri)
                        if (photoBytes != null) {
                            val mediaType = "image/jpeg".toMediaTypeOrNull()
                            val filename = "${eventPhoto.key}.jpg"
                            photoParts.add(
                                MultipartBody.Part.createFormData(
                                    name = "photo${index}",
                                    filename = filename,
                                    body = photoBytes.toRequestBody(mediaType)
                                )
                            )
                        } else {
                            Timber.w("Could not load bytes for local photo with key: %s, URI: %s", eventPhoto.key, eventPhoto.uri)
                        }
                    }
                }
                makeApiCall(
                    apiCall = { api.createEvent(eventRequest, *photoParts.toTypedArray()) },
                    mapper = { it.toAgendaItem() }
                )
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
                val photoParts = mutableListOf<MultipartBody.Part>()

                agendaItem.details.photos.forEachIndexed { index, eventPhoto ->
                    if (eventPhoto is EventPhoto.Local) {
                        val photoBytes = photoByteLoader.getBytes(eventPhoto.uri)
                        if (photoBytes != null) {
                            val mediaType = "image/jpeg".toMediaTypeOrNull()
                            val filename = "${eventPhoto.key}.jpg"
                            photoParts.add(
                                MultipartBody.Part.createFormData(
                                    name = "photo${index}",
                                    filename = filename,
                                    body = photoBytes.toRequestBody(mediaType)
                                )
                            )
                        } else {
                            Timber.w("Could not load bytes for local photo with key: %s, URI: %s", eventPhoto.key, eventPhoto.uri)
                        }
                    }
                }
                makeApiCall(
                    apiCall = { api.updateEvent(eventRequest, *photoParts.toTypedArray()) },
                    mapper = { it.toAgendaItem() }
                )
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
                attendeeResponse.attendee?.takeIf { attendeeResponse.doesUserExist }?.toAttendee()
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

    override suspend fun deleteAgendaItem(agendaItemId: String): EmptyResult<DataError.Network> {
        return when {
            agendaItemId.contains(AgendaItem.PREFIX_EVENT_ID) -> makeApiCall(
                apiCall = { api.deleteEvent(agendaItemId) }
            )
            agendaItemId.contains(AgendaItem.PREFIX_TASK_ID) -> makeApiCall(
                apiCall = { api.deleteTask(agendaItemId) }
            )
            agendaItemId.contains(AgendaItem.PREFIX_REMINDER_ID) -> makeApiCall(
                apiCall = { api.deleteReminder(agendaItemId) }
            )
            else -> Result.Error(DataError.Network.BAD_REQUEST)
        }
    }

    override suspend fun logout(): EmptyResult<DataError.Network> {
        return makeApiCall(apiCall = { api.logout() })
    }
}