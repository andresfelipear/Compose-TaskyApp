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
                _state.update {
                    it.copy(
                        fromTime = action.time
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

            is AgendaDetailScreenAction.OnToDateChanged -> {
                _state.update {
                    it.copy(
                        details = (it.details as AgendaItemDetails.Event).copy(
                            toDate = action.date
                        )
                    )
                }
            }

            is AgendaDetailScreenAction.OnToTimeChanged -> {
                _state.update {
                    it.copy(
                        details = (it.details as AgendaItemDetails.Event).copy(
                            toTime = action.time
                        )
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
