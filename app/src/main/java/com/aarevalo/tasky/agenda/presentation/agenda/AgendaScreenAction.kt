package com.aarevalo.tasky.agenda.presentation.agenda

import com.aarevalo.tasky.agenda.presentation.agenda_detail.AgendaItemDetails
import java.time.LocalDate

sealed interface AgendaScreenAction {
    data class OnDateChanged(val date: LocalDate) : AgendaScreenAction
    data class OnShowDatePicker(val showDatePicker: Boolean) : AgendaScreenAction
    data object OnDateSelectedCalendar : AgendaScreenAction
    data object OnLogout : AgendaScreenAction
    data class OnNavigateToAgendaDetail(
        val agendaItemId: String?,
        val isEditable: Boolean,
        val startDate: LocalDate = LocalDate.now(),
        val type: AgendaItemDetails
    ) : AgendaScreenAction
}
