package com.aarevalo.tasky.agenda.data.local.mappers

import com.aarevalo.tasky.agenda.data.local.entity.AttendeeEntity
import com.aarevalo.tasky.agenda.data.local.entity.EventEntity
import com.aarevalo.tasky.agenda.data.local.entity.PhotoEntity
import com.aarevalo.tasky.agenda.data.local.entity.ReminderEntity
import com.aarevalo.tasky.agenda.data.local.entity.TaskEntity
import com.aarevalo.tasky.agenda.domain.model.AgendaItem
import com.aarevalo.tasky.agenda.domain.model.Attendee
import com.aarevalo.tasky.agenda.domain.model.EventPhoto
import com.aarevalo.tasky.agenda.presentation.agenda_detail.AgendaItemDetails
import com.aarevalo.tasky.core.util.parseTimestampToLocalDate
import com.aarevalo.tasky.core.util.parseTimestampToLocalTime
import com.aarevalo.tasky.core.util.parseTimestampToZonedDateTime
import kotlin.time.toJavaDuration

fun EventEntity.toAgendaItem(): AgendaItem {
    return AgendaItem(
        id = eventId,
        fromTime = parseTimestampToLocalTime(fromTimestamp),
        fromDate = parseTimestampToLocalDate(fromTimestamp),
        description = description,
        title = title,
        reminderAt = parseTimestampToZonedDateTime(reminderAt),
        details = AgendaItemDetails.Event(
            toTime = parseTimestampToLocalTime(toTimestamp),
            toDate = parseTimestampToLocalDate(toTimestamp),
            photos = emptyList(),
            attendees = emptyList(),
            isUserEventCreator = isUserEventCreator
        )
    )
}

fun AttendeeEntity.toAttendee(): Attendee{
    return Attendee(
        userId = attendeeUserId,
        eventId = eventId ?: "",
        fullName = fullName,
        email = email,
        isGoing = isGoing ?: true,
        reminderAt = reminderAt?.let {
            parseTimestampToZonedDateTime(it)
        } ?: parseTimestampToZonedDateTime(System.currentTimeMillis())
    )
}

fun PhotoEntity.toEventPhoto(): EventPhoto{
    return EventPhoto.Local(
        key = key,
        uriString = uri
    )
}

fun TaskEntity.toAgendaItem(): AgendaItem {
    return AgendaItem(
        id = taskId,
        fromTime = parseTimestampToLocalTime(time),
        fromDate = parseTimestampToLocalDate(time),
        description = description,
        title = title,
        reminderAt = parseTimestampToZonedDateTime(reminderAt),
        details = AgendaItemDetails.Task(
            isDone = isDone
        )
    )
}

fun ReminderEntity.toAgendaItem(): AgendaItem {
    return AgendaItem(
        id = reminderId,
        fromTime = parseTimestampToLocalTime(time),
        fromDate = parseTimestampToLocalDate(time),
        description = description,
        title = title,
        reminderAt = parseTimestampToZonedDateTime(time).minus(remindAt.duration.toJavaDuration()),
        details = AgendaItemDetails.Reminder
    )
}