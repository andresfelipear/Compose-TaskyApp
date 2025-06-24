package com.aarevalo.tasky.agenda.data.local

import android.database.sqlite.SQLiteFullException
import com.aarevalo.tasky.core.domain.util.Result
import com.aarevalo.tasky.agenda.data.local.dao.AttendeeDao
import com.aarevalo.tasky.agenda.data.local.dao.EventDao
import com.aarevalo.tasky.agenda.data.local.dao.PhotoDao
import com.aarevalo.tasky.agenda.data.local.dao.ReminderDao
import com.aarevalo.tasky.agenda.data.local.dao.TaskDao
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
import com.aarevalo.tasky.agenda.domain.model.AgendaItemType
import com.aarevalo.tasky.agenda.presentation.agenda_detail.AgendaItemDetails
import com.aarevalo.tasky.core.domain.util.DataError
import com.aarevalo.tasky.core.util.parseLocalDateToTimestamp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import javax.inject.Inject

class RoomLocalAgendaDataSource @Inject constructor(
    private val eventDao: EventDao,
    private val taskDao: TaskDao,
    private val reminderDao: ReminderDao,
    private val attendeeDao: AttendeeDao,
    private val photoDao: PhotoDao
): LocalAgendaDataSource{
    override fun getAgendaItems(): Flow<List<AgendaItem>> {
        val tasksFlow = taskDao.getTasks()
        val remindersFlow = reminderDao.getReminders()
        val eventsFlow = eventDao.getEvents()

        return combine(
            eventsFlow,
            tasksFlow,
            remindersFlow,
        ){
            eventsEntities, taskEntities, reminderEntities ->
            val agendaItems = mutableListOf<AgendaItem>()

            eventsEntities.forEach{ eventEntity ->
                val attendees = attendeeDao.getAttendeesByEventId(eventEntity.eventId).map { it.toAttendee() }
                val photos = photoDao.getPhotosByKeys(eventEntity.photoKeys).map { it.toEventPhoto() }

                val eventAgendaItem = eventEntity.toAgendaItem().copy(
                    details = (eventEntity.toAgendaItem().details as AgendaItemDetails.Event).copy(
                        attendees = attendees,
                        photos = photos
                    )
                )
                agendaItems.add(eventAgendaItem)
            }

            taskEntities.forEach{ taskEntity ->
                val agendaItem = taskEntity.toAgendaItem()
                agendaItems.add(agendaItem)
            }

            reminderEntities.forEach{ reminderEntity ->
                val agendaItem = reminderEntity.toAgendaItem()
                agendaItems.add(agendaItem)
            }
            agendaItems
        }
    }

    override fun getAgendaItemsByDate(date: LocalDate): Flow<List<AgendaItem>> {
        val tasksFlow = taskDao.getTasksForDay(
            startOfDay = parseLocalDateToTimestamp(date),
            endOfDay = parseLocalDateToTimestamp(date.plusDays(1))
        )
        val remindersFlow = reminderDao.getRemindersForDay(
            startOfDay = parseLocalDateToTimestamp(date),
            endOfDay = parseLocalDateToTimestamp(date.plusDays(1))
        )
        val eventsFlow = eventDao.getEventsForDay(
            startOfDay = parseLocalDateToTimestamp(date),
            endOfDay = parseLocalDateToTimestamp(date.plusDays(1))
        )

        return combine(
            eventsFlow,
            tasksFlow,
            remindersFlow,
        ){
            eventsEntities, taskEntities, reminderEntities ->
            val agendaItems = mutableListOf<AgendaItem>()

            eventsEntities.forEach{ eventEntity ->
                val attendees = attendeeDao.getAttendeesByEventId(eventEntity.eventId).map { it.toAttendee() }
                val photos = photoDao.getPhotosByKeys(eventEntity.photoKeys).map { it.toEventPhoto() }

                val eventAgendaItem = eventEntity.toAgendaItem().copy(
                    details = (eventEntity.toAgendaItem().details as AgendaItemDetails.Event).copy(
                        attendees = attendees,
                        photos = photos
                    )
                )
                agendaItems.add(eventAgendaItem)
            }

            taskEntities.forEach{ taskEntity ->
                val agendaItem = taskEntity.toAgendaItem()
                agendaItems.add(agendaItem)
            }

            reminderEntities.forEach{ reminderEntity ->
                val agendaItem = reminderEntity.toAgendaItem()
                agendaItems.add(agendaItem)
            }
            agendaItems
        }
    }

    override suspend fun getAgendaItemById(
        agendaItemId: String,
        agendaItemType: AgendaItemType
    ): AgendaItem? {
        when(agendaItemType){
            AgendaItemType.EVENT -> {
                val eventDao = eventDao.getEventById(agendaItemId)
                if(eventDao != null){
                    val attendees = attendeeDao.getAttendeesByEventId(eventDao.eventId).map { it.toAttendee() }
                    val photos = photoDao.getPhotosByKeys(eventDao.photoKeys).map { it.toEventPhoto() }
                    return eventDao.toAgendaItem().copy(
                        details = (eventDao.toAgendaItem().details as AgendaItemDetails.Event).copy(
                            attendees = attendees,
                            photos = photos
                        )
                    )
                }
                return null
            }
            AgendaItemType.TASK -> {
                return taskDao.getTaskById(agendaItemId)?.toAgendaItem()
            }
            AgendaItemType.REMINDER -> {
                return reminderDao.getReminderById(agendaItemId)?.toAgendaItem()
            }
        }
    }

    override suspend fun upsertAgendaItem(
        agendaItem: AgendaItem,
        hostId: String?
    ): Result<String, DataError.Local> {
        return try {
            when(agendaItem.details) {
                is AgendaItemDetails.Event -> {
                    val eventEntity = agendaItem.toEventEntity().copy(
                        hostId = hostId ?: "",
                    )
                    eventDao.upsertEvent(eventEntity)
                    attendeeDao.upsertAttendees(agendaItem.details.attendees.map { it.toAttendeeEntity() })
                    photoDao.upsertPhotos(agendaItem.details.photos.map { it.toPhotoEntity() })
                    Result.Success(eventEntity.eventId)
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

    override suspend fun deleteAgendaItem(
        agendaItemId: String,
        agendaItemType: AgendaItemType
    ) {
        when(agendaItemType){
            AgendaItemType.EVENT -> {
                eventDao.deleteEventById(agendaItemId)
            }
            AgendaItemType.TASK -> {
                taskDao.deleteTaskById(agendaItemId)
            }
            AgendaItemType.REMINDER -> {
                reminderDao.deleteReminderById(agendaItemId)
            }
        }
    }

    override suspend fun deleteAllAgendaItems() {
        eventDao.deleteAllEvents()
        taskDao.deleteAllTasks()
        reminderDao.deleteAllReminders()
    }

}