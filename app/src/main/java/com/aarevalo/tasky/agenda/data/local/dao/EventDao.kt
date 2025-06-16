package com.aarevalo.tasky.agenda.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aarevalo.tasky.agenda.data.local.entity.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Upsert
    suspend fun upsertEvent(event: EventEntity)

    @Upsert
    suspend fun upsertEvents(events: List<EventEntity>)

    @Query("SELECT * FROM events ORDER BY fromTimestamp DESC")
    fun getEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE eventId = :id")
    suspend fun getEventById(id: String): EventEntity?

    @Query("DELETE FROM events WHERE eventId = :id")
    suspend fun deleteEventById(id: String)

    @Query("DELETE FROM events")
    suspend fun deleteAllEvents()

    @Query("SELECT * FROM events WHERE fromTimestamp >= :startOfDay AND fromTimestamp < :endOfNextDay ORDER BY fromTimestamp ASC")
    fun getEventsForDay(startOfDay: Long, endOfNextDay: Long): Flow<List<EventEntity>>

}