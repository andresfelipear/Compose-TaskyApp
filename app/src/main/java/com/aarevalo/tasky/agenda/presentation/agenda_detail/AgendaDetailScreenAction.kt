package com.aarevalo.tasky.agenda.presentation.agenda_detail

import java.time.LocalDate

sealed interface AgendaDetailScreenAction {
    data object OnChangeFromDateDialogVisibility: AgendaDetailScreenAction
    data class OnFromDateChanged(val date: LocalDate): AgendaDetailScreenAction
}
