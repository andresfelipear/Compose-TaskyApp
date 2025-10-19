package com.aarevalo.tasky.agenda.domain.model

import com.aarevalo.tasky.agenda.presentation.agenda_detail.AgendaItemDetails
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime

data class AgendaItem(
    val id: String,
    val fromTime: LocalTime,
    val fromDate: LocalDate,
    val description: String,
    val title: String,
    val remindAt: ZonedDateTime,
    val details: AgendaItemDetails,
    val hostId: String,
    val type: AgendaItemType
)

fun AgendaItemDetails.toAgendaItemType(): AgendaItemType {
    return when(this) {
        is AgendaItemDetails.Event -> AgendaItemType.EVENT
        is AgendaItemDetails.Task -> AgendaItemType.TASK
        is AgendaItemDetails.Reminder -> AgendaItemType.REMINDER
    }
}
