package com.aarevalo.tasky.core.util

import com.aarevalo.tasky.agenda.domain.model.ReminderType
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.toKotlinDuration

fun formattedDateTimeToString(
    date: LocalDate,
    time: LocalTime
): String {
    val shortDate = date.month.toString()
        .first() + date.month.toString()
        .drop(1)
        .take(2)
        .lowercase() + " " + date.dayOfMonth

    val formatter = DateTimeFormatter.ofPattern("hh:mm")
    val shortTime = time.format(formatter)

    return "${shortDate}, $shortTime"
}

fun formattedDateToString(
    date: LocalDate
): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
    return date.format(formatter)
}

fun formattedFromToDateTimeToString(
    dateFrom: LocalDate,
    timeFrom: LocalTime,
    dateTo: LocalDate,
    timeTo: LocalTime
): String {
    val fromDateTime = formattedDateTimeToString(dateFrom, timeFrom)
    val toDateTime = formattedDateTimeToString(dateTo, timeTo)

    return "$fromDateTime - $toDateTime"
}

fun parseTimestampToLocalTime(
    timestampMillis: Long,
    zoneId: ZoneId = ZoneId.systemDefault()
): LocalTime {
    val instant = Instant.ofEpochMilli(timestampMillis)
    val zonedDateTime = instant.atZone(zoneId)
    return zonedDateTime.toLocalTime()
}

fun getReminderTypeFromLocalDateTime(
    localDateTime: LocalDateTime,
    reminderAt: LocalDateTime
): ReminderType {
    val durationBetween = Duration.between(reminderAt, localDateTime)
    return ReminderType.entries.find { it.duration == durationBetween.toKotlinDuration()
    }?: ReminderType.TEN_MINUTES
}

fun parseTimestampToLocalDate(
    timestampMillis: Long,
    zoneId: ZoneId = ZoneId.systemDefault()
): LocalDate {
    val instant = Instant.ofEpochMilli(timestampMillis)
    val zonedDateTime = instant.atZone(zoneId)
    return zonedDateTime.toLocalDate()
}

fun parseTimestampToZonedDateTime(
    timestampMillis: Long,
    zoneId: ZoneId = ZoneId.systemDefault()
): ZonedDateTime {
    val instant = Instant.ofEpochMilli(timestampMillis)
    val zonedDateTime = instant.atZone(zoneId)
    return zonedDateTime
}

fun parseLocalDateToTimestamp(
    localDate: LocalDate,
): Long {
    val localDateTime = localDate.atStartOfDay()
    return localDateTime.toInstant(java.time.ZoneOffset.UTC).toEpochMilli()
}

fun parseLocalDateTimeToTimestamp(
    localDate: LocalDate,
    localTime: LocalTime,
    zoneId: ZoneId = ZoneId.systemDefault()
): Long{
    val localDateTime = LocalDateTime.of(localDate, localTime)
    val zonedDateTime = localDateTime.atZone(zoneId)
    return zonedDateTime.toInstant().toEpochMilli()
}

fun parseZonedDateTimeToTimestamp(
    zonedDateTime: ZonedDateTime,
    zoneId: ZoneId = ZoneId.systemDefault()
): Long {
    return zonedDateTime.withZoneSameInstant(zoneId)
        .toInstant()
        .toEpochMilli()
}

fun getUtcTimestampFromLocalDate(
    localDate: LocalDate,
    zoneId: ZoneId = ZoneId.systemDefault()
): Long {

    val localDateTime = localDate.atStartOfDay()
    val zonedDateTime = localDateTime.atZone(zoneId)
    val instant = zonedDateTime.toInstant()
    return instant.toEpochMilli()
}
