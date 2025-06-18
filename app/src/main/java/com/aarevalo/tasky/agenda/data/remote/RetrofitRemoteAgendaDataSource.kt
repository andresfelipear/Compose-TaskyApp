package com.aarevalo.tasky.agenda.data.remote

import com.aarevalo.tasky.agenda.data.remote.api.TaskyAgendaApi
import com.aarevalo.tasky.agenda.data.remote.dto.EventRequest
import com.aarevalo.tasky.agenda.data.remote.mappers.toAgendaItem
import com.aarevalo.tasky.agenda.data.remote.mappers.toEventRequest
import com.aarevalo.tasky.agenda.data.remote.mappers.toPhotoDto
import com.aarevalo.tasky.agenda.data.remote.mappers.toReminderDto
import com.aarevalo.tasky.agenda.data.remote.mappers.toTaskDto
import com.aarevalo.tasky.agenda.domain.RemoteAgendaDataSource
import com.aarevalo.tasky.agenda.domain.model.AgendaItem
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
import com.squareup.moshi.Moshi
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.LocalDate
import javax.inject.Inject

class RetrofitRemoteAgendaDataSource @Inject constructor(
    private val api: TaskyAgendaApi,
    private val moshi: Moshi,
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

    override suspend fun fetchAgendaItem(agendaItemId: String): Result<AgendaItem?, DataError.Network> {
        TODO("Not yet implemented")
    }

    override suspend fun createAgendaItem(agendaItem: AgendaItem): EmptyResult<DataError.Network> {
        when(agendaItem.details){
            is AgendaItemDetails.Event -> {

                val eventRequest = agendaItem.toEventRequest()
                val photoParts = mutableListOf<MultipartBody.Part>()

                agendaItem.details.photos.forEachIndexed{ index, eventPhoto ->
                    if(eventPhoto is EventPhoto.Local){
                        val photoBytes = photoByteLoader.getBytes(eventPhoto.uri)
                        if(photoBytes != null){
                            val mediaType = "image/jpeg".toMediaType()
                            val filename = "${eventPhoto.key}.jpg"

                            photoParts.add(
                                MultipartBody.Part.createFormData(
                                    name = "photo${index}",
                                    filename = filename,
                                    body = eventPhoto.uriString.toRequestBody(mediaType)
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
                return responseToResult(response).asEmptyDataResult()
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

    override suspend fun updateAgendaItem(agendaItem: AgendaItem): Result<AgendaItem, DataError.Network> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchAttendee(email: String): Result<Attendee, DataError.Network> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAttendee(eventId: String): EmptyResult<DataError.Network> {
        TODO("Not yet implemented")
    }

    override suspend fun syncAgenda(
        deletedEventIds: List<String>,
        deletedTaskIds: List<String>,
        deletedReminderIds: List<String>
    ): EmptyResult<DataError.Network> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAgendaItem(agendaItemId: String): EmptyResult<DataError.Network> {
        TODO("Not yet implemented")
    }

    override suspend fun logout(): EmptyResult<DataError.Network> {
        val response = responseToResult(api.logout())
        return response.asEmptyDataResult()
    }

}