package com.aarevalo.tasky.agenda.presentation.agenda_detail

import com.aarevalo.tasky.agenda.domain.model.ReminderType
import java.time.LocalDate
import java.time.LocalTime

sealed interface AgendaDetailScreenAction {
    data object OnChangeFromDateDialogVisibility: AgendaDetailScreenAction
    data class OnFromDateChanged(val date: LocalDate): AgendaDetailScreenAction
    data object OnChangeFromTimeDialogVisibility: AgendaDetailScreenAction
    data class OnFromTimeChanged(val time: LocalTime): AgendaDetailScreenAction
    data class OnReminderTypeChanged(val reminderType: ReminderType): AgendaDetailScreenAction
    data object OnDeleteItem: AgendaDetailScreenAction
    data object OnChangeDeleteDialogVisibility: AgendaDetailScreenAction
}
