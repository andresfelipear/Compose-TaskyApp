package com.aarevalo.tasky.agenda.domain.model

import java.time.LocalDateTime

data class Attendee(
    var userId: String,
    val fullName: String,
    val email: String,
    val isGoing: Boolean,
    val reminderAt: LocalDateTime
)
