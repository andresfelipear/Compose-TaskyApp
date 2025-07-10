package com.aarevalo.tasky.agenda.data.remote.mappers

import com.aarevalo.tasky.agenda.data.remote.dto.AttendeeDto
import com.aarevalo.tasky.agenda.data.remote.dto.EventCreateRequest
import com.aarevalo.tasky.agenda.data.remote.dto.EventDto
import com.aarevalo.tasky.agenda.data.remote.dto.EventUpdateRequest
import com.aarevalo.tasky.agenda.data.remote.dto.PhotoDto
import com.aarevalo.tasky.agenda.data.remote.dto.ReminderDto
import com.aarevalo.tasky.agenda.data.remote.dto.TaskDto
import com.aarevalo.tasky.agenda.domain.model.AgendaItem
import com.aarevalo.tasky.agenda.domain.model.Attendee
import com.aarevalo.tasky.agenda.domain.model.EventPhoto
import com.aarevalo.tasky.agenda.presentation.agenda_detail.AgendaItemDetails
import com.aarevalo.tasky.agenda.presentation.agenda_detail.asEventDetails
import com.aarevalo.tasky.agenda.presentation.agenda_detail.asTaskDetails
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
        remindAt = parseTimestampToZonedDateTime(remindAt),
        details = AgendaItemDetails.Event(
            toTime = parseTimestampToLocalTime(to),
            toDate = parseTimestampToLocalDate(to),
            photos = photos.map { it.toEventPhoto() },
            attendees = attendees.map { it.toAttendee() },
            isUserEventCreator = isUserEventCreator,
        ),
        hostId = host
    )
}

fun TaskDto.toAgendaItem(): AgendaItem {
    return AgendaItem(
        id = id,
        fromTime = parseTimestampToLocalTime(time),
        fromDate = parseTimestampToLocalDate(time),
        description = description,
        title = title,
        remindAt = parseTimestampToZonedDateTime(remindAt),
        details = AgendaItemDetails.Task(
            isDone = isDone
        ),
        hostId = ""
    )
}

fun ReminderDto.toAgendaItem(): AgendaItem {
    return AgendaItem(
        id = id,
        fromTime = parseTimestampToLocalTime(time),
        fromDate = parseTimestampToLocalDate(time),
        description = description,
        title = title,
        remindAt = parseTimestampToZonedDateTime(remindAt),
        details = AgendaItemDetails.Reminder,
        hostId = ""
    )
}

fun AgendaItem.toEventUpdateRequest(): EventUpdateRequest {
    val details = details.asEventDetails
    requireNotNull(details)

    return EventUpdateRequest(
        id = id,
        title = title,
        description = description,
        from = parseLocalDateTimeToTimestamp(fromDate, fromTime),
        to = parseLocalDateTimeToTimestamp(details.toDate, details.toTime),
        remindAt = remindAt.toInstant().toEpochMilli(),
        attendeeIds = details.attendees.map { it.userId },
        deletedPhotoKeys = emptyList(),
        isGoing = false
    )
}

fun AgendaItem.toEventCreateRequest(): EventCreateRequest {
    val details = details.asEventDetails
    requireNotNull(details)

    return EventCreateRequest(
        id = id,
        title = title,
        description = description,
        from = parseLocalDateTimeToTimestamp(fromDate, fromTime),
        to = parseLocalDateTimeToTimestamp(details.toDate, details.toTime),
        remindAt = remindAt.toInstant().toEpochMilli(),
        attendeeIds = details.attendees.map { it.userId },
    )
}

fun AgendaItem.toTaskDto(): TaskDto {
    return TaskDto(
        id = id,
        title = title,
        description = description,
        time = parseLocalDateTimeToTimestamp(fromDate, fromTime),
        remindAt = parseZonedDateTimeToTimestamp(remindAt),
        isDone = details.asTaskDetails?.isDone?:false
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
        remindAt = parseZonedDateTimeToTimestamp(remindAt)
    )
}

fun AttendeeDto.toAttendee(): Attendee {
    return Attendee(
        userId = userId,
        fullName = fullName,
        email = email,
        isGoing = isGoing?:true,
        remindAt = parseTimestampToZonedDateTime(remindAt?:System.currentTimeMillis()),
        eventId = eventId?:""
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
        remindAt = parseZonedDateTimeToTimestamp(remindAt)
    )
}

fun EventPhoto.Remote.toPhotoDto(): PhotoDto {
    return PhotoDto(
        key = key,
        url = uri
    )
}