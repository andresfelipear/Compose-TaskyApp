package com.aarevalo.tasky.agenda.presentation.agenda_detail

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarevalo.tasky.R
import com.aarevalo.tasky.agenda.domain.model.Attendee
import com.aarevalo.tasky.agenda.domain.model.EventPhoto
import com.aarevalo.tasky.auth.domain.util.InputValidator
import com.aarevalo.tasky.core.domain.preferences.SessionStorage
import com.aarevalo.tasky.core.presentation.util.UiText
import com.aarevalo.tasky.core.util.stateInWhileSubscribed
import com.aarevalo.tasky.core.util.toTitleCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AgendaDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val sessionStorage: SessionStorage,
    private val inputValidator: InputValidator,
    private val applicationContext: Application
): ViewModel() {

    private val _state = MutableStateFlow(
        AgendaDetailScreenState()
    )

    val state = _state
        .onStart {
            loadInitialData()
            observeAttendeeChanges()
        }.stateInWhileSubscribed(
            scope = viewModelScope,
            initialValue = _state.value
        )

    private val eventChannel = Channel<AgendaDetailScreenEvent>()
    val event = eventChannel.receiveAsFlow()


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
                        attendeesState = it.attendeesState.copy(
                            selectedFilter = action.filterType
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
                        attendeesState = it.attendeesState.copy(
                            email = action.email,
                            isEmailValid = validationResult
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
                            attendeesState = it.attendeesState.copy(
                                isAdding = true
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
                            ),
                            attendeesState = it.attendeesState.copy(
                                isAdding = false,
                                email = ""
                            )
                        )
                    }
                }
            }

            is AgendaDetailScreenAction.OnUpdateIsGoingStatus -> {
                if(action.isGoing == _state.value.details.asEventDetails?.localAttendee?.isGoing){
                    return
                }
                viewModelScope.launch {
                    toggleAttendeeGoingState(action.isGoing)
                }
            }

            is AgendaDetailScreenAction.OnEditTitle -> {
                _state.update {
                    it.copy(
                        title = action.title
                    )
                }
            }

            is AgendaDetailScreenAction.OnEditDescription -> {
                _state.update {
                    it.copy(
                        description = action.description
                    )
                }
            }

            is AgendaDetailScreenAction.OnChangeTaskStatus -> {
                val taskDetails = state.value.details.asTaskDetails
                requireNotNull(taskDetails) { "Changing task status only possible for Tasks"}

                _state.update {
                    it.copy(
                        details = taskDetails.copy(
                            isDone = !taskDetails.isDone
                        )
                    )
                }
            }

            is AgendaDetailScreenAction.OnSaveChanges -> {
                _state.update {
                    it.copy(
                        isSavingItem = true
                    )
                }
                /* TODO save changes in the api */
                viewModelScope.launch {
                    delay(timeMillis = 1000)
                    _state.update {
                        it.copy(
                            isSavingItem = false
                        )
                    }
                    eventChannel.send(AgendaDetailScreenEvent.ItemSaved)
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

            else -> Unit
        }
    }

    private fun loadInitialData() {

        val existingAgendaItemId = savedStateHandle.get<String>("agendaItemId")
        val agendaItemType = savedStateHandle.get<String>("type")
            ?: throw IllegalArgumentException("Agenda item type must be provided")
        val isEditable = savedStateHandle.get<Boolean>("isEditable")?:false

        println("existingAgendaItemId: $existingAgendaItemId")
        println("agendaItemType: $agendaItemType")
        println("isEditable: $isEditable")

        if(existingAgendaItemId != null){
            /* TODO fetch agenda item from api */
        } else{
            _state.update {
                it.copy(
                    details = AgendaItemDetails.fromString(agendaItemType),
                    isEditable = isEditable,
                    title = UiText.StringResource(id = R.string.new_agenda_item_title, args = arrayOf(agendaItemType)).asString(
                        applicationContext
                    ).toTitleCase(),
                    description = UiText.StringResource(id = R.string.new_agenda_item_description, args = arrayOf(agendaItemType)).asString(
                        applicationContext
                    ).toTitleCase()
                )
            }
        }
    }

    private fun observeAttendeeChanges(){
        viewModelScope.launch {

            _state
                .mapNotNull { it.details.asEventDetails?.attendees }
                .distinctUntilChanged()
                .onEach { attendees ->
                    val (going, notGoing) = attendees.partition { it.isGoing }
                    val userId = sessionStorage.getSession()?.userId
                    requireNotNull(userId) { "User Id cannot be null when viewing agenda item"}

                    _state.update {
                        it.copy(
                            attendeesState = it.attendeesState.copy(
                                going = going,
                                notGoing = notGoing,
                                isUserGoing = going.map { attendee -> attendee.userId }.contains(userId)
                            )
                        )
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    private suspend fun toggleAttendeeGoingState(isGoing: Boolean){
        val eventDetails = state.value.details.asEventDetails
        requireNotNull(eventDetails) { "Joining an event only possible for Events"}

        val userData = sessionStorage.getSession()
        requireNotNull(userData) { "User data cannot be null when joining an event"}

        val newAttendeesList = eventDetails.attendees.toMutableList().apply {
            replaceAll{ attendee ->
                if(attendee.userId == userData.userId) attendee.copy(isGoing = isGoing)
                else attendee
            }
        }.toList()

        _state.update {
            it.copy(
                details = eventDetails.copy(
                    attendees = newAttendeesList
                ),
                attendeesState = it.attendeesState.copy(
                    isUserGoing = newAttendeesList.filter { it.isGoing }.map { it.userId }.contains(userData.userId)
                )
            )
        }

    }
}


