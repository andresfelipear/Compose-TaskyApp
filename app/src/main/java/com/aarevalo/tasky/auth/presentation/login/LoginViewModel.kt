package com.aarevalo.tasky.auth.presentation.login

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
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
class LoginViewModel @Inject constructor(
    private val inputValidator: InputValidator,
    private val savedStateHandle: SavedStateHandle,
    private val authRepository: AuthenticationRepository
) : ViewModel() {

    companion object {
        private const val KEY_IS_LOGGED = "isLogged"
    }

    private val _state = MutableStateFlow(
        LoginScreenState(
            isLoggedIn = savedStateHandle[KEY_IS_LOGGED] ?: false,
            email = "",
            passwordState = TextFieldState(),
            isPasswordVisible = false,
            isValidEmail = false,
        )
    )

    val state = _state
        .onStart {
            observePasswordText()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _state.value
        )

    private val eventChannel = Channel<LoginScreenEvent>()
    val event = eventChannel.receiveAsFlow()

    fun onAction(action: LoginScreenAction) {
        when(action) {
            is LoginScreenAction.OnEmailChanged -> {
                val validationResult = inputValidator.isValidEmailPattern(action.email)
                _state.update {
                    it.copy(
                        email = action.email,
                        isValidEmail = validationResult
                    )
                }
            }

            is LoginScreenAction.OnPasswordVisibilityChanged -> {
                _state.update {
                    it.copy(isPasswordVisible = !action.isPasswordVisible)
                }
            }

            is LoginScreenAction.OnLogin -> {
                login()
            }
            else -> Unit
        }
    }

    private fun observePasswordText() {
        snapshotFlow { _state.value.passwordState.text }.distinctUntilChanged()
            .onEach { password ->
                val validationResult = inputValidator.isValidPassword(password.toString())
                _state.update {
                    it.copy(isValidPassword = validationResult.isValid)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun login() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            val result = authRepository.login(
                User(
                    email = state.value.email,
                    password = state.value.passwordState.text.toString()
                )
            )
            _state.update {
                it.copy(isLoading = false)
            }

            when(result) {
                is Result.Error -> {
                    if(result.error == DataError.Network.UNAUTHORIZED) {
                        eventChannel.send(LoginScreenEvent.Error(
                            UiText.StringResource(R.string.error_email_password_incorrect)
                        ))
                    } else {
                        eventChannel.send(LoginScreenEvent.Error(result.error.asUiText()))
                    }
                }
                is Result.Success -> {
                    savedStateHandle[KEY_IS_LOGGED] = true
                    _state.update {
                        it.copy(isLoggedIn = true)
                    }
                    eventChannel.send(LoginScreenEvent.Success)
                }
            }
        }
    }
}
