package com.aarevalo.tasky.agenda.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aarevalo.tasky.agenda.data.local.converter.PhotoListConverter
import com.aarevalo.tasky.agenda.data.local.dao.AttendeeDao
import com.aarevalo.tasky.agenda.data.local.dao.EventDao
import com.aarevalo.tasky.agenda.data.local.dao.PendingItemSyncDao
import com.aarevalo.tasky.agenda.data.local.dao.PhotoDao
import com.aarevalo.tasky.agenda.data.local.dao.ReminderDao
import com.aarevalo.tasky.agenda.data.local.dao.TaskDao
import com.aarevalo.tasky.agenda.data.local.entity.AttendeeEntity
import com.aarevalo.tasky.agenda.data.local.entity.EventEntity
import com.aarevalo.tasky.agenda.data.local.entity.PendingItemSyncEntity
import com.aarevalo.tasky.agenda.data.local.entity.PhotoEntity
import com.aarevalo.tasky.agenda.data.local.entity.ReminderEntity
import com.aarevalo.tasky.agenda.data.local.entity.TaskEntity

@Database(
    entities = [
        EventEntity::class,
        TaskEntity::class,
        ReminderEntity::class,
        AttendeeEntity::class,
        PhotoEntity::class,
        PendingItemSyncEntity::class
    ],
    version = 5
)
@TypeConverters(PhotoListConverter::class)
abstract class AgendaDatabase : RoomDatabase() {

    abstract val eventDao: EventDao
    abstract val taskDao: TaskDao
    abstract val reminderDao: ReminderDao
    abstract val attendeeDao: AttendeeDao
    abstract val photoDao: PhotoDao
    abstract val pendingItemSyncDao: PendingItemSyncDao

}