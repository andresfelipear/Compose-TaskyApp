package com.aarevalo.tasky.agenda.presentation.agenda_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarevalo.tasky.agenda.domain.model.Attendee
import com.aarevalo.tasky.agenda.domain.model.EventPhoto
import com.aarevalo.tasky.auth.domain.util.InputValidator
import com.aarevalo.tasky.core.domain.preferences.SessionStorage
import com.aarevalo.tasky.core.util.stateInWhileSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AgendaDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val sessionStorage: SessionStorage,
    private val inputValidator: InputValidator
): ViewModel() {

    private val _state = MutableStateFlow(
        AgendaDetailScreenState()
    )
    private lateinit var localUserId: String

    val state = _state
        .onStart {
            loadInitialData()
            observeAttendeeChanges()
        }.stateInWhileSubscribed(
            scope = viewModelScope,
            initialValue = _state.value
        )


    private val deletedRemotePhotos = MutableStateFlow<List<EventPhoto.Remote>>(emptyList())

    fun onAction(action: AgendaDetailScreenAction){
        when(action){
            is AgendaDetailScreenAction.OnFromDateChanged -> {
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
                _state.update{
                    it.copy(
                        details = (it.details as AgendaItemDetails.Event).copy(
                            attendees = it.details.attendees.filter { attendee ->
                                attendee.userId != action.attendeeId
                            },
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
                            attendeesState = it.details.attendeesState.copy(
                                selectedFilter = action.filterType
                            )
                        )
                    )
                }
            }

            is AgendaDetailScreenAction.OnNewAttendeeEmailChanged -> {
                if(state.value.isSavingItem){
                    return
                }
                val validationResult = inputValidator.isValidEmailPattern(action.email)
                _state.update {
                    it.copy(
                        details = (it.details as AgendaItemDetails.Event).copy(
                            attendeesState = it.details.attendeesState.copy(
                                email = action.email,
                                isEmailValid = validationResult
                            )
                        )
                    )
                }
            }

            is AgendaDetailScreenAction.OnAddAttendee -> {
                viewModelScope.launch {
                    if(state.value.isSavingItem){
                        return@launch
                    }
                    _state.update {
                        it.copy(
                            details = (it.details as AgendaItemDetails.Event).copy(
                                attendeesState = it.details.attendeesState.copy(
                                    isAdding = true
                                )
                            )
                        )
                    }
                    /* TODO fetch attendee fullName from api - simulate behavior*/
                    delay(timeMillis = 1000)
                    val attendee = Attendee(
                        userId = UUID.randomUUID().toString(),
                        fullName = "Test Name",
                        email = action.email,
                        isGoing = true,
                        reminderAt = LocalDateTime.now()
                    )
                    _state.update {
                        it.copy(
                            details = (it.details as AgendaItemDetails.Event).copy(
                                attendees = it.details.attendees + attendee,
                                isAddAttendeeDialogVisible = false,
                                attendeesState = it.details.attendeesState.copy(
                                    isAdding = false,
                                    email = ""
                                )
                            )
                        )
                    }
                }
            }

            is AgendaDetailScreenAction.OnUpdateIsGoingStatus -> {
                if(action.isGoing == _state.value.details.asEventDetails?.localAttendee?.isGoing){
                    return
                }
                _state.update {
                    val newAttendees = (it.details as AgendaItemDetails.Event).attendees.map {  attendee ->
                        if(attendee.userId == localUserId){
                            attendee.copy(
                                isGoing = action.isGoing
                            )
                        } else {
                            attendee
                        }
                    }
                    it.copy(
                        details = it.details.copy(
                            attendees = newAttendees,
                            localAttendee = newAttendees.find { attendee ->
                                attendee.userId == localUserId
                            }
                        )
                    )
                }
            }

            is AgendaDetailScreenAction.OnChangeIsEditable -> {
                _state.update {
                    it.copy(
                        isEditable = !it.isEditable
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

            is AgendaDetailScreenAction.OnChangeIsAddAttendeeDialogVisibility -> {
                _state.update {
                    it.copy(
                        details = (it.details as AgendaItemDetails.Event).copy(
                            isAddAttendeeDialogVisible = !it.details.isAddAttendeeDialogVisible
                        )
                    )
                }
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val session = sessionStorage.getSession()
            /* TODO handle null userId */
            localUserId = session?.userId!!
        }
    }

    private fun observeAttendeeChanges(){
        viewModelScope.launch {
            _state
                .mapNotNull { it.details.asEventDetails?.attendees }
                .distinctUntilChanged()
                .onEach { attendees ->
                    val goingAttendees = attendees.filter { it.isGoing }
                    val notGoingAttendees = attendees.filter { !it.isGoing }
                    _state.update { currentState ->
                        val currentDetails = currentState.details.asEventDetails
                        if(currentDetails != null){
                            currentState.copy(
                                details = currentDetails.copy(
                                    attendeesState = currentDetails.attendeesState.copy(
                                        going = goingAttendees,
                                        notGoing = notGoingAttendees
                                    )
                                )
                            )
                        } else {
                            currentState
                        }
                    }
                }
                .launchIn(viewModelScope)
        }
    }
}


