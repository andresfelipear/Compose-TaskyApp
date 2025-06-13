package com.aarevalo.tasky.core.util

import com.aarevalo.tasky.R
import com.aarevalo.tasky.core.presentation.util.UiText
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

/**
 * Converts a Duration to a human-readable string like "10 minutes", "1 hour", "1 day".
 * This is designed for durations that are whole units of minutes, hours, or days.
 */
fun Duration.UiText(): UiText {
    return when {

        this.inWholeDays > 0 && this == this.inWholeDays.days -> {
            val days = this.inWholeDays
            if (days == 1L) UiText.StringResource(id = R.string.one_day) else UiText.StringResource(id = R.string.days, arrayOf(days))
        }

        this.inWholeHours > 0 && this == this.inWholeHours.hours -> {
            val hours = this.inWholeHours
            if (hours == 1L) UiText.StringResource(id = R.string.one_hour) else UiText.StringResource(id = R.string.hours, arrayOf(hours))
        }
        this.inWholeMinutes > 0 && this == this.inWholeMinutes.minutes -> {
            val minutes = this.inWholeMinutes
            if (minutes == 1L) UiText.StringResource(id = R.string.one_minute) else UiText.StringResource(id = R.string.minutes, arrayOf(minutes))
        }
        else -> UiText.StringResource(id = R.string.not_a_whole, arrayOf(this.toString()))
    }
}