package com.aarevalo.tasky.auth.presentation.login

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarevalo.tasky.auth.domain.util.InputValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val inputValidator: InputValidator,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {


    val state = savedStateHandle.getStateFlow(
        "state",
        LoginScreenState()
    )
        .onStart {
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
                savedStateHandle["state"] = state.value.copy(
                    email = action.email,
                    isValidEmail = validationResult
                )
            }

            is LoginScreenAction.OnPasswordVisibilityChanged -> {
                savedStateHandle["state"] = state.value.copy(
                    isPasswordVisible = !state.value.isPasswordVisible
                )
            }

            is LoginScreenAction.OnLogin -> {
                TODO()
            }

            is LoginScreenAction.OnErrorMessageSeen -> {
                savedStateHandle["state"] = state.value.copy(
                    errorMessage = null
                )
            }

            else -> Unit
        }
    }

    private fun observePasswordText() {
        snapshotFlow { state.value.passwordState.text }.distinctUntilChanged()
            .onEach { password ->
                val validationResult = inputValidator.isValidPassword(password.toString())
                savedStateHandle["state"] = state.value.copy(
                    isValidPassword = validationResult.isValid
                )
            }
            .launchIn(viewModelScope)
    }
}
