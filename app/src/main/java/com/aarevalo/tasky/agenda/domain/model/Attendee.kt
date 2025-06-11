package com.aarevalo.tasky.agenda.domain.model

data class Attendee(
    var userId: String,
    val fullName: String,
    val email: String,
    val isGoing: Boolean,
)
