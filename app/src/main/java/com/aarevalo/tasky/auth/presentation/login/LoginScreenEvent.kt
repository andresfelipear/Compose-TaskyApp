package com.aarevalo.tasky.auth.presentation.login

import com.aarevalo.tasky.core.presentation.util.UiText

sealed interface LoginScreenEvent {
    data object Success : LoginScreenEvent
    data class Error(val errorMessage: UiText) : LoginScreenEvent
}