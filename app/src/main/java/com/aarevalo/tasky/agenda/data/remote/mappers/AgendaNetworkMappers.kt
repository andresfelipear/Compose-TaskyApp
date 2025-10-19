package com.aarevalo.tasky.agenda.data.remote.mappers

import com.aarevalo.tasky.agenda.data.remote.dto.AttendeeDto
import com.aarevalo.tasky.agenda.data.remote.dto.EventCreateRequest
import com.aarevalo.tasky.agenda.data.remote.dto.EventDto
import com.aarevalo.tasky.agenda.data.remote.dto.EventUpdateRequest
import com.aarevalo.tasky.agenda.data.remote.dto.PhotoDto
import com.aarevalo.tasky.agenda.data.remote.dto.ReminderDto
import com.aarevalo.tasky.agenda.data.remote.dto.TaskDto
import com.aarevalo.tasky.agenda.domain.model.AgendaItem
import com.aarevalo.tasky.agenda.domain.model.AgendaItemType
import com.aarevalo.tasky.agenda.domain.model.Attendee
import com.aarevalo.tasky.agenda.domain.model.EventPhoto
import com.aarevalo.tasky.agenda.presentation.agenda_detail.AgendaItemDetails
import com.aarevalo.tasky.agenda.presentation.agenda_detail.asEventDetails
import com.aarevalo.tasky.agenda.presentation.agenda_detail.asTaskDetails
import com.aarevalo.tasky.core.util.fromIsoInstantString
import com.aarevalo.tasky.core.util.isoInstantStringToMillis
import com.aarevalo.tasky.core.util.millisToIsoInstantString
import com.aarevalo.tasky.core.util.parseLocalDateTimeToTimestamp
import com.aarevalo.tasky.core.util.parseTimestampToLocalDate
import com.aarevalo.tasky.core.util.parseTimestampToLocalTime
import com.aarevalo.tasky.core.util.parseTimestampToZonedDateTime
import com.aarevalo.tasky.core.util.parseZonedDateTimeToTimestamp
import com.aarevalo.tasky.core.util.toIsoInstantString

fun EventDto.toAgendaItem(): AgendaItem {
    return AgendaItem(
        id = id,
        fromTime = parseTimestampToLocalTime(isoInstantStringToMillis(from)),
        fromDate = parseTimestampToLocalDate(isoInstantStringToMillis(from)),
        description = description,
        title = title,
        remindAt = fromIsoInstantString(remindAt),
        details = AgendaItemDetails.Event(
            toTime = parseTimestampToLocalTime(isoInstantStringToMillis(to)),
            toDate = parseTimestampToLocalDate(isoInstantStringToMillis(to)),
            photos = photos.map { it.toEventPhoto() },
            attendees = attendees.map { it.toAttendee() },
            isUserEventCreator = isUserEventCreator,
        ),
        hostId = hostId,
        type = AgendaItemType.EVENT
    )
}

fun TaskDto.toAgendaItem(): AgendaItem {
    return AgendaItem(
        id = id,
        fromTime = parseTimestampToLocalTime(isoInstantStringToMillis(time)),
        fromDate = parseTimestampToLocalDate(isoInstantStringToMillis(time)),
        description = description,
        title = title,
        remindAt = fromIsoInstantString(remindAt),
        details = AgendaItemDetails.Task(
            isDone = isDone
        ),
        hostId = "",
        type = AgendaItemType.TASK
    )
}

fun ReminderDto.toAgendaItem(): AgendaItem {
    return AgendaItem(
        id = id,
        fromTime = parseTimestampToLocalTime(isoInstantStringToMillis(time)),
        fromDate = parseTimestampToLocalDate(isoInstantStringToMillis(time)),
        description = description,
        title = title,
        remindAt = fromIsoInstantString(remindAt),
        details = AgendaItemDetails.Reminder,
        hostId = "",
        type = AgendaItemType.REMINDER
    )
}

fun AgendaItem.toEventUpdateRequest(): EventUpdateRequest {
    val details = details.asEventDetails
    requireNotNull(details)

    return EventUpdateRequest(
        title = title,
        description = description,
        from = millisToIsoInstantString(parseLocalDateTimeToTimestamp(fromDate, fromTime)),
        to = millisToIsoInstantString(parseLocalDateTimeToTimestamp(details.toDate, details.toTime)),
        remindAt = toIsoInstantString(remindAt),
        attendeeIds = details.attendees.map { it.userId },
        newPhotoKeys = details.photos.filterIsInstance<EventPhoto.Local>().map { it.key },
        deletedPhotoKeys = emptyList(),
        isGoing = false,
        updatedAt = millisToIsoInstantString(System.currentTimeMillis())
    )
}

fun AgendaItem.toEventCreateRequest(): EventCreateRequest {
    val details = details.asEventDetails
    requireNotNull(details)

    return EventCreateRequest(
        id = id,
        title = title,
        description = description,
        from = millisToIsoInstantString(parseLocalDateTimeToTimestamp(fromDate, fromTime)),
        to = millisToIsoInstantString(parseLocalDateTimeToTimestamp(details.toDate, details.toTime)),
        remindAt = toIsoInstantString(remindAt),
        attendeeIds = details.attendees.map { it.userId },
        photoKeys = details.photos.filterIsInstance<EventPhoto.Local>().map { it.key },
        updatedAt = millisToIsoInstantString(System.currentTimeMillis())
    )
}

fun AgendaItem.toTaskDto(): TaskDto {
    return TaskDto(
        id = id,
        title = title,
        description = description,
        time = millisToIsoInstantString(parseLocalDateTimeToTimestamp(fromDate, fromTime)),
        remindAt = toIsoInstantString(remindAt),
        updatedAt = millisToIsoInstantString(System.currentTimeMillis()),
        isDone = details.asTaskDetails?.isDone?:false
    )
}

fun AgendaItem.toReminderDto(): ReminderDto {
    return ReminderDto(
        id = id,
        title = title,
        description = description,
        time = millisToIsoInstantString(parseLocalDateTimeToTimestamp(fromDate, fromTime)),
        remindAt = toIsoInstantString(remindAt),
        updatedAt = millisToIsoInstantString(System.currentTimeMillis())
    )
}

fun AttendeeDto.toAttendee(): Attendee {
    return Attendee(
        userId = userId,
        fullName = fullName,
        email = email,
        isGoing = isGoing?:true,
        remindAt = remindAt?.let { fromIsoInstantString(it) } ?: parseTimestampToZonedDateTime(System.currentTimeMillis()),
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
        remindAt = toIsoInstantString(remindAt)
    )
}

fun EventPhoto.Remote.toPhotoDto(): PhotoDto {
    return PhotoDto(
        key = key,
        url = uri
    )
}