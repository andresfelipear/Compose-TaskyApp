package com.aarevalo.tasky.agenda.presentation.agenda

import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import com.aarevalo.tasky.agenda.presentation.agenda.AgendaScreenState.Companion.RANGE_DAYS
import com.aarevalo.tasky.agenda.presentation.agenda_detail.AgendaItemDetails
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
data class AgendaScreenState (
    val selectedDate: LocalDate = LocalDate.now(),
    val initials: String = "",
    val showDatePicker: Boolean = false,
    val datePickerState: DatePickerState = DatePickerState(
        locale = CalendarLocale.getDefault(),
    ),
    val relatedDates: List<LocalDate> = getRelatedDates(LocalDate.now()),
    val fromTime: LocalTime = LocalTime.now(),
    val fromDate: LocalDate = LocalDate.now(),
    val description: String = "Event description",
    val title: String = "New Event",
    val details: AgendaItemDetails = AgendaItemDetails.Event()
){
    companion object{
        const val RANGE_DAYS: Long = 15
    }
}

private fun getRelatedDates(date: LocalDate): List<LocalDate>{
    val start = date.minusDays(RANGE_DAYS)
    val end   = date.plusDays(RANGE_DAYS)

    return generateSequence(start) {
        it.plusDays(1)
    }.takeWhile{ !it.isAfter(end)}
        .toList()
}