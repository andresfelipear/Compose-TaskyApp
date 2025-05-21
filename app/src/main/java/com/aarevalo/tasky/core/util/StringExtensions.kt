package com.aarevalo.tasky.core.util

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