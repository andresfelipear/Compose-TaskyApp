package com.aarevalo.tasky.agenda.domain.model

data class Attendee(
    val id: String,
    val fullName: String,
    val email: String,
    val isGoing: Boolean,
)
