package com.aarevalo.tasky.agenda.presentation.agenda_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
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

    fun onAction(action: AgendaDetailScreenAction){
        when(action){
            is AgendaDetailScreenAction.OnChangeFromDateDialogVisibility -> {
                _state.update {
                    it.copy(
                        isFromDateDialogVisible = !it.isFromDateDialogVisible
                    )
                }
            }

            is AgendaDetailScreenAction.OnFromDateChanged -> {
                _state.update {
                    it.copy(
                        fromDate = action.date
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

            is AgendaDetailScreenAction.OnFromTimeChanged -> {
                println("From Time Changed ${action.time}")
                _state.update {
                    it.copy(
                        fromTime = action.time
                    )
                }
                println("From Time Changed ${_state.value.fromTime}")
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

            is AgendaDetailScreenAction.OnChangeDeleteDialogVisibility -> {
                _state.update {
                    it.copy(
                        isConfirmingToDeleteItem = !it.isConfirmingToDeleteItem
                    )
                }
            }
        }
    }
}
