package com.aarevalo.tasky.agenda.domain.model

import com.aarevalo.tasky.agenda.presentation.agenda_detail.AgendaItemDetails
import java.time.LocalDate
import java.time.LocalTime

data class AgendaItem(
    val id: String,
    val fromTime: LocalTime,
    val fromDate: LocalDate,
    val description: String,
    val title: String,
    val details: AgendaItemDetails,
)
