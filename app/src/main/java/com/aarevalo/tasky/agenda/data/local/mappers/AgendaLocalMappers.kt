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
import com.aarevalo.tasky.core.util.getReminderTypeFromLocalDateTime
import com.aarevalo.tasky.core.util.parseLocalDateTimeToTimestamp
import com.aarevalo.tasky.core.util.parseTimestampToLocalDate
import com.aarevalo.tasky.core.util.parseTimestampToLocalTime
import com.aarevalo.tasky.core.util.parseTimestampToZonedDateTime
import com.aarevalo.tasky.core.util.parseZonedDateTimeToTimestamp
import java.time.LocalDateTime
import kotlin.time.toJavaDuration

fun EventEntity.toAgendaItem(): AgendaItem {
    return AgendaItem(
        id = eventId,
        fromTime = parseTimestampToLocalTime(fromTimestamp),
        fromDate = parseTimestampToLocalDate(fromTimestamp),
        description = description,
        title = title,
        reminderAt = parseTimestampToZonedDateTime(reminderAt),
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
        hostId = "",
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
        hostId = "",
        reminderAt = parseTimestampToZonedDateTime(time).minus(remindAt.duration.toJavaDuration()),
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
        reminderAt = parseZonedDateTimeToTimestamp(reminderAt),
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
        reminderAt = parseZonedDateTimeToTimestamp(reminderAt),
        isDone = (details as AgendaItemDetails.Task).isDone
    )
}

fun AgendaItem.toReminderEntity(): ReminderEntity {
    val reminderAtToLocalDateTime = reminderAt.toLocalDateTime()
    val localDateTime = LocalDateTime.of(fromDate, fromTime)
    return ReminderEntity(
        reminderId = id,
        title = title,
        description = description,
        time = parseLocalDateTimeToTimestamp(
            localDate = fromDate,
            localTime = fromTime
        ),
        remindAt = getReminderTypeFromLocalDateTime(
            localDateTime = localDateTime,
            reminderAt = reminderAtToLocalDateTime
        )
    )
}

fun Attendee.toAttendeeEntity(): AttendeeEntity{
    return AttendeeEntity(
        attendeeUserId = userId,
        eventId = eventId,
        fullName = fullName,
        email = email,
        isGoing = isGoing,
        reminderAt = parseZonedDateTimeToTimestamp(reminderAt)
    )
}

fun EventPhoto.toPhotoEntity(): PhotoEntity{
    return PhotoEntity(
        key = key,
        uri = uri
    )
}