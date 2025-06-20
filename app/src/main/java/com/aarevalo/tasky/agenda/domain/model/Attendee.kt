package com.aarevalo.tasky.agenda.domain.model

import java.time.ZonedDateTime

data class Attendee(
    val userId: String,
    val eventId: String,
    val fullName: String,
    val email: String,
    val isGoing: Boolean,
    val reminderAt: ZonedDateTime
)
