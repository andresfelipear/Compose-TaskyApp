package com.aarevalo.tasky.auth.presentation.register

import com.aarevalo.tasky.core.presentation.util.UiText

sealed interface RegistrationEvent {
    data object Success : RegistrationEvent
    data class Error(val errorMessage: UiText) : RegistrationEvent
}
