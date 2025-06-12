package com.aarevalo.tasky.agenda.presentation.agenda_detail

import androidx.compose.foundation.text.input.TextFieldState
import com.aarevalo.tasky.agenda.domain.model.Attendee
import com.aarevalo.tasky.agenda.domain.model.VisitorFilterType
import com.aarevalo.tasky.core.presentation.util.UiText

data class AttendeesState (
    val email: TextFieldState = TextFieldState(),
    val isEmailValid: Boolean = false,
    val isAdding: Boolean = false,
    val isProcessingEmail: Boolean = false,
    val errorMessage: UiText? = null,
    val filterOptions: List<VisitorFilterType> = VisitorFilterType.entries,
    val selectedFilter: VisitorFilterType = VisitorFilterType.ALL,
    // HERE
    val going: List<Attendee> = emptyList(),
    val notGoing: List<Attendee> = emptyList(),
    // HERE
    val isUserGoing: Boolean = true,
)
