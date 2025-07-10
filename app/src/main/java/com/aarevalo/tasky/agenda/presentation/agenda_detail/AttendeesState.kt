package com.aarevalo.tasky.agenda.presentation.agenda_detail

import com.aarevalo.tasky.agenda.domain.model.Attendee
import com.aarevalo.tasky.agenda.domain.model.VisitorFilterType
import com.aarevalo.tasky.core.presentation.util.UiText

data class AttendeesState (
    val email: String = "",
    val isEmailValid: Boolean = false,
    val isAdding: Boolean = false,
    val errorMessage: UiText? = null,
    val filterOptions: List<VisitorFilterType> = VisitorFilterType.entries,
    val selectedFilter: VisitorFilterType = VisitorFilterType.ALL,
    val going: List<Attendee> = emptyList(),
    val notGoing: List<Attendee> = emptyList(),
    val isUserGoing: Boolean = true,
)
