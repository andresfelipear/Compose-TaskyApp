package com.aarevalo.tasky.agenda.domain

import com.aarevalo.tasky.agenda.domain.model.AgendaItem
import com.aarevalo.tasky.agenda.domain.model.AgendaItemType
import com.aarevalo.tasky.agenda.domain.model.Attendee
import com.aarevalo.tasky.core.domain.util.DataError
import com.aarevalo.tasky.core.domain.util.EmptyResult
import com.aarevalo.tasky.core.domain.util.Result
import java.time.LocalDate

interface RemoteAgendaDataSource {
    suspend fun fetchFullAgenda(): Result<List<AgendaItem>, DataError.Network>
    suspend fun fetchAgendaItems(date: LocalDate): Result<List<AgendaItem>, DataError.Network>
    suspend fun fetchAgendaItem(agendaItemId: String, type: AgendaItemType): Result<AgendaItem?, DataError.Network>
    suspend fun createAgendaItem(agendaItem: AgendaItem): Result<AgendaItem?,DataError.Network>
    suspend fun updateAgendaItem(agendaItem: AgendaItem, deletedPhotoKeys: List<String>, isGoing: Boolean): Result<AgendaItem?, DataError.Network>
    suspend fun fetchAttendee(email: String): Result<Attendee?, DataError.Network>
    suspend fun deleteAttendee(eventId: String): EmptyResult<DataError.Network>
    suspend fun syncAgenda(deletedEventIds: List<String>, deletedTaskIds: List<String>, deletedReminderIds: List<String>): EmptyResult<DataError.Network>
    suspend fun deleteAgendaItem(agendaItemId: String): EmptyResult<DataError.Network>
    suspend fun logout(): EmptyResult<DataError.Network>
}