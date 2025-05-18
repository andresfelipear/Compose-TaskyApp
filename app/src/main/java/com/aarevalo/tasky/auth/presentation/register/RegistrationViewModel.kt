package com.aarevalo.tasky.auth.presentation.register

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarevalo.tasky.R
import com.aarevalo.tasky.auth.domain.model.User
import com.aarevalo.tasky.auth.domain.repository.AuthenticationRepository
import com.aarevalo.tasky.auth.domain.util.InputValidator
import com.aarevalo.tasky.core.domain.util.DataError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.aarevalo.tasky.core.domain.util.Result
import com.aarevalo.tasky.core.presentation.ui.asUiText
import com.aarevalo.tasky.core.presentation.util.UiText
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val inputValidator: InputValidator,
    private val authRepository: AuthenticationRepository
) : ViewModel()
{
    private val _state = MutableStateFlow(RegistrationScreenState())
    val state = _state
        .onStart {
            observePasswordText()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _state.value
        )

    private val eventChannel = Channel<RegistrationEvent>()
    val event = eventChannel.receiveAsFlow()

    fun onAction(action: RegistrationAction) {
        when(action) {
            is RegistrationAction.OnNameChanged -> {
                val validationResult = action.name.length in MIN_NAME_LENGTH..MAX_NAME_LENGTH
                _state.update {
                    it.copy(
                        name = action.name,
                        isValidName = validationResult
                    )
                }
            }

            is RegistrationAction.OnEmailChanged -> {
                val validationResult = inputValidator.isValidEmailPattern(action.email)
                _state.update {
                    it.copy(
                        email = action.email,
                        isValidEmail = validationResult
                    )
                }
            }

            is RegistrationAction.OnPasswordVisibilityChanged -> {
                _state.update {
                    it.copy(isPasswordVisible = !action.isPasswordVisible)
                }
            }

            is RegistrationAction.OnRegister -> {
                register()
            }

            else -> Unit
        }
    }

    private fun observePasswordText(){
        snapshotFlow { state.value.passwordState.text }
            .distinctUntilChanged()
            .onEach { password ->
                val validationResult = inputValidator.isValidPassword(password.toString())
                _state.update {
                    it.copy(isValidPassword = validationResult.isValid)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun register() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            val result = authRepository.register(
                User(
                    name = state.value.name,
                    email = state.value.email,
                    password = state.value.passwordState.text.toString()
                )
            )
            _state.update {
                it.copy(isLoading = false)
            }

            when(result) {
                is Result.Error -> {
                    if(result.error == DataError.Network.CONFLICT){
                        eventChannel.send(RegistrationEvent.Error(
                            UiText.StringResource(R.string.error_user_already_exists)
                        ))
                    } else {
                        eventChannel.send(RegistrationEvent.Error(
                            result.error.asUiText()
                        ))
                    }
                }
                is Result.Success -> {
                    eventChannel.send(RegistrationEvent.Success)
                }
            }
        }
    }

    companion object{
        const val MIN_NAME_LENGTH = 5
        const val MAX_NAME_LENGTH = 49
    }
}



