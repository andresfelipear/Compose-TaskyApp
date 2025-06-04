package com.aarevalo.tasky.agenda.presentation.edit_text

import androidx.compose.foundation.text.input.TextFieldState
import com.aarevalo.tasky.agenda.domain.model.EditTextFieldType

data class EditTextScreenState(
    val textFieldContent: TextFieldState = TextFieldState(),
    val type: EditTextFieldType = EditTextFieldType.TITLE,
)
