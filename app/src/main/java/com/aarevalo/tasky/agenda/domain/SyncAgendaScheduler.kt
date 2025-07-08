package com.aarevalo.tasky.agenda.domain

import com.aarevalo.tasky.agenda.presentation.agenda_detail.AgendaItemDetails
import kotlin.time.Duration

interface SyncAgendaScheduler {

    suspend fun scheduleSyncAgenda(syncType: SyncType)
    suspend fun cancelAllSyncs()

    sealed interface SyncType {
        data class PeriodicFetch(val interval: Duration): SyncType
        data class CreateAgendaItem(val itemId: String) : SyncType
        data class UpdateAgendaItem(val itemId: String) : SyncType
        data class DeleteAgendaItem(val itemId: String, val itemType: AgendaItemDetails) : SyncType
    }
}