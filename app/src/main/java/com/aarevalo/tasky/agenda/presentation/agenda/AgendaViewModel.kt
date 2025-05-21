package com.aarevalo.tasky.agenda.presentation.agenda

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class AgendaViewModel @Inject constructor(
    private val sessionStorage: SessionStorage
): ViewModel(){


    private val _state = MutableStateFlow(AgendaScreenState())
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
                _state.update {
                    it.copy(
                        date = action.date
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
            else -> Unit
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val session = sessionStorage.getSession()
            println("there is a access token?: ${session?.accessToken.isNullOrBlank().not()}")
            _state.update {
                it.copy(
                    initials = session?.fullName!!.toInitials()
                )
            }
        }
    }
}
