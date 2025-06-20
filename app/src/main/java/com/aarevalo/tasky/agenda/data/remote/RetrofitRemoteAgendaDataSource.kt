package com.aarevalo.tasky.agenda.data.remote

import com.aarevalo.tasky.agenda.data.remote.api.TaskyAgendaApi
import com.aarevalo.tasky.agenda.data.remote.dto.SyncAgendaRequest
import com.aarevalo.tasky.agenda.data.remote.mappers.toAgendaItem
import com.aarevalo.tasky.agenda.data.remote.mappers.toAttendee
import com.aarevalo.tasky.agenda.data.remote.mappers.toEventRequest
import com.aarevalo.tasky.agenda.data.remote.mappers.toReminderDto
import com.aarevalo.tasky.agenda.data.remote.mappers.toTaskDto
import com.aarevalo.tasky.agenda.domain.RemoteAgendaDataSource
import com.aarevalo.tasky.agenda.domain.model.AgendaItem
import com.aarevalo.tasky.agenda.domain.model.AgendaItemType
import com.aarevalo.tasky.agenda.domain.model.Attendee
import com.aarevalo.tasky.agenda.domain.model.EventPhoto
import com.aarevalo.tasky.agenda.domain.util.PhotoByteLoader
import com.aarevalo.tasky.agenda.presentation.agenda_detail.AgendaItemDetails
import com.aarevalo.tasky.core.data.networking.responseToResult
import com.aarevalo.tasky.core.domain.util.DataError
import com.aarevalo.tasky.core.domain.util.EmptyResult
import com.aarevalo.tasky.core.domain.util.Result
import com.aarevalo.tasky.core.domain.util.asEmptyDataResult
import com.aarevalo.tasky.core.domain.util.map
import com.aarevalo.tasky.core.util.getUtcTimestampFromLocalDate
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.LocalDate
import javax.inject.Inject

