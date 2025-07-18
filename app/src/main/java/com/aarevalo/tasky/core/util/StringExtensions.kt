package com.aarevalo.tasky.core.util

import java.util.Locale

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

fun String.toTitleCase(): String{
    val stringParts = this.split(" ")

    return stringParts.joinToString(
        separator = " ",
        transform = { stringPart ->
            stringPart.replaceFirstChar {
                it.titlecase(Locale.getDefault())
            }
        }
    )
}