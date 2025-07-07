package com.aarevalo.tasky.agenda.presentation.agenda

import com.aarevalo.tasky.agenda.presentation.agenda_detail.AgendaItemDetails
import java.time.LocalDate

sealed interface AgendaScreenAction {
    data class OnDateChanged(val date: LocalDate) : AgendaScreenAction
    data object OnChangeDatePickerVisibility: AgendaScreenAction
    data class OnDateSelectedCalendar(val date:LocalDate) : AgendaScreenAction
    data object OnLogout : AgendaScreenAction
    data object OnChangeDeleteDialogVisibility : AgendaScreenAction
    data class OnChangeTaskStatus(
        val agendaItemId: String
    ) : AgendaScreenAction
    data class OnEditAgendaItemClick(
        val agendaItemId: String?,
        val type: AgendaItemDetails
    ) : AgendaScreenAction
    data class OnConfirmDeleteAgendaItem(
        val agendaItemId: String,
        val type: AgendaItemDetails
    ) : AgendaScreenAction
    data class OnOpenAgendaItemClick(
        val agendaItemId: String,
        val type: AgendaItemDetails
    ) : AgendaScreenAction
    data class OnCreateAgendaItemClick(
        val type: AgendaItemDetails
    ) : AgendaScreenAction
    data object OnDeleteAgendaItem : AgendaScreenAction
}
