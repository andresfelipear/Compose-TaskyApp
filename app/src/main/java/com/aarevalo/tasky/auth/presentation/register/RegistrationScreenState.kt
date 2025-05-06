package com.aarevalo.tasky.auth.presentation.register

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class RegistrationScreenState(
    val name: String = "",
    val email: String = "",
    val passwordState: MutableState<TextFieldState> = mutableStateOf(TextFieldState()),
    val isValidName: Boolean = false,
    val isValidEmail: Boolean = false,
    val isValidPassword: Boolean = false,
    val isLoading: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val error: String? = null,
)
