package com.aarevalo.tasky.agenda.presentation.agenda

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarevalo.tasky.agenda.domain.AgendaItem
import com.aarevalo.tasky.agenda.presentation.agenda.AgendaScreenState.Companion.RANGE_DAYS
import com.aarevalo.tasky.agenda.presentation.agenda_detail.AgendaItemDetails
import com.aarevalo.tasky.auth.presentation.login.LoginScreenEvent
import com.aarevalo.tasky.core.domain.preferences.SessionStorage
import com.aarevalo.tasky.core.util.toInitials
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@HiltViewModel
class AgendaViewModel @Inject constructor(
    private val sessionStorage: SessionStorage
): ViewModel(){


    private val _state = MutableStateFlow(AgendaScreenState(
        agendaItems = listOf(
            AgendaItem(
                id = "1",
                title = "Event 1",
                description = "Event 1 description",
                fromTime = LocalTime.now()
                    .plusMinutes(31)
            ),
            AgendaItem(
                id = "2",
                title = "Event 2",
                description = "Event 2 description",
                fromTime = LocalTime.now()
                    .plusMinutes(32),
                details = AgendaItemDetails.Task()
            ),
            AgendaItem(
                id = "3",
                title = "Event 1",
                description = "Event 1 description",
                fromTime = LocalTime.now()
                    .plusMinutes(33)
            ),
            AgendaItem(
                id = "4",
                title = "Event 2",
                description = "Event 2 description",
                fromTime = LocalTime.now()
                    .plusMinutes(0),
                details = AgendaItemDetails.Reminder
            )
        )
    ))

    val state = _state
        .onStart {
            loadInitialData()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _state.value
        )

    private val eventChannel = Channel<LoginScreenEvent>()
    val event = eventChannel.receiveAsFlow()

    fun onAction(action: AgendaScreenAction) {
        when(action) {
            is AgendaScreenAction.OnDateChanged -> {
                _state.update { currentState ->
                    currentState.copy(
                        selectedDate = action.date,
                        relatedDates = getRelatedDates(action.date),
                        datePickerState = currentState.datePickerState.apply {
                            selectedDateMillis = action.date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
                        }
                    )
                }
            }

            is AgendaScreenAction.OnShowDatePicker -> {
                _state.update {
                    it.copy(
                        showDatePicker = action.showDatePicker
                    )
                }
            }
            is AgendaScreenAction.OnDateSelectedCalendar -> {
                _state.update {
                    val selectedDateMillis = it.datePickerState.selectedDateMillis
                    val selectedDate = Instant.ofEpochMilli(selectedDateMillis!!).atZone(ZoneOffset.UTC).toLocalDate()
                    it.copy(
                        showDatePicker = false,
                        selectedDate = selectedDate,
                        relatedDates = getRelatedDates(selectedDate)
                    )
                }
            }
            else -> {}
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val session = sessionStorage.getSession()
            _state.update {
                it.copy(
                    initials = session?.fullName!!.toInitials()
                )
            }
        }
    }

    private fun getRelatedDates(date: LocalDate): List<LocalDate>{
        val start = date.minusDays(RANGE_DAYS)
        val end   = date.plusDays(RANGE_DAYS)

        return generateSequence(start) {
            it.plusDays(1)
        }.takeWhile{ !it.isAfter(end)}
            .toList()
    }
}
