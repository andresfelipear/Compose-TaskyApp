package com.aarevalo.tasky.agenda.data.local

import android.database.sqlite.SQLiteFullException
import androidx.room.withTransaction
import com.aarevalo.tasky.core.domain.util.Result
import com.aarevalo.tasky.agenda.data.local.dao.AttendeeDao
import com.aarevalo.tasky.agenda.data.local.dao.EventDao
import com.aarevalo.tasky.agenda.data.local.dao.PhotoDao
import com.aarevalo.tasky.agenda.data.local.dao.ReminderDao
import com.aarevalo.tasky.agenda.data.local.dao.TaskDao
import com.aarevalo.tasky.agenda.data.local.database.AgendaDatabase
import com.aarevalo.tasky.agenda.data.local.mappers.toAgendaItem
import com.aarevalo.tasky.agenda.data.local.mappers.toAttendee
import com.aarevalo.tasky.agenda.data.local.mappers.toAttendeeEntity
import com.aarevalo.tasky.agenda.data.local.mappers.toEventEntity
import com.aarevalo.tasky.agenda.data.local.mappers.toEventPhoto
import com.aarevalo.tasky.agenda.data.local.mappers.toPhotoEntity
import com.aarevalo.tasky.agenda.data.local.mappers.toReminderEntity
import com.aarevalo.tasky.agenda.data.local.mappers.toTaskEntity
import com.aarevalo.tasky.agenda.domain.LocalAgendaDataSource
import com.aarevalo.tasky.agenda.domain.model.AgendaItem
import com.aarevalo.tasky.agenda.presentation.agenda_detail.AgendaItemDetails
import com.aarevalo.tasky.agenda.presentation.agenda_detail.asEventDetails
import com.aarevalo.tasky.core.domain.util.DataError
import com.aarevalo.tasky.core.util.parseLocalDateToTimestamp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class RoomLocalAgendaDataSource @Inject constructor(
    private val db: AgendaDatabase,
    private val eventDao: EventDao,
    private val taskDao: TaskDao,
    private val reminderDao: ReminderDao,
    private val attendeeDao: AttendeeDao,
    private val photoDao: PhotoDao
): LocalAgendaDataSource{
    override fun getAgendaItems(): Flow<List<AgendaItem>> {

        val eventsFlow = eventDao.getEvents().map { eventsEntities -> eventsEntities.map { eventEntity ->
            val attendees = attendeeDao.getAttendeesByEventId(eventEntity.eventId).map { it.toAttendee() }
            val photos = photoDao.getPhotosByKeys(eventEntity.photoKeys).map { it.toEventPhoto() }
            eventEntity.toAgendaItem().copy(
                details = (eventEntity.toAgendaItem().details as AgendaItemDetails.Event).copy(
                    attendees = attendees,
                    photos = photos
                )
            )
        }
        }

        val remindersFlow = reminderDao.getReminders()
            .map { remindersEntities -> remindersEntities
                .map { reminderEntity ->
            reminderEntity.toAgendaItem()
            }
        }

        val tasksFlow = taskDao.getTasks()
            .map { tasksEntities ->
                tasksEntities.map { taskEntity ->
                        taskEntity.toAgendaItem()
                    }
            }

        return combine(
            eventsFlow,
            remindersFlow,
            tasksFlow,
        ){ events, reminders, tasks ->
            (events + reminders + tasks).sortedBy { it.fromTime}
        }
    }

    override fun getAgendaItemsByDate(date: LocalDate): Flow<List<AgendaItem>> {

        val eventsFlow = eventDao.getEventsForDay(
            startOfDay = parseLocalDateToTimestamp(date),
            endOfDay = parseLocalDateToTimestamp(date.plusDays(1))
        ).map { eventsEntities -> eventsEntities.map { eventEntity ->
            val attendees = attendeeDao.getAttendeesByEventId(eventEntity.eventId).map { it.toAttendee() }
            val photos = photoDao.getPhotosByKeys(eventEntity.photoKeys).map { it.toEventPhoto() }
            eventEntity.toAgendaItem().copy(
                details = (eventEntity.toAgendaItem().details as AgendaItemDetails.Event).copy(
                    attendees = attendees,
                    photos = photos
                )
            )
        }
        }

        val remindersFlow = reminderDao.getRemindersForDay(
            startOfDay = parseLocalDateToTimestamp(date),
            endOfDay = parseLocalDateToTimestamp(date.plusDays(1))
        )
            .map { remindersEntities -> remindersEntities
                .map { reminderEntity ->
                    reminderEntity.toAgendaItem()
                }
            }

        val tasksFlow = taskDao.getTasksForDay(
            startOfDay = parseLocalDateToTimestamp(date),
            endOfDay = parseLocalDateToTimestamp(date.plusDays(1))
        )
            .map { tasksEntities ->
                tasksEntities.map { taskEntity ->
                    taskEntity.toAgendaItem()
                }
            }

        return combine(
            eventsFlow,
            remindersFlow,
            tasksFlow,
        ){ events, reminders, tasks ->
            (events + reminders + tasks).sortedBy { it.fromTime}
        }
    }

    override suspend fun getAgendaItemById(
        agendaItemId: String,
    ): AgendaItem? {
        return when {
            agendaItemId.contains(AgendaItem.PREFIX_EVENT_ID) -> {
                eventDao.getEventById(agendaItemId)?.let { eventDao ->
                    val attendees = attendeeDao.getAttendeesByEventId(eventDao.eventId).map { it.toAttendee() }
                    val photos = photoDao.getPhotosByKeys(eventDao.photoKeys).map { it.toEventPhoto() }
                    eventDao.toAgendaItem().copy(
                        details = (eventDao.toAgendaItem().details as AgendaItemDetails.Event).copy(
                            attendees = attendees,
                            photos = photos
                        )
                    )
                }
            }
            agendaItemId.contains(AgendaItem.PREFIX_REMINDER_ID) -> {
                reminderDao.getReminderById(agendaItemId)?.toAgendaItem()
            }
            agendaItemId.contains(AgendaItem.PREFIX_TASK_ID) -> {
                taskDao.getTaskById(agendaItemId)?.toAgendaItem()
            }
            else -> null
        }
    }

    override suspend fun upsertAgendaItem(
        agendaItem: AgendaItem,
    ): Result<String, DataError.Local> {
        return try {
            when(agendaItem.details) {
                is AgendaItemDetails.Event -> {
                    db.withTransaction {
                        val eventEntity = agendaItem.toEventEntity()
                        eventDao.upsertEvent(eventEntity)
                        attendeeDao.upsertAttendees(agendaItem.details.attendees.map { it.toAttendeeEntity() })
                        photoDao.upsertPhotos(agendaItem.details.photos.map { it.toPhotoEntity() })
                        Result.Success(eventEntity.eventId)
                    }
                }
                is AgendaItemDetails.Task -> {
                    val taskEntity = agendaItem.toTaskEntity()
                    taskDao.upsertTask(taskEntity)
                    Result.Success(taskEntity.taskId)
                }
                is AgendaItemDetails.Reminder -> {
                    val reminderEntity = agendaItem.toReminderEntity()
                    reminderDao.upsertReminder(reminderEntity)
                    Result.Success(reminderEntity.reminderId)
                }
            }
        } catch(e: SQLiteFullException){
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun upsertAgendaItems(
        agendaItems: List<AgendaItem>,
    ): Result<List<String>, DataError.Local> {
        try {
            val events = agendaItems.filter {
                it.details is AgendaItemDetails.Event
            }

            val eventEntities = events.map {
                it.toEventEntity()
            }

            val tasks = agendaItems.filter {
                it.details is AgendaItemDetails.Task
            }.map {
                it.toTaskEntity()
            }

            val reminders = agendaItems.filter {
                it.details is AgendaItemDetails.Reminder
            }.map {
                it.toReminderEntity()
            }

            db.withTransaction {
                eventDao.upsertEvents(eventEntities)
                taskDao.upsertTasks(tasks)
                reminderDao.upsertReminders(reminders)
                attendeeDao.upsertAttendees(
                    events.map{
                        (it.details as AgendaItemDetails.Event).attendees.map { attendee ->
                            attendee.toAttendeeEntity()
                        }
                    }.flatten()
                )
                photoDao.upsertPhotos(
                    events.map{
                        (it.details as AgendaItemDetails.Event).photos.map { photo ->
                            photo.toPhotoEntity()
                        }
                    }.flatten()
                )
            }
            return Result.Success(agendaItems.map { it.id })
        } catch(e: SQLiteFullException){
            return Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun deleteAgendaItem(
        agendaItemId: String,
    ) {
        when{
            agendaItemId.contains(AgendaItem.PREFIX_EVENT_ID) -> {
                db.withTransaction {
                    val eventToDelete = eventDao.getEventById(agendaItemId)
                    eventDao.deleteEventById(agendaItemId)
                    eventToDelete?.photoKeys?.let {
                        if(it.isNotEmpty()){
                            photoDao.deletePhotosByKeys(eventToDelete.photoKeys)
                        }
                    }
                }
            }
            agendaItemId.contains(AgendaItem.PREFIX_REMINDER_ID) -> {
                reminderDao.deleteReminderById(agendaItemId)
            }
            agendaItemId.contains(AgendaItem.PREFIX_TASK_ID) -> {
                taskDao.deleteTaskById(agendaItemId)
            }
        }
    }

    override suspend fun deleteAllAgendaItems() {
        db.withTransaction {
            eventDao.deleteAllEvents()
            taskDao.deleteAllTasks()
            reminderDao.deleteAllReminders()
        }
    }

}