class RetrofitRemoteAgendaDataSource @Inject constructor(
    private val api: TaskyAgendaApi,
    private val photoByteLoader: PhotoByteLoader
): RemoteAgendaDataSource {
    override suspend fun fetchFullAgenda(): Result<List<AgendaItem>, DataError.Network> {
        val response = api.getFullAgenda()
        return responseToResult(response).map {
            it.events.map { eventDto -> eventDto.toAgendaItem() } +
            it.tasks.map { taskDto ->  taskDto.toAgendaItem() } +
            it.reminders.map { reminderDto ->  reminderDto.toAgendaItem() }
        }
    }

    override suspend fun fetchAgendaItems(
        date: LocalDate
    ): Result<List<AgendaItem>, DataError.Network> {
        val response = api.getAgenda(getUtcTimestampFromLocalDate(date))
        return responseToResult(response).map {
            it.events.map { eventDto -> eventDto.toAgendaItem() } +
            it.tasks.map { taskDto ->  taskDto.toAgendaItem() } +
            it.reminders.map { reminderDto ->  reminderDto.toAgendaItem() }
        }
    }

    override suspend fun fetchAgendaItem(agendaItemId: String, type: AgendaItemType): Result<AgendaItem?, DataError.Network> {
        when(type){
            AgendaItemType.EVENT -> {
                val response = api.getEvent(agendaItemId)
                return responseToResult(response).map { it.toAgendaItem() }
            }
            AgendaItemType.TASK -> {
                val response = api.getTask(agendaItemId)
                return responseToResult(response).map { it.toAgendaItem() }
            }
            AgendaItemType.REMINDER -> {
                val response = api.getReminder(agendaItemId)
                return responseToResult(response).map { it.toAgendaItem() }
            }
            else -> {
                return Result.Error(DataError.Network.BAD_REQUEST)
            }
        }
    }

    override suspend fun createAgendaItem(agendaItem: AgendaItem): Result<Unit, DataError.Network> {
        when(agendaItem.details){
            is AgendaItemDetails.Event -> {

                val eventRequest = agendaItem.toEventRequest()
                val photoParts = mutableListOf<MultipartBody.Part>()

                agendaItem.details.photos.forEachIndexed{ index, eventPhoto ->
                    if(eventPhoto is EventPhoto.Local){
                        val photoBytes = photoByteLoader.getBytes(eventPhoto.uri)
                        if(photoBytes != null){
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
                            println("Warning: Could not load bytes for local photo with key: ${eventPhoto.key}, URI: ${eventPhoto.uri}")
                        }
                    }
                }

                val response = api.createEvent(
                    eventRequest,
                    *photoParts.toTypedArray()
                )
                return responseToResult(response).map { it.toAgendaItem() }
            }
            is AgendaItemDetails.Task -> {
                val response = api.createTask(agendaItem.toTaskDto())
                return responseToResult(response).asEmptyDataResult()
            }
            is AgendaItemDetails.Reminder -> {
                val response = api.createReminder(agendaItem.toReminderDto())
                return responseToResult(response).asEmptyDataResult()
            }
        }
    }

    override suspend fun updateAgendaItem(agendaItem: AgendaItem, deletedPhotoKeys: List<String>, isGoing: Boolean): Result<Unit, DataError.Network> {
        when(agendaItem.details){
            is AgendaItemDetails.Event -> {

                val eventRequest = agendaItem.toEventRequest().copy(
                    deletedPhotoKeys = deletedPhotoKeys,
                    isGoing = isGoing
                )

                val photoParts = mutableListOf<MultipartBody.Part>()

                agendaItem.details.photos.forEachIndexed{ index, eventPhoto ->
                    if(eventPhoto is EventPhoto.Local){
                        val photoBytes = photoByteLoader.getBytes(eventPhoto.uri)
                        if(photoBytes != null){
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
                            println("Warning: Could not load bytes for local photo with key: ${eventPhoto.key}, URI: ${eventPhoto.uri}")
                        }
                    }
                }

                val response = api.updateEvent(
                    eventRequest,
                    *photoParts.toTypedArray()
                )
                return responseToResult(response).map { it.toAgendaItem() }
            }
            is AgendaItemDetails.Task -> {
                val response = api.updateTask(agendaItem.toTaskDto())
                return responseToResult(response).asEmptyDataResult()
            }
            is AgendaItemDetails.Reminder -> {
                val response = api.updateReminder(agendaItem.toReminderDto())
                return responseToResult(response).asEmptyDataResult()
            }
        }
    }

    override suspend fun fetchAttendee(email: String): Result<Attendee?, DataError.Network> {
        val response = api.getAttendee(email)
        return responseToResult(response).map { attendeeResponse ->
            attendeeResponse.attendee?.takeIf { attendeeResponse.doesUserExist }?.toAttendee()
        }
    }

    override suspend fun deleteAttendee(eventId: String): EmptyResult<DataError.Network> {
        val response = api.deleteAttendee(eventId)
        return responseToResult(response).asEmptyDataResult()
    }

    override suspend fun syncAgenda(
        deletedEventIds: List<String>,
        deletedTaskIds: List<String>,
        deletedReminderIds: List<String>
    ): EmptyResult<DataError.Network> {
        val response = api.syncAgenda(
            SyncAgendaRequest(
                deletedEventIds = deletedEventIds,
                deletedTaskIds = deletedTaskIds,
                deletedReminderIds = deletedReminderIds
            )
        )
        return responseToResult(response).asEmptyDataResult()
    }

    override suspend fun deleteAgendaItem(agendaItemId: String, type: AgendaItemType): EmptyResult<DataError.Network> {
        when(type){
            AgendaItemType.EVENT -> {
                val response = api.deleteEvent(agendaItemId)
                return responseToResult(response).asEmptyDataResult()
            }
            AgendaItemType.TASK -> {
                val response = api.deleteTask(agendaItemId)
                return responseToResult(response).asEmptyDataResult()
            }
            AgendaItemType.REMINDER -> {
                val response = api.deleteReminder(agendaItemId)
                return responseToResult(response).asEmptyDataResult()
            }
        }
    }

    override suspend fun logout(): EmptyResult<DataError.Network> {
        val response = responseToResult(api.logout())
        return response.asEmptyDataResult()
    }
}