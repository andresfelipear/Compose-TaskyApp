package com.aarevalo.tasky.auth.presentation.login

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarevalo.tasky.auth.domain.util.InputValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val inputValidator: InputValidator,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_IS_LOGGED = "isLogged"
    }

    private val isLogged = savedStateHandle.getStateFlow(
        KEY_IS_LOGGED,
        false
    )

    private val _state = MutableStateFlow(LoginScreenState())

    val state: StateFlow<LoginScreenState> = combine(
        isLogged,
        _state
    ){ isLogged, state ->
        LoginScreenState(
            isLoggedIn = isLogged,
            email = state.email,
            passwordState = state.passwordState,
            errorMessage = state.errorMessage,
            isPasswordVisible = state.isPasswordVisible,
            isValidEmail = state.isValidEmail,
        )
    }.onStart {
        observePasswordText()
    }
    .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LoginScreenState()
    )

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
                _state.update {
                    it.copy(isLoading = true)
                }
                //Simulate login process
                viewModelScope.launch {
                    delay(2000)
                    _state.update {
                        it.copy(isLoading = false)
                    }
                    savedStateHandle[KEY_IS_LOGGED] = true
                }
            }

            is LoginScreenAction.OnErrorMessageSeen -> {
                _state.update {
                    it.copy(errorMessage = null)
                }
            }

            else -> Unit
        }
    }

    private fun observePasswordText() {
        snapshotFlow { state.value.passwordState.text }.distinctUntilChanged()
            .onEach { password ->
                val validationResult = inputValidator.isValidPassword(password.toString())
                _state.update {
                    it.copy(isValidPassword = validationResult.isValid)
                }
            }
            .launchIn(viewModelScope)
    }
}
