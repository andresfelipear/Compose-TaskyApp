package com.aarevalo.tasky.agenda.domain.model

import java.time.ZonedDateTime

data class AlarmItem(
    val time: ZonedDateTime,
    val title: String,
    val description: String,
    val id: String,
    val itemType: String,
)
