package com.aarevalo.tasky.agenda.presentation.agenda_detail

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarevalo.tasky.R
import com.aarevalo.tasky.agenda.domain.AgendaRepository
import com.aarevalo.tasky.agenda.domain.model.AgendaItem
import com.aarevalo.tasky.agenda.domain.model.AgendaItemType
import com.aarevalo.tasky.agenda.domain.model.Attendee
import com.aarevalo.tasky.agenda.domain.model.EventPhoto
import com.aarevalo.tasky.auth.domain.util.InputValidator
import com.aarevalo.tasky.core.domain.preferences.SessionStorage
import com.aarevalo.tasky.core.domain.util.Result
import com.aarevalo.tasky.core.presentation.ui.asUiText
import com.aarevalo.tasky.core.presentation.util.UiText
import com.aarevalo.tasky.core.util.stateInWhileSubscribed
import com.aarevalo.tasky.core.util.toTitleCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AgendaDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val sessionStorage: SessionStorage,
    private val inputValidator: InputValidator,
    private val applicationContext: Application,
    private val agendaRepository: AgendaRepository
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

    private val existingAgendaItemId = savedStateHandle.get<String>("agendaItemId")

    private val agendaItemType = savedStateHandle.get<String>("type")?.let {
        AgendaItemType.valueOf(it)
    }
    ?: throw IllegalArgumentException("Agenda item type must be provided")

    private val isEditable = savedStateHandle.get<Boolean>("isEditable")?:false

    private val deletedRemotePhotos = MutableStateFlow<List<EventPhoto.Remote>>(emptyList())

    fun onAction(action: AgendaDetailScreenAction){
        when(action){
            is AgendaDetailScreenAction.OnFromDateChanged -> {
                _state.update {
                    if(it.details is AgendaItemDetails.Event){
                        it.copy(
                            fromDate = action.date,
                            details = it.details.copy(
                                toDate = if(action.date > it.details.toDate) action.date else it.details.toDate
                            ),
                            isFromDateDialogVisible = false
                        )
                    } else {
                        it.copy(
                            fromDate = action.date,
                            isFromDateDialogVisible = false
                        )
                    }

                }
            }


            is AgendaDetailScreenAction.OnFromTimeChanged -> {
                _state.update {
                    if(it.details is AgendaItemDetails.Event) {
                        it.copy(
                            fromTime = action.time,
                            details = it.details.copy(
                                toTime = if(action.time > it.details.toTime) action.time else it.details.toTime
                            ),
                            isFromTimeDialogVisible = false
                        )
                    } else {
                        it.copy(
                            fromTime = action.time,
                            isFromTimeDialogVisible = false
                        )
                    }
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
                viewModelScope.launch {
                    agendaRepository.deleteAgendaItem(existingAgendaItemId!!)
                }
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
                    val eventDetails = state.value.details.asEventDetails
                    requireNotNull(eventDetails) { "Adding an attendee only possible for Events"}

                    if(state.value.isSavingItem){
                        return@launch
                    }

                    if(eventDetails.attendees.any { attendee -> attendee.email == action.email }){
                        eventChannel.send(
                            AgendaDetailScreenEvent.Error(
                                UiText.StringResource(id = R.string.attendee_already_added)
                            )
                        )
                    }

                    _state.update {
                        it.copy(
                            attendeesState = it.attendeesState.copy(
                                isAdding = true,
                            )
                        )
                    }


                    when(val attendeeResult = agendaRepository.getAttendee(action.email)){

                        is Result.Error -> {
                            _state.update {
                                it.copy(
                                    attendeesState = it.attendeesState.copy(
                                        isAdding = false,
                                    )
                                )
                            }
                            eventChannel.send(
                                AgendaDetailScreenEvent.Error(attendeeResult.error.asUiText())
                            )
                        }
                        is Result.Success -> {
                            attendeeResult.data.let { attendee ->
                                if(attendee != null){
                                    _state.update {
                                        it.copy(
                                            details = (it.details as AgendaItemDetails.Event).copy(
                                                attendees = it.details.attendees + attendee.copy(
                                                    eventId = existingAgendaItemId?:attendee.eventId
                                                ),
                                                isAddAttendeeDialogVisible = false,
                                            ),
                                            attendeesState = it.attendeesState.copy(
                                                isAdding = false,
                                                email = ""
                                            )
                                        )
                                    }
                                }
                                else{
                                    _state.update {
                                        it.copy(
                                            attendeesState = it.attendeesState.copy(
                                                isAdding = false,
                                                email = ""
                                            )
                                        )
                                    }
                                    eventChannel.send(
                                        AgendaDetailScreenEvent.Error(
                                            UiText.StringResource(id = R.string.attendee_not_found)
                                        )
                                    )
                                }
                            }
                        }
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

                viewModelScope.launch  {
                    val userId = sessionStorage.getSession()?.userId
                    if(userId != null){
                        println("userId: $userId")
                        if(existingAgendaItemId != null){
                            println("Agenda Item Details: ${state.value.details}")

                            val result = agendaRepository.updateAgendaItem(
                                agendaItem = AgendaItem(
                                    id = existingAgendaItemId,
                                    title = state.value.title,
                                    description = state.value.description,
                                    fromDate = state.value.fromDate,
                                    fromTime = state.value.fromTime,
                                    remindAt = state.value.remindAt,
                                    hostId = userId,
                                    details = state.value.details,
                                ),
                                deletedPhotoKeys = if(state.value.details is AgendaItemDetails.Event) deletedRemotePhotos.value.map { it.key } else emptyList(),
                                isGoing = if(state.value.details is AgendaItemDetails.Event) (state.value.details as AgendaItemDetails.Event).localAttendee?.isGoing ?: false else false
                            )

                            _state.update {
                                it.copy(
                                    isSavingItem = false
                                )
                            }
                            when(result){
                                is Result.Error -> {
                                    eventChannel.send(
                                        AgendaDetailScreenEvent.Error(
                                            result.error.asUiText()
                                        )
                                    )
                                }
                                is Result.Success -> {
                                    eventChannel.send(AgendaDetailScreenEvent.ItemSaved)
                                }
                            }
                        } else {
                            println("Creating new agenda item")

                            val result = when(agendaItemType){
                                AgendaItemType.EVENT -> {
                                    val eventId = AgendaItem.PREFIX_EVENT_ID + UUID.randomUUID().toString()
                                    agendaRepository.createAgendaItem(
                                        agendaItem = AgendaItem(
                                            id = eventId,
                                            title = _state.value.title,
                                            description = state.value.description,
                                            fromDate = state.value.fromDate,
                                            fromTime = state.value.fromTime,
                                            remindAt = state.value.remindAt,
                                            hostId = userId,
                                            details = (state.value.details as AgendaItemDetails.Event).copy(
                                                attendees = (state.value.details as AgendaItemDetails.Event).attendees.map { it.copy(
                                                    eventId = eventId
                                                ) }
                                            ),
                                        )
                                    )
                                }
                                AgendaItemType.TASK -> {
                                    println("Task details: ${state.value.details}")
                                    agendaRepository.createAgendaItem(
                                        agendaItem = AgendaItem(
                                            id = AgendaItem.PREFIX_TASK_ID + UUID.randomUUID().toString(),
                                            title = state.value.title,
                                            description = state.value.description,
                                            fromDate = state.value.fromDate,
                                            fromTime = state.value.fromTime,
                                            remindAt = state.value.remindAt,
                                            hostId = userId,
                                            details = state.value.details,
                                        )
                                    )
                                }
                                AgendaItemType.REMINDER -> {
                                    agendaRepository.createAgendaItem(
                                        agendaItem = AgendaItem(
                                            id = AgendaItem.PREFIX_REMINDER_ID + UUID.randomUUID().toString(),
                                            title = state.value.title,
                                            description = state.value.description,
                                            fromDate = state.value.fromDate,
                                            fromTime = state.value.fromTime,
                                            remindAt = state.value.remindAt,
                                            hostId = userId,
                                            details = state.value.details,
                                        )
                                    )
                                }
                            }
                            _state.update {
                                it.copy(
                                    isSavingItem = false
                                )
                            }
                            when(result){
                                is Result.Error -> {
                                    eventChannel.send(
                                        AgendaDetailScreenEvent.Error(
                                            result.error.asUiText()
                                        )
                                    )
                                }
                                is Result.Success -> {
                                    eventChannel.send(AgendaDetailScreenEvent.ItemCreated)
                                }
                            }

                        }
                    } else {
                        agendaRepository.logout()
                        eventChannel.send(AgendaDetailScreenEvent.GoingBackToLoginScreen)
                    }
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

        println("existingAgendaItemId: $existingAgendaItemId")
        println("agendaItemType: $agendaItemType")
        println("isEditable: $isEditable")

        if(existingAgendaItemId != null){
            viewModelScope.launch {
                val agendaItem = agendaRepository.getAgendaItemById(existingAgendaItemId)
                agendaItem?.let {
                    var details = agendaItem.details
                    if(agendaItem.details is AgendaItemDetails.Event){
                        val eventCreator : Attendee = agendaItem.details.attendees.first { it.userId == agendaItem.hostId }
                        val localAttendee : Attendee = agendaItem.details.attendees.first { it.userId == sessionStorage.getSession()?.userId }
                        details = (details as AgendaItemDetails.Event).copy(
                            eventCreator = eventCreator,
                            localAttendee = localAttendee
                        )
                    }
                    if(agendaItem.details is AgendaItemDetails.Task){
                        println("Task details: ${agendaItem.details}")
                    }

                    _state.update {
                        it.copy(
                            isEditable = isEditable,
                            title = agendaItem.title,
                            description = agendaItem.description,
                            fromDate = agendaItem.fromDate,
                            fromTime = agendaItem.fromTime,
                            details = details,
                            isItemCreated = true
                        )
                    }
                }
            }
        } else{
            _state.update {
                it.copy(
                    title = UiText.StringResource(id = R.string.new_agenda_item_title, args = arrayOf(agendaItemType)).asString(
                        applicationContext
                    ).toTitleCase(),
                    description = UiText.StringResource(id = R.string.new_agenda_item_description, args = arrayOf(agendaItemType)).asString(
                        applicationContext
                    ).toTitleCase(),
                    isEditable = isEditable,
                    details = AgendaItemDetails.fromAgendaItemType(agendaItemType),
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
                    requireNotNull(userId) {
                        println("User Id cannot be null when viewing agenda item")
                        navigateToLoginScreen()
                    }

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
        requireNotNull(userData) {
            println("User data cannot be null when joining an event")
            navigateToLoginScreen()
        }

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

    private suspend fun navigateToLoginScreen(){
        agendaRepository.logout()
        eventChannel.send(AgendaDetailScreenEvent.GoingBackToLoginScreen)
    }
}


