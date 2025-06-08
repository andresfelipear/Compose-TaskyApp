package com.aarevalo.tasky.core.util

import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

/**
 * Converts a Duration to a human-readable string like "10 minutes", "1 hour", "1 day".
 * This is designed for durations that are whole units of minutes, hours, or days.
 */
fun Duration.toHumanReadableString(): String {
    return when {

        this.inWholeDays > 0 && this == this.inWholeDays.days -> {
            val days = this.inWholeDays
            if (days == 1L) "1 day" else "$days days"
        }

        this.inWholeHours > 0 && this == this.inWholeHours.hours -> {
            val hours = this.inWholeHours
            if (hours == 1L) "1 hour" else "$hours hours"
        }
        this.inWholeMinutes > 0 && this == this.inWholeMinutes.minutes -> {
            val minutes = this.inWholeMinutes
            if (minutes == 1L) "1 minute" else "$minutes minutes"
        }
        // Fallback for any duration not explicitly handled (e.g., milliseconds, or mixed units like 1h 30m)
        else -> this.toString() // Returns "1h 30m" etc.
    }
}