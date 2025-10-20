package com.aarevalo.tasky.agenda.domain

import com.aarevalo.tasky.agenda.domain.model.AgendaItem
import com.aarevalo.tasky.agenda.domain.model.AgendaItemType
import com.aarevalo.tasky.agenda.domain.model.Attendee
import com.aarevalo.tasky.agenda.domain.model.EventPhoto
import com.aarevalo.tasky.agenda.domain.model.ReminderType
import com.aarevalo.tasky.agenda.presentation.agenda_detail.AgendaItemDetails
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.UUID

/**
 * Factory for creating test AgendaItems.
 * Provides convenient methods for creating valid test data.
 */
object AgendaItemTestFactory {
    
    /**
     * Extension function to get ZonedDateTime from AgendaItem for testing.
     */
    fun AgendaItem.getDateTime(): ZonedDateTime {
        return fromDate.atTime(fromTime).atZone(ZonedDateTime.now().zone)
    }
    
    fun createTestEvent(
        id: String = UUID.randomUUID().toString(),
        title: String = "Test Event",
        description: String = "Test Description",
        fromDate: LocalDate = LocalDate.now(),
        fromTime: LocalTime = LocalTime.of(9, 0),
        remindAt: ZonedDateTime = ZonedDateTime.now().plusHours(1),
        hostId: String = "test-host-id",
        attendees: List<Attendee> = emptyList(),
        photos: List<EventPhoto> = emptyList(),
        toDate: LocalDate = fromDate,
        toTime: LocalTime = fromTime.plusHours(1)
    ): AgendaItem {
        return AgendaItem(
            id = id,
            title = title,
            description = description,
            fromDate = fromDate,
            fromTime = fromTime,
            remindAt = remindAt,
            hostId = hostId,
            type = AgendaItemType.EVENT,
            details = AgendaItemDetails.Event(
                toDate = toDate,
                toTime = toTime,
                attendees = attendees,
                photos = photos,
                isUserEventCreator = true,
                localAttendee = null,
                isAddAttendeeDialogVisible = false
            )
        )
    }
    
    fun createTestTask(
        id: String = UUID.randomUUID().toString(),
        title: String = "Test Task",
        description: String = "Test Description",
        fromDate: LocalDate = LocalDate.now(),
        fromTime: LocalTime = LocalTime.of(9, 0),
        remindAt: ZonedDateTime = ZonedDateTime.now().plusHours(1),
        hostId: String = "test-host-id",
        isDone: Boolean = false
    ): AgendaItem {
        return AgendaItem(
            id = id,
            title = title,
            description = description,
            fromDate = fromDate,
            fromTime = fromTime,
            remindAt = remindAt,
            hostId = hostId,
            type = AgendaItemType.TASK,
            details = AgendaItemDetails.Task(isDone = isDone)
        )
    }
    
    fun createTestReminder(
        id: String = UUID.randomUUID().toString(),
        title: String = "Test Reminder",
        description: String = "Test Description",
        fromDate: LocalDate = LocalDate.now(),
        fromTime: LocalTime = LocalTime.of(9, 0),
        remindAt: ZonedDateTime = ZonedDateTime.now().plusHours(1),
        hostId: String = "test-host-id"
    ): AgendaItem {
        return AgendaItem(
            id = id,
            title = title,
            description = description,
            fromDate = fromDate,
            fromTime = fromTime,
            remindAt = remindAt,
            hostId = hostId,
            type = AgendaItemType.REMINDER,
            details = AgendaItemDetails.Reminder
        )
    }
    
    fun createTestAttendee(
        email: String = "attendee@example.com",
        fullName: String = "Test Attendee",
        userId: String = UUID.randomUUID().toString(),
        eventId: String = UUID.randomUUID().toString(),
        isGoing: Boolean = true,
        remindAt: ZonedDateTime = ZonedDateTime.now().plusHours(1)
    ): Attendee {
        return Attendee(
            email = email,
            fullName = fullName,
            userId = userId,
            eventId = eventId,
            isGoing = isGoing,
            remindAt = remindAt
        )
    }
    
    fun createTestLocalPhoto(
        key: String = UUID.randomUUID().toString(),
        uriString: String = "content://test/photo.jpg"
    ): EventPhoto.Local {
        return EventPhoto.Local(
            key = key,
            uriString = uriString
        )
    }
    
    fun createTestRemotePhoto(
        key: String = UUID.randomUUID().toString(),
        photoUrl: String = "https://example.com/photo.jpg"
    ): EventPhoto.Remote {
        return EventPhoto.Remote(
            key = key,
            photoUrl = photoUrl
        )
    }
    
    fun createAgendaItemsForDate(
        date: LocalDate,
        eventCount: Int = 1,
        taskCount: Int = 1,
        reminderCount: Int = 1
    ): List<AgendaItem> {
        val items = mutableListOf<AgendaItem>()
        
        val baseTime = LocalTime.of(9, 0)
        
        repeat(eventCount) { index ->
            items.add(createTestEvent(
                title = "Event $index",
                fromDate = date,
                fromTime = baseTime.plusHours(index.toLong())
            ))
        }
        
        repeat(taskCount) { index ->
            items.add(createTestTask(
                title = "Task $index",
                fromDate = date,
                fromTime = baseTime.plusHours(eventCount.toLong() + index.toLong())
            ))
        }
        
        repeat(reminderCount) { index ->
            items.add(createTestReminder(
                title = "Reminder $index",
                fromDate = date,
                fromTime = baseTime.plusHours((eventCount + taskCount).toLong() + index.toLong())
            ))
        }
        
        return items
    }
}

