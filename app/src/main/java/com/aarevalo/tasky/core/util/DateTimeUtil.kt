package com.aarevalo.tasky.core.util

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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