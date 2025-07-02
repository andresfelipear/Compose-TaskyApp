package com.aarevalo.tasky.agenda.presentation.agenda

import com.aarevalo.tasky.core.presentation.util.UiText

sealed interface AgendaScreenEvent {
    data object Success : AgendaScreenEvent
    data object SuccessLogout : AgendaScreenEvent
    data object GoingBackToLoginScreen : AgendaScreenEvent
    data class Error(val errorMessage: UiText) : AgendaScreenEvent
}