package com.aarevalo.tasky.agenda.domain.model

import com.aarevalo.tasky.agenda.presentation.agenda_detail.AgendaItemDetails
import java.time.LocalDate
import java.time.LocalTime

data class AgendaItem(
    val id: String,
    val fromTime: LocalTime = LocalTime.now(),
    val fromDate: LocalDate = LocalDate.now(),
    val description: String = "Event description",
    val title: String = "New Event",
    val details: AgendaItemDetails = AgendaItemDetails.Event(),
)
