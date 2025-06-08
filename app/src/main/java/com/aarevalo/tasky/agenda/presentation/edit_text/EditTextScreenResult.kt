package com.aarevalo.tasky.agenda.presentation.edit_text

import android.os.Parcelable
import com.aarevalo.tasky.agenda.domain.model.EditTextFieldType
import kotlinx.parcelize.Parcelize

@Parcelize
data class EditTextScreenResult(
    val type: EditTextFieldType,
    val value: String
): Parcelable
