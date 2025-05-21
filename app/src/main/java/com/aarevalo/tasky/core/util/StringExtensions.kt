package com.aarevalo.tasky.core.util

fun String.toInitials(): String {
    val nameParts = this.trim().split(" ", "-")
    if(nameParts.isEmpty()){
        return ""
    }

    val firstInitial = nameParts.firstOrNull()?.firstOrNull()?.uppercaseChar() ?: '?'
    val lastInitial = if (nameParts.size > 1) {
        nameParts.lastOrNull()?.firstOrNull()?.uppercaseChar() ?: '?'
    } else {
        nameParts.firstOrNull()?.getOrNull(1)?.uppercaseChar() ?: '?'
    }

    return "$firstInitial$lastInitial".trim().replace(" ", "")
}