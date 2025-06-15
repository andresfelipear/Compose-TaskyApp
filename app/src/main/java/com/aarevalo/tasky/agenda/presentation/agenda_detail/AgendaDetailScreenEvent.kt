package com.aarevalo.tasky.agenda.presentation.agenda_detail

import com.aarevalo.tasky.core.presentation.util.UiText

interface AgendaDetailScreenEvent {
    data object Success : AgendaDetailScreenEvent
    data class Error(val errorMessage: UiText) : AgendaDetailScreenEvent
}