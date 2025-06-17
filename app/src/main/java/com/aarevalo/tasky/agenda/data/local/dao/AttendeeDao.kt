package com.aarevalo.tasky.agenda.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aarevalo.tasky.agenda.data.local.entity.AttendeeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendeeDao {
    @Upsert
    suspend fun upsertAttendee(attendee: AttendeeEntity)

    @Upsert
    suspend fun upsertAttendees(attendees: List<AttendeeEntity>)

    @Query("SELECT * FROM attendees")
    fun getAttendees(): Flow<List<AttendeeEntity>>

    @Query("SELECT * FROM attendees WHERE attendeeUserId = :id")
    suspend fun getAttendeeById(id: String): AttendeeEntity?

    @Query("DELETE FROM attendees WHERE attendeeUserId = :id")
    suspend fun deleteAttendeeById(id: String)

    @Query("DELETE FROM attendees")
    suspend fun deleteAllAttendees()

    @Query("SELECT * FROM attendees WHERE eventId = :eventId")
    fun getAttendeesByEventId(eventId: String): Flow<List<AttendeeEntity>>

}