package com.aarevalo.tasky.agenda.presentation.edit_text

sealed interface EditTextScreenAction {
    data class GoBack(val content: String?) : EditTextScreenAction
}
