package com.aarevalo.tasky.agenda.data.remote.mappers

import com.aarevalo.tasky.agenda.data.remote.dto.AttendeeDto
import com.aarevalo.tasky.agenda.data.remote.dto.EventDto
import com.aarevalo.tasky.agenda.data.remote.dto.EventRequest
import com.aarevalo.tasky.agenda.data.remote.dto.PhotoDto
import com.aarevalo.tasky.agenda.data.remote.dto.ReminderDto
import com.aarevalo.tasky.agenda.data.remote.dto.TaskDto
import com.aarevalo.tasky.agenda.domain.model.AgendaItem
import com.aarevalo.tasky.agenda.domain.model.Attendee
import com.aarevalo.tasky.agenda.domain.model.EventPhoto
import com.aarevalo.tasky.agenda.presentation.agenda_detail.AgendaItemDetails
import com.aarevalo.tasky.agenda.presentation.agenda_detail.asEventDetails
import com.aarevalo.tasky.core.util.parseLocalDateTimeToTimestamp
import com.aarevalo.tasky.core.util.parseTimestampToLocalDate
import com.aarevalo.tasky.core.util.parseTimestampToLocalTime
import com.aarevalo.tasky.core.util.parseTimestampToZonedDateTime
import com.aarevalo.tasky.core.util.parseZonedDateTimeToTimestamp

fun EventDto.toAgendaItem(): AgendaItem {
    return AgendaItem(
        id = id,
        fromTime = parseTimestampToLocalTime(from),
        fromDate = parseTimestampToLocalDate(from),
        description = description,
        title = title,
        reminderAt = parseTimestampToZonedDateTime(reminderAt),
        details = AgendaItemDetails.Event(
            toTime = parseTimestampToLocalTime(to),
            toDate = parseTimestampToLocalDate(to),
            photos = photos.map { it.toEventPhoto() },
            attendees = attendees.map { it.toAttendee() },
            isUserEventCreator = isUserEventCreator,

        )
    )
}

fun TaskDto.toAgendaItem(): AgendaItem {
    return AgendaItem(
        id = id,
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

fun ReminderDto.toAgendaItem(): AgendaItem {
    return AgendaItem(
        id = id,
        fromTime = parseTimestampToLocalTime(time),
        fromDate = parseTimestampToLocalDate(time),
        description = description,
        title = title,
        reminderAt = parseTimestampToZonedDateTime(reminderAt),
        details = AgendaItemDetails.Reminder
    )
}

fun AgendaItem.toEventRequest(): EventRequest {
    val details = details.asEventDetails
    requireNotNull(details)

    return EventRequest(
        id = id,
        title = title,
        description = description,
        from = parseLocalDateTimeToTimestamp(fromDate, fromTime),
        to = parseLocalDateTimeToTimestamp(details.toDate, details.toTime),
        reminderAt = reminderAt.toInstant().toEpochMilli(),
        attendeeIds = details.attendees.map { it.userId },
    )
}

fun AgendaItem.toTaskDto(): TaskDto {
    return TaskDto(
        id = id,
        title = title,
        description = description,
        time = parseLocalDateTimeToTimestamp(fromDate, fromTime),
        reminderAt = parseZonedDateTimeToTimestamp(reminderAt),
        isDone = false
    )
}

fun AgendaItem.toReminderDto(): ReminderDto {
    return ReminderDto(
        id = id,
        title = title,
        description = description,
        time = parseLocalDateTimeToTimestamp(
            fromDate,
            fromTime
        ),
        reminderAt = parseZonedDateTimeToTimestamp(reminderAt)
    )
}

fun AttendeeDto.toAttendee(): Attendee {
    return Attendee(
        userId = userId,
        fullName = fullName,
        email = email,
        isGoing = isGoing,
        reminderAt = parseTimestampToZonedDateTime(reminderAt),
        eventId = eventId
    )
}

fun PhotoDto.toEventPhoto(): EventPhoto.Local {
    return EventPhoto.Local(
        key = key,
        uriString = url
    )
}

fun EventPhoto.toPhotoDto(): PhotoDto {
    return PhotoDto(
        key = key,
        url = uri
    )
}

fun Attendee.toAttendeeDto(): AttendeeDto {
    return AttendeeDto(
        userId = userId,
        fullName = fullName,
        email = email,
        eventId = eventId,
        isGoing = isGoing,
        reminderAt = parseZonedDateTimeToTimestamp(reminderAt)
    )
}

fun EventPhoto.Remote.toPhotoDto(): PhotoDto {
    return PhotoDto(
        key = key,
        url = uri
    )
}