package com.aarevalo.tasky.agenda.data.remote.api

import com.aarevalo.tasky.agenda.data.remote.dto.AgendaResponse
import com.aarevalo.tasky.agenda.data.remote.dto.AttendeeUserResponse
import com.aarevalo.tasky.agenda.data.remote.dto.ConfirmUploadRequest
import com.aarevalo.tasky.agenda.data.remote.dto.EventCreateRequest
import com.aarevalo.tasky.agenda.data.remote.dto.EventDto
import com.aarevalo.tasky.agenda.data.remote.dto.EventUpdateRequest
import com.aarevalo.tasky.agenda.data.remote.dto.EventWithUploadUrlsResponse
import com.aarevalo.tasky.agenda.data.remote.dto.LogoutRequest
import com.aarevalo.tasky.agenda.data.remote.dto.ReminderDto
import com.aarevalo.tasky.agenda.data.remote.dto.SyncAgendaRequest
import com.aarevalo.tasky.agenda.data.remote.dto.TaskDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TaskyAgendaApi {

    @POST("auth/logout")
    suspend fun logout(
        @Body body: LogoutRequest
    ): Response<Unit>

    @GET("agenda")
    suspend fun getAgenda(
        @Query("time") timeIso: String,
    ): Response<AgendaResponse>

    @POST("syncAgenda")
    suspend fun syncAgenda(
        @Body request: SyncAgendaRequest
    ): Response<Unit>

    @GET("fullAgenda")
    suspend fun getFullAgenda(): Response<AgendaResponse>

    @POST("event")
    suspend fun createEvent(
        @Body eventRequest: EventCreateRequest,
    ): Response<EventWithUploadUrlsResponse>

    @GET("event/{eventId}")
    suspend fun getEvent(
        @Path("eventId") id: String
    ): Response<EventDto>

    @DELETE("event")
    suspend fun deleteEvent(
        @Query("eventId") id: String,
        @Query("deleteAt") deletedAtIso: String? = null
    ): Response<Unit>

    @PUT("event/{eventId}")
    suspend fun updateEvent(
        @Path("eventId") eventId: String,
        @Body eventRequest: EventUpdateRequest,
    ): Response<EventWithUploadUrlsResponse>

    @POST("event/{eventId}/confirm-upload")
    suspend fun confirmUpload(
        @Path("eventId") eventId: String,
        @Body body: ConfirmUploadRequest
    ): Response<EventWithUploadUrlsResponse>

    @GET("attendee")
    suspend fun getAttendee(
        @Query("email") email: String
    ): Response<AttendeeUserResponse>

    /** This will remove the local attendee from the event. */
    @DELETE("attendee")
    suspend fun deleteAttendee(
        @Query("eventId") eventId: String,
    ): Response<Unit>

    /** Creates a new task **/
    @POST("task")
    suspend fun createTask(
        @Body taskRequest: TaskDto
    ): Response<TaskDto>

    /** Updates an existing task **/
    @PUT("task")
    suspend fun updateTask(
        @Body taskRequest: TaskDto
    ): Response<TaskDto>

    /** Deletes an existing task **/
    @DELETE("task/{taskId}")
    suspend fun deleteTask(
        @Path("taskId") taskId: String,
        @Query("deletedAt") deletedAtIso: String? = null
    ): Response<Unit>

    /** Gets an existing task **/
    @GET("task/{taskId}")
    suspend fun getTask(
        @Path("taskId") taskId: String
    ): Response<TaskDto>

    /** Creates a new reminder **/
    @POST("reminder")
    suspend fun createReminder(
        @Body reminderRequest: ReminderDto
    ): Response<ReminderDto>

    /** Updates an existing reminder **/
    @PUT("reminder")
    suspend fun updateReminder(
        @Body reminderRequest: ReminderDto
    ): Response<ReminderDto>

    /** Deletes an existing reminder **/
    @DELETE("reminder/{reminderId}")
    suspend fun deleteReminder(
        @Path("reminderId") reminderId: String,
        @Query("deletedAt") deletedAtIso: String? = null
    ): Response<Unit>

    /** Gets an existing reminder **/
    @GET("reminder/{reminderId}")
    suspend fun getReminder(
        @Path("reminderId") reminderId: String
    ): Response<ReminderDto>

}