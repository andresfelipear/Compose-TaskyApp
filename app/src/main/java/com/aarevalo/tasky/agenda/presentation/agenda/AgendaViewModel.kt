package com.aarevalo.tasky.agenda.presentation.agenda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarevalo.tasky.agenda.domain.AgendaRepository
import com.aarevalo.tasky.agenda.presentation.agenda.AgendaScreenState.Companion.RANGE_DAYS
import com.aarevalo.tasky.core.domain.preferences.SessionStorage
import com.aarevalo.tasky.core.domain.util.Result
import com.aarevalo.tasky.core.presentation.ui.asUiText
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
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AgendaViewModel @Inject constructor(
    private val sessionStorage: SessionStorage,
    private val agendaRepository: AgendaRepository
): ViewModel(){


    private val _state = MutableStateFlow(AgendaScreenState(
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

    private val eventChannel = Channel<AgendaScreenEvent>()
    val event = eventChannel.receiveAsFlow()

    fun onAction(action: AgendaScreenAction) {
        when(action) {
            is AgendaScreenAction.OnDateChanged -> {
                _state.update { currentState ->
                    currentState.copy(
                        selectedDate = action.date,
                        relatedDates = getRelatedDates(action.date),
                    )
                }
            }

            is AgendaScreenAction.OnChangeDatePickerVisibility -> {
                _state.update {
                    it.copy(
                        showDatePicker = !it.showDatePicker
                    )
                }
            }
            is AgendaScreenAction.OnDateSelectedCalendar -> {
                val selectedDate = action.date
                _state.update {
                    it.copy(
                        selectedDate = selectedDate,
                        relatedDates = getRelatedDates(selectedDate),
                        showDatePicker = false,
                    )
                }
            }
            is AgendaScreenAction.OnLogout -> {
                viewModelScope.launch {
                    when(val result = agendaRepository.logout()) {
                        is Result.Error -> {
                            eventChannel.send(
                                AgendaScreenEvent.Error(
                                    result.error.asUiText()
                                ))
                        }
                        is Result.Success -> {
                            eventChannel.send(AgendaScreenEvent.SuccessLogout)
                        }
                    }
                }
            }
            is AgendaScreenAction.OnDeleteAgendaItem -> {
                /* TODO delete agenda item remotely and in the database */
                _state.update {
                    it.copy(
                        agendaItems = it.agendaItems.filter { item ->
                            item.id != action.agendaItemId
                        }
                    )
                }
            }
            else -> Unit

        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val session = sessionStorage.getSession()
            _state.update {
                it.copy(
                    initials = session?.fullName!!.toInitials(),
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
