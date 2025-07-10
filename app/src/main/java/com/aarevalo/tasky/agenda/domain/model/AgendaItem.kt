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
    val hostId: String
){
    companion object {
        const val PREFIX_EVENT_ID = "event_"
        const val PREFIX_REMINDER_ID = "reminder_"
        const val PREFIX_TASK_ID = "task_"

        fun getAgendaItemTypeFromItemId(itemId: String): AgendaItemType{
            return when{
                itemId.contains(this.PREFIX_EVENT_ID) -> AgendaItemType.EVENT
                itemId.contains(this.PREFIX_REMINDER_ID) -> AgendaItemType.REMINDER
                itemId.contains(this.PREFIX_TASK_ID) -> AgendaItemType.TASK
                else -> throw IllegalArgumentException("Invalid itemId: $itemId")
            }
        }
    }
}
