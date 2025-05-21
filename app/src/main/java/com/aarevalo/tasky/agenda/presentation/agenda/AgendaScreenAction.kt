package com.aarevalo.tasky.agenda.presentation.agenda

import java.time.LocalDate

sealed interface AgendaScreenAction {
    data class OnDateChanged(val date: LocalDate) : AgendaScreenAction
    data class OnShowDatePicker(val showDatePicker: Boolean) : AgendaScreenAction
}
