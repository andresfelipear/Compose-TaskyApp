package com.aarevalo.tasky.agenda.domain

import com.aarevalo.tasky.agenda.domain.model.AgendaItem
import com.aarevalo.tasky.agenda.domain.model.AgendaItemType
import com.aarevalo.tasky.agenda.domain.model.Attendee
import com.aarevalo.tasky.core.domain.util.DataError
import com.aarevalo.tasky.core.domain.util.Result
import com.aarevalo.tasky.core.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZonedDateTime

interface AgendaRepository {
    suspend fun scheduleReminder(agendaItem: AgendaItem)
    suspend fun cancelReminder(agendaItemId: String, itemType: AgendaItemType)
    fun getAllAgendaItems(): Flow<List<AgendaItem>>
    fun getAgendaItemsByDate(date: LocalDate): Flow<List<AgendaItem>>
    suspend fun fetchAgendaItems(): EmptyResult<DataError>
    suspend fun getAgendaItemById(agendaItemId: String): AgendaItem?
    suspend fun createAgendaItem(agendaItem: AgendaItem): EmptyResult<DataError>
    suspend fun updateAgendaItem(agendaItem: AgendaItem, isGoing: Boolean, deletedPhotoKeys: List<String>): EmptyResult<DataError>
    suspend fun deleteAgendaItem(agendaItemId: String, itemType: AgendaItemType): EmptyResult<DataError>
    suspend fun syncPendingAgendaItems()
    suspend fun getAttendee(email: String): Result<Attendee?, DataError.Network>
    suspend fun logout(): EmptyResult<DataError.Network>
}