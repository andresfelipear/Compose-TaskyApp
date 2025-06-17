package com.aarevalo.tasky.agenda.domain

import com.aarevalo.tasky.agenda.domain.model.AgendaItem
import com.aarevalo.tasky.agenda.domain.model.Attendee
import com.aarevalo.tasky.core.domain.util.DataError
import com.aarevalo.tasky.core.domain.util.Result
import com.aarevalo.tasky.core.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow

interface AgendaRepository {
    fun getAgendaItems(): Flow<List<AgendaItem>>
    suspend fun fetchAgendaItems(): EmptyResult<DataError>
    suspend fun getAgendaItemById(agendaItemId: String): AgendaItem?
    suspend fun upsertAgendaItem(agendaItem: AgendaItem): EmptyResult<DataError>
    suspend fun deleteAgendaItem(agendaItemId: String)
    suspend fun syncPendingAgendaItems()
    suspend fun deleteAllAgendaItems()
    suspend fun getAttendee(email: String): Result<Attendee, DataError.Network>
    suspend fun logout(): EmptyResult<DataError.Network>
}