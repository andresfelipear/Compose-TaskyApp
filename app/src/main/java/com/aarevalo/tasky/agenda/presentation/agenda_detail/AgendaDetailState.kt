package com.aarevalo.tasky.agenda.presentation.agenda_detail

import androidx.compose.foundation.text.input.TextFieldState
import com.aarevalo.tasky.agenda.domain.model.VisitorFilterType
import com.aarevalo.tasky.agenda.domain.model.Attendee
import com.aarevalo.tasky.agenda.domain.model.ReminderType
import com.aarevalo.tasky.core.presentation.util.UiText
import java.time.LocalDate
import java.time.LocalTime

private const val TO_TIME_ADDITION_MINUTES = 30L

data class AgendaDetailState(
    val isEditable: Boolean = true,
    val reminderType: ReminderType = ReminderType.ONE_HOUR,
    val fromTime: LocalTime = LocalTime.now(),
    val fromDate: LocalDate = LocalDate.now(),
    val description: String = "Event description",
    val title: String = "New Event",
    val isSelectingReminder: Boolean = false,
    val isLoadingItem: Boolean = false,
    val isSavingItem: Boolean = false,
    val infoMessage: UiText? = null,
    val isConfirmingToDeleteItem: Boolean = false,
    val isDeletingItem: Boolean = false,
    val isItemDeleted: Boolean = false,
    val isItemCreated: Boolean = false,
    val isLoggedIn: Boolean = true,
    val isFromDateDialogVisible: Boolean = false,
    val isFromTimeDialogVisible: Boolean = false,
    val isToDateDialogVisible: Boolean = false,
    val isToTimeDialogVisible: Boolean = false,
    val details: AgendaItemDetails = AgendaItemDetails.Event()
)

sealed interface AgendaItemDetails{
    data class Event(
        val toTime: LocalTime = LocalTime.now().plusMinutes(TO_TIME_ADDITION_MINUTES),
        val toDate: LocalDate = LocalDate.now(),
        val attendees: List<Attendee> = emptyList(),
        val isUserEventCreator: Boolean = false,
        val eventCreator: Attendee? = null,
        val isCheckingForAttendeesExistence: Boolean = false,
        val canAddVisitor: Boolean = false,
        val isAddingAttendee: Boolean = false,
        val filterType: VisitorFilterType = VisitorFilterType.ALL,
        val addAttendeeError: UiText? = null,
        val newAttendeeEmail: TextFieldState = TextFieldState(),
        val localAttendee: Attendee? = null,
        val canEditPhotos: Boolean = false,
        val isAddingPhoto: Boolean = false,
        ): AgendaItemDetails

    data class Task(
        val isDone: Boolean = false,
    ): AgendaItemDetails


    data object Reminder: AgendaItemDetails
}
