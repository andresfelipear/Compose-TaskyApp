package com.aarevalo.tasky.agenda.presentation.agenda_detail

import com.aarevalo.tasky.agenda.domain.model.Attendee
import com.aarevalo.tasky.agenda.domain.model.EventPhoto
import com.aarevalo.tasky.agenda.domain.model.ReminderType
import com.aarevalo.tasky.core.presentation.util.UiText
import java.time.LocalDate
import java.time.LocalTime

private const val TO_TIME_ADDITION_MINUTES = 30L

data class AgendaDetailScreenState(
    val isEditable: Boolean = false,
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
    val details: AgendaItemDetails = AgendaItemDetails.Event(),
    val attendeesState: AttendeesState = AttendeesState(),
    )

sealed interface AgendaItemDetails{

    data class Event(
        val toTime: LocalTime = LocalTime.now().plusMinutes(TO_TIME_ADDITION_MINUTES),
        val toDate: LocalDate = LocalDate.now(),
        val photos: List<EventPhoto> = emptyList(),
        val attendees: List<Attendee> = emptyList(),
        val isUserEventCreator: Boolean = false,
        val eventCreator: Attendee? = null,
        val canAddVisitor: Boolean = false,
        val isAddAttendeeDialogVisible: Boolean = false,
        val localAttendee: Attendee? = null,
        val canEditPhotos: Boolean = false,
        val isAddingPhoto: Boolean = false,
        ): AgendaItemDetails

    data class Task(
        val isDone: Boolean = false,
    ): AgendaItemDetails


    data object Reminder: AgendaItemDetails
}

val AgendaItemDetails.asEventDetails: AgendaItemDetails.Event?
    get() = this as? AgendaItemDetails.Event

val AgendaItemDetails.asTaskDetails: AgendaItemDetails.Task?
    get() = this as? AgendaItemDetails.Task

val AgendaItemDetails.asReminderDetails: AgendaItemDetails.Reminder?
    get() = this as? AgendaItemDetails.Reminder

