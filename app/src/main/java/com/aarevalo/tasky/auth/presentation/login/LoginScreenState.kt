package com.aarevalo.tasky.auth.presentation.login

import androidx.compose.foundation.text.input.TextFieldState

data class LoginScreenState(
    val email: String = "",
    val passwordState: TextFieldState = TextFieldState(),
    val isPasswordVisible: Boolean = false,
    val isValidEmail: Boolean = false,
    val isValidPassword: Boolean = false,
    val isLoading: Boolean = false
)
