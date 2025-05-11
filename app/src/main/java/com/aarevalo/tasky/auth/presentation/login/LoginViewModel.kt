package com.aarevalo.tasky.auth.presentation.login

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarevalo.tasky.auth.domain.util.InputValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val inputValidator: InputValidator
) : ViewModel() {

    private val _state = MutableStateFlow(LoginScreenState())
    val state = _state
        .onStart {
            observePasswordText()
        }
        .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = _state.value
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
                TODO()
            }

            is LoginScreenAction.OnErrorMessageSeen -> {
                _state.update {
                    it.copy(errorMessage = null)
                }
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

}
