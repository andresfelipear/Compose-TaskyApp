package com.aarevalo.tasky.agenda.presentation.agenda_detail

import com.aarevalo.tasky.core.presentation.util.UiText

sealed interface AgendaDetailScreenEvent {
    data object AgendaItemNotFound : AgendaDetailScreenEvent
    data object ItemSaved : AgendaDetailScreenEvent
    data class Error(val message: UiText) : AgendaDetailScreenEvent
    data class SkippedUploadingImages(val count: Int): AgendaDetailScreenEvent
}