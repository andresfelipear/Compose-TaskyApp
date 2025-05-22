package com.aarevalo.tasky.agenda.presentation.agenda

import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
data class AgendaScreenState (
    val selectedDate: LocalDate = LocalDate.now(),
    val initials: String = "",
    val showDatePicker: Boolean = false,
    val datePickerState: DatePickerState = DatePickerState(
        locale = CalendarLocale.getDefault(),
    ),
    val relatedDates: List<LocalDate> = generateSequence(LocalDate.now().minusDays(RANGE_DAYS)) {
        it.plusDays(1)
    }.takeWhile({ !it.isAfter(LocalDate.now().plusDays(15))})
        .toList()
){
    companion object{
        const val RANGE_DAYS: Long = 15
    }
}