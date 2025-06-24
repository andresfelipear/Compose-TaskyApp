package com.aarevalo.tasky.agenda.domain

import com.aarevalo.tasky.agenda.domain.model.AgendaItem
import com.aarevalo.tasky.agenda.domain.model.AgendaItemType
import com.aarevalo.tasky.core.domain.util.DataError
import com.aarevalo.tasky.core.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface LocalAgendaDataSource {
    fun getAgendaItems(): Flow<List<AgendaItem>>
    fun getAgendaItemsByDate(date: LocalDate): Flow<List<AgendaItem>>
    suspend fun getAgendaItemById(agendaItemId: String, agendaItemType: AgendaItemType): AgendaItem?
    suspend fun upsertAgendaItem(agendaItem: AgendaItem): EmptyResult<DataError.Local>
    suspend fun deleteAgendaItem(agendaItemId: String, agendaItemType: AgendaItemType)
    suspend fun deleteAllAgendaItems()
}