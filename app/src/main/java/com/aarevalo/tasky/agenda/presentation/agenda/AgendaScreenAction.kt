package com.aarevalo.tasky.agenda.presentation.agenda

import com.aarevalo.tasky.agenda.presentation.agenda_detail.AgendaItemDetails
import java.time.LocalDate

sealed interface AgendaScreenAction {
    data class OnDateChanged(val date: LocalDate) : AgendaScreenAction
    data object OnChangeDatePickerVisibility: AgendaScreenAction
    data class OnDateSelectedCalendar(val date:LocalDate) : AgendaScreenAction
    data object OnLogout : AgendaScreenAction
    data class OnNavigateToAgendaDetail(
        val agendaItemId: String?,
        val isEditable: Boolean,
        val startDate: LocalDate = LocalDate.now(),
        val type: AgendaItemDetails
    ) : AgendaScreenAction
}
