package com.aarevalo.tasky.auth.presentation.login

import androidx.compose.foundation.text.input.TextFieldState
import com.aarevalo.tasky.core.presentation.util.UiText

data class LoginScreenState(
    val email: String = "",
    val passwordState: TextFieldState = TextFieldState(),
    val isPasswordVisible: Boolean = false,
    val isValidEmail: Boolean = false,
    val isValidPassword: Boolean = false,
    val infoMessage: List<UiText>? = emptyList(),
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false
)
