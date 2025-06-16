package com.aarevalo.tasky.agenda.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aarevalo.tasky.agenda.data.local.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Upsert
    suspend fun upsertReminder(reminder: ReminderEntity)

    @Upsert
    suspend fun upsertReminders(reminders: List<ReminderEntity>)

    @Query("SELECT * FROM reminders")
    fun getReminders(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE reminderId = :id")
    suspend fun getReminderById(id: String): ReminderEntity?

    @Query("DELETE FROM reminders WHERE reminderId = :id")
    suspend fun deleteReminderById(id: String)

    @Query("DELETE FROM reminders")
    suspend fun deleteAllReminders()

    @Query("SELECT * FROM reminders WHERE time >= :startOfDay AND time < :endOfNextDay ORDER BY time ASC")
    fun getRemindersForDay(startOfDay: Long, endOfNextDay: Long): Flow<List<ReminderEntity>>

}