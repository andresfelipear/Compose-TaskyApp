package com.aarevalo.tasky.agenda.presentation.agenda_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.aarevalo.tasky.agenda.domain.model.EventPhoto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AgendaDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
): ViewModel() {

    private val _state = MutableStateFlow(
        AgendaDetailScreenState()
    )

    val state = _state

    private var shouldUpdateItem = false

    private val deletedRemotePhotos = MutableStateFlow<List<EventPhoto.Remote>>(emptyList())

    fun onAction(action: AgendaDetailScreenAction){
        when(action){
            is AgendaDetailScreenAction.OnFromDateChanged -> {
                shouldUpdateItem = true
                _state.update {
                    it.copy(
                        fromDate = action.date,
                        details = (it.details as AgendaItemDetails.Event).copy(
                            toDate = if(action.date > it.details.toDate) action.date else it.details.toDate
                        ),
                        isFromDateDialogVisible = false
                    )
                }
            }


            is AgendaDetailScreenAction.OnFromTimeChanged -> {
                shouldUpdateItem = true
                _state.update {
                    it.copy(
                        fromTime = action.time,
                        details = (it.details as AgendaItemDetails.Event).copy(
                            toTime = if(action.time > it.details.toTime) action.time else it.details.toTime
                        ),
                        isFromTimeDialogVisible = false
                    )
                }
            }


            is AgendaDetailScreenAction.OnToDateChanged -> {
                shouldUpdateItem = true
                _state.update {
                    it.copy(
                        details = (it.details as AgendaItemDetails.Event).copy(
                            toDate = action.date
                        ),
                        fromDate = if(action.date < it.fromDate) action.date else it.fromDate,
                        isToDateDialogVisible = false
                    )
                }
            }

            is AgendaDetailScreenAction.OnToTimeChanged -> {
                shouldUpdateItem = true
                _state.update {
                    it.copy(
                        details = (it.details as AgendaItemDetails.Event).copy(
                            toTime = action.time
                        ),
                        fromTime = if(action.time < it.fromTime) action.time else it.fromTime,
                        isToTimeDialogVisible = false
                    )
                }
            }

            is AgendaDetailScreenAction.OnReminderTypeChanged -> {
                shouldUpdateItem = true
                _state.update {
                    it.copy(
                        reminderType = action.reminderType
                    )
                }
            }

            is AgendaDetailScreenAction.OnDeleteItem -> {

                _state.update {
                    it.copy(
                        isDeletingItem = true,
                    )
                }
                /** TODO delete item */
                _state.update {
                    it.copy(
                        isConfirmingToDeleteItem = false,
                        isDeletingItem = false,
                        isItemDeleted = true
                    )
                }
            }

            is AgendaDetailScreenAction.OnDeleteAttendee -> {
                if(state.value.isSavingItem){
                    return
                }
                shouldUpdateItem = true
                _state.update{
                    it.copy(
                        details = (it.details as AgendaItemDetails.Event).copy(
                            attendees = it.details.attendees.filter { attendee ->
                                attendee.id != action.attendeeId
                            }
                        )
                    )
                }
            }

            is AgendaDetailScreenAction.OnFilterTypeChanged -> {
                if(state.value.isSavingItem){
                    return
                }
                _state.update {
                    it.copy(
                        details = (it.details as AgendaItemDetails.Event).copy(
                            filterType = action.filterType
                        )
                    )
                }
            }

            is AgendaDetailScreenAction.OnChangeFromDateDialogVisibility -> {
                _state.update {
                    it.copy(
                        isFromDateDialogVisible = !it.isFromDateDialogVisible
                    )
                }
            }

            is AgendaDetailScreenAction.OnChangeFromTimeDialogVisibility -> {
                _state.update {
                    it.copy(
                        isFromTimeDialogVisible = !it.isFromTimeDialogVisible
                    )
                }
            }

            is AgendaDetailScreenAction.OnChangeToDateDialogVisibility -> {
                _state.update {
                    it.copy(
                        isToDateDialogVisible = !it.isToDateDialogVisible
                    )
                }
            }

            is AgendaDetailScreenAction.OnChangeToTimeDialogVisibility -> {
                _state.update {
                    it.copy(
                        isToTimeDialogVisible = !it.isToTimeDialogVisible
                    )
                }
            }

            is AgendaDetailScreenAction.OnChangeDeleteDialogVisibility -> {
                _state.update {
                    it.copy(
                        isConfirmingToDeleteItem = !it.isConfirmingToDeleteItem
                    )
                }
            }

            is AgendaDetailScreenAction.OnChangeIsAddingAttendeeVisibility -> {
                _state.update {
                    it.copy(
                        details = (it.details as AgendaItemDetails.Event).copy(
                            isAddingAttendee = !it.details.isAddingAttendee
                        )
                    )
                }
            }
        }
    }
}
