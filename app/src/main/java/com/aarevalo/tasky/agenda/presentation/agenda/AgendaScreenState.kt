package com.aarevalo.tasky.agenda.presentation.agenda

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
data class AgendaScreenState (
    val date: LocalDate = LocalDate.now(),
    val initials: String = "",
    val showDatePicker: Boolean = false,
    val datePickerState: DatePickerState = DatePickerState(
        locale = CalendarLocale.getDefault(),
    )
)
