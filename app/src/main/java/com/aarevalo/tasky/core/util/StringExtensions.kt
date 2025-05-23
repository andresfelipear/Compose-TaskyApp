package com.aarevalo.tasky.core.util

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun String.toInitials(): String {
    val fullName = this.trim()
    val nameParts = fullName
        .split(" ")
        .filter { it.isNotBlank() }

    val initials = if(nameParts.size == 1){
        fullName.take(2)
    } else {
        nameParts[0].first().toString() + nameParts.last().first()
    }

    return initials.uppercase()
}

fun String.toShortDateTime(date: LocalDate, time: LocalTime): String {
    val shortDate = date.month.toString().first() + date.month.toString().drop(1).take(2).lowercase() + " " + date.dayOfMonth

    val formatter = DateTimeFormatter.ofPattern("hh:mm")
    val shortTime = time.format(formatter)

    return "${shortDate}, $shortTime"
}