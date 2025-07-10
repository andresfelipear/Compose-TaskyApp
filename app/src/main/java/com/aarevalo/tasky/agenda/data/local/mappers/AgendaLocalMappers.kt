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
import com.aarevalo.tasky.core.util.parseLocalDateTimeToTimestamp
import com.aarevalo.tasky.core.util.parseTimestampToLocalDate
import com.aarevalo.tasky.core.util.parseTimestampToLocalTime
import com.aarevalo.tasky.core.util.parseTimestampToZonedDateTime
import com.aarevalo.tasky.core.util.parseZonedDateTimeToTimestamp

fun EventEntity.toAgendaItem(): AgendaItem {
    return AgendaItem(
        id = eventId,
        fromTime = parseTimestampToLocalTime(fromTimestamp),
        fromDate = parseTimestampToLocalDate(fromTimestamp),
        description = description,
        title = title,
        remindAt = parseTimestampToZonedDateTime(remindAt),
        hostId = hostId,
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
        remindAt = remindAt?.let {
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
        hostId = "",
        remindAt = parseTimestampToZonedDateTime(remindAt),
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
        hostId = "",
        remindAt = parseTimestampToZonedDateTime(remindAt),
        details = AgendaItemDetails.Reminder,
        )
}

fun AgendaItem.toEventEntity(): EventEntity{
    return EventEntity(
        eventId = id,
        title = title,
        description = description,
        fromTimestamp = parseLocalDateTimeToTimestamp(
            localDate = fromDate,
            localTime = fromTime
        ),
        toTimestamp = parseLocalDateTimeToTimestamp(
            localDate = (details as AgendaItemDetails.Event).toDate,
            localTime = details.toTime
        ),
        remindAt = parseZonedDateTimeToTimestamp(remindAt),
        hostId = hostId,
        isUserEventCreator = details.isUserEventCreator,
        photoKeys = details.photos.map { it.key }
    )
}

fun AgendaItem.toTaskEntity(): TaskEntity{
    return TaskEntity(
        taskId = id,
        title = title,
        description = description,
        time = parseLocalDateTimeToTimestamp(
            localDate = fromDate,
            localTime = fromTime
        ),
        remindAt = parseZonedDateTimeToTimestamp(remindAt),
        isDone = (details as AgendaItemDetails.Task).isDone
    )
}

fun AgendaItem.toReminderEntity(): ReminderEntity {
    return ReminderEntity(
        reminderId = id,
        title = title,
        description = description,
        time = parseLocalDateTimeToTimestamp(
            localDate = fromDate,
            localTime = fromTime
        ),
        remindAt = parseZonedDateTimeToTimestamp(remindAt)
    )
}

fun Attendee.toAttendeeEntity(): AttendeeEntity{
    return AttendeeEntity(
        attendeeUserId = userId,
        eventId = eventId,
        fullName = fullName,
        email = email,
        isGoing = isGoing,
        remindAt = parseZonedDateTimeToTimestamp(remindAt)
    )
}

fun EventPhoto.toPhotoEntity(): PhotoEntity{
    return PhotoEntity(
        key = key,
        uri = uri
    )
}