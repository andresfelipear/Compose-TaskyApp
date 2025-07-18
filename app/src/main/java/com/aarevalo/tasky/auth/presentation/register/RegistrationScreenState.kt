package com.aarevalo.tasky.auth.presentation.register

import androidx.compose.foundation.text.input.TextFieldState
import com.aarevalo.tasky.core.presentation.util.UiText

data class RegistrationScreenState(
    val name: String = "",
    val email: String = "",
    val passwordState: TextFieldState = TextFieldState(),
    val isValidName: Boolean = false,
    val isValidEmail: Boolean = false,
    val isValidPassword: Boolean = false,
    val isLoading: Boolean = false,
    val isPasswordVisible: Boolean = false,
)
