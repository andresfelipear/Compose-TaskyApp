package com.aarevalo.tasky.agenda.presentation.agenda

import com.aarevalo.tasky.agenda.domain.model.AgendaItem
import com.aarevalo.tasky.agenda.presentation.agenda.AgendaScreenState.Companion.RANGE_DAYS
import java.time.LocalDate
import java.time.LocalTime

data class AgendaScreenState (
    val selectedDate: LocalDate = LocalDate.now(),
    val initials: String = "",
    val showDatePicker: Boolean = false,
    val relatedDates: List<LocalDate> = getRelatedDates(LocalDate.now()),
    val agendaItems: List<AgendaItem> = emptyList(),
    val timeNeedled: LocalTime = LocalTime.now()
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
