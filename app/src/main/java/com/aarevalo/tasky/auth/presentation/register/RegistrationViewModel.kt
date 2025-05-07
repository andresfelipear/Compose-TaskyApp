package com.aarevalo.tasky.auth.presentation.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.aarevalo.tasky.auth.domain.model.AuthConstants.MAX_NAME_LENGTH
import com.aarevalo.tasky.auth.domain.model.AuthConstants.MIN_NAME_LENGTH
import com.aarevalo.tasky.auth.domain.util.PasswordValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RegistrationViewModel: ViewModel(
) {
    private val _state = MutableStateFlow(RegistrationScreenState())
    val state = _state.asStateFlow()

    fun onAction(action: RegistrationAction) {
        when(action) {
            is RegistrationAction.OnNameChanged -> {
                val validationResult = action.name.length in MIN_NAME_LENGTH..MAX_NAME_LENGTH
                _state.value = _state.value.copy(name = action.name, isValidName = validationResult)

            }
            is RegistrationAction.OnEmailChanged -> {
                val validationResult = Patterns.EMAIL_ADDRESS.matcher(action.email).matches()
                _state.value = _state.value.copy(email = action.email, isValidEmail = validationResult)
            }
            is RegistrationAction.OnPasswordVisibilityChanged -> {
                _state.value = _state.value.copy(isPasswordVisible = !action.isPasswordVisible)
            }
            is RegistrationAction.OnRegister -> {
                TODO()
            }
            is RegistrationAction.OnPasswordChanged -> {
                val validationResult = PasswordValidator.validate(action.password)
                _state.value = _state.value.copy(
                    isValidPassword = validationResult.isValid,
                )
            }
            else -> Unit
        }
    }
}
