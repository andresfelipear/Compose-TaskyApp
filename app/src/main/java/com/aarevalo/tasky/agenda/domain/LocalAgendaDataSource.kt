package com.aarevalo.tasky.agenda.domain

import com.aarevalo.tasky.agenda.domain.model.AgendaItem
import com.aarevalo.tasky.core.domain.util.DataError
import com.aarevalo.tasky.core.domain.util.Result
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface LocalAgendaDataSource {
    fun getAgendaItems(): Flow<List<AgendaItem>>
    fun getAgendaItemsByDate(date: LocalDate): Flow<List<AgendaItem>>
    suspend fun getAgendaItemById(agendaItemId: String): AgendaItem?
    suspend fun upsertAgendaItem(agendaItem: AgendaItem): Result<String, DataError.Local>
    suspend fun upsertAgendaItems(agendaItems: List<AgendaItem>): Result<List<String>, DataError.Local>
    suspend fun deleteAgendaItem(agendaItemId: String)
    suspend fun deleteAllAgendaItems()
}