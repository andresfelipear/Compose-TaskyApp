package com.aarevalo.tasky.agenda.presentation.agenda_detail

import com.aarevalo.tasky.agenda.domain.model.EditTextFieldType
import com.aarevalo.tasky.agenda.domain.model.ReminderType
import com.aarevalo.tasky.agenda.domain.model.VisitorFilterType
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
    data object OnChangeToTimeDialogVisibility: AgendaDetailScreenAction
    data object OnChangeToDateDialogVisibility: AgendaDetailScreenAction
    data class OnToTimeChanged(val time: LocalTime): AgendaDetailScreenAction
    data class OnToDateChanged(val date: LocalDate): AgendaDetailScreenAction
    data class OnFilterTypeChanged(val filterType: VisitorFilterType): AgendaDetailScreenAction
    data class OnDeleteAttendee(val attendeeId: String): AgendaDetailScreenAction
    data object OnChangeIsAddAttendeeDialogVisibility: AgendaDetailScreenAction
    data class OnAddAttendee(val email: String): AgendaDetailScreenAction
    data class OnNewAttendeeEmailChanged(val email: String): AgendaDetailScreenAction
    data class OnUpdateIsGoingStatus(val isGoing: Boolean): AgendaDetailScreenAction
    data object OnChangeIsEditable: AgendaDetailScreenAction
    data class OnNavigateToEditTextScreen(val type: EditTextFieldType, val text: String): AgendaDetailScreenAction
    data class OnEditTitle(val title: String): AgendaDetailScreenAction
    data class OnEditDescription(val description: String): AgendaDetailScreenAction
}
