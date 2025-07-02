package com.aarevalo.tasky.agenda.data.remote.api

import com.aarevalo.tasky.agenda.data.remote.dto.AgendaResponse
import com.aarevalo.tasky.agenda.data.remote.dto.EventCreateRequest
import com.aarevalo.tasky.agenda.data.remote.dto.EventDto
import com.aarevalo.tasky.agenda.data.remote.dto.EventUpdateRequest
import com.aarevalo.tasky.agenda.data.remote.dto.SyncAgendaRequest
import com.aarevalo.tasky.agenda.data.remote.dto.GetAttendeeResponse
import com.aarevalo.tasky.agenda.data.remote.dto.ReminderDto
import com.aarevalo.tasky.agenda.data.remote.dto.TaskDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface TaskyAgendaApi {

    @GET("authenticate")
    suspend fun checkIfUserIsAuthenticated(): Response<Unit>

    @GET("logout")
    suspend fun logout(): Response<Unit>

    @GET("agenda")
    suspend fun getAgenda(
        @Query("time") time: Long,
    ): Response<AgendaResponse>

    @POST("syncAgenda")
    suspend fun syncAgenda(
        @Body request: SyncAgendaRequest
    ): Response<Unit>

    @GET("fullAgenda")
    suspend fun getFullAgenda(): Response<AgendaResponse>

    @Multipart
    @POST("event")
    suspend fun createEvent(
        @Part("create_event_request")eventRequest: EventCreateRequest,
        @Part vararg photos: MultipartBody.Part
    ): Response<EventDto>

    @GET("event")
    suspend fun getEvent(
        @Query("eventId") id: String
    ): Response<EventDto>

    @DELETE("event")
    suspend fun deleteEvent(
        @Query("eventId") id: String
    ): Response<Unit>

    @Multipart
    @PUT("event")
    suspend fun updateEvent(
        @Part("create_event_request")eventRequest: EventUpdateRequest,
        @Part vararg photos: MultipartBody.Part
    ): Response<EventDto>

    @GET("attendee")
    suspend fun getAttendee(
        @Query("email") email: String
    ): Response<GetAttendeeResponse>

    /** This will remove the local attendee from the event.
    It's intended to be called when the local user is an attendee of an event and deletes it from their agenda. **/
    @DELETE("attendee")
    suspend fun deleteAttendee(
        @Query("eventId") eventId: String,
    ): Response<Unit>

    /** Creates a new task **/
    @POST("task")
    suspend fun createTask(
        @Body taskRequest: TaskDto
    ): Response<Unit>

    /** Updates an existing task **/
    @PUT("task")
    suspend fun updateTask(
        @Body taskRequest: TaskDto
    ): Response<Unit>

    /** Deletes an existing task **/
    @DELETE("task")
    suspend fun deleteTask(
        @Query("taskId") taskId: String
    ): Response<Unit>

    /** Gets an existing task **/
    @GET("task")
    suspend fun getTask(
        @Query("taskId") taskId: String
    ): Response<TaskDto>

    /** Creates a new reminder **/
    @POST("reminder")
    suspend fun createReminder(
        @Body reminderRequest: ReminderDto
    ): Response<Unit>

    /** Updates an existing reminder **/
    @PUT("reminder")
    suspend fun updateReminder(
        @Body reminderRequest: ReminderDto
    ): Response<Unit>

    /** Deletes an existing reminder **/
    @DELETE("reminder")
    suspend fun deleteReminder(
        @Query("reminderId") reminderId: String
    ): Response<Unit>

    /** Gets an existing reminder **/
    @GET("reminder")
    suspend fun getReminder(
        @Query("reminderId") reminderId: String
    ): Response<ReminderDto>

}