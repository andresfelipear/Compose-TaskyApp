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
import kotlin.time.toJavaDuration
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
    fromDate: LocalDate,
    fromTime: LocalTime,
    remindAt: LocalDateTime
): ReminderType {
    val localDateTime = LocalDateTime.of(fromDate, fromTime)
    val durationBetween = Duration.between(remindAt, localDateTime)

    return ReminderType.entries.find { it.duration == durationBetween.toKotlinDuration()
    }?: ReminderType.ONE_HOUR
}

fun getReminderTypeFromTimestamp(
    time: Long,
    remindAt: Long
): ReminderType {
    val instant = Instant.ofEpochMilli(time)
    val zonedDateTime = instant.atZone(ZoneId.systemDefault())
    val localDateTime = zonedDateTime.toLocalDateTime()

    val instantRemindAt = Instant.ofEpochMilli(remindAt)
    val zonedDateTimeRemindAt = instantRemindAt.atZone(ZoneId.systemDefault())
    val localDateTimeRemindAt = zonedDateTimeRemindAt.toLocalDateTime()

    val durationBetween = Duration.between(localDateTimeRemindAt, localDateTime)

    return ReminderType.entries.find { it.duration == durationBetween.toKotlinDuration()
    }?: ReminderType.ONE_HOUR
}

fun getRemindAtFromReminderType(
    reminderType: ReminderType,
    fromDate: LocalDate,
    fromTime: LocalTime
): ZonedDateTime {
    val localDateTime = LocalDateTime.of(fromDate, fromTime)
    val reminderAtLocalDateTime = localDateTime.minus(reminderType.duration.toJavaDuration())

    return ZonedDateTime.of(reminderAtLocalDateTime, ZoneId.systemDefault())
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
    val localDateTime = localDate.atStartOfDay(ZoneId.systemDefault())
    return localDateTime.toInstant().toEpochMilli()
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

fun toIsoInstantString(zonedDateTime: ZonedDateTime): String {
    return zonedDateTime.withZoneSameInstant(ZoneId.of("UTC")).toInstant().toString()
}

fun fromIsoInstantString(iso: String): ZonedDateTime {
    return Instant.parse(iso).atZone(ZoneId.systemDefault())
}

fun millisToIsoInstantString(millis: Long): String {
    return Instant.ofEpochMilli(millis).toString()
}

fun isoInstantStringToMillis(iso: String): Long {
    return Instant.parse(iso).toEpochMilli()
}
