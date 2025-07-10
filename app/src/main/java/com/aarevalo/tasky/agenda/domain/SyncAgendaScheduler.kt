package com.aarevalo.tasky.agenda.domain

import com.aarevalo.tasky.agenda.domain.model.AgendaItem
import kotlin.time.Duration

interface SyncAgendaScheduler {

    suspend fun scheduleSyncAgenda(syncType: SyncType)
    suspend fun cancelAllSyncs()

    sealed interface SyncType {
        data class PeriodicFetch(val interval: Duration): SyncType
        data class CreateAgendaItem(val agendaItem: AgendaItem) : SyncType
        data class UpdateAgendaItem(val agendaItem: AgendaItem, val isGoing: Boolean, val deletedPhotoKeys: List<String>) : SyncType
        data class DeleteAgendaItem(val itemId: String) : SyncType
    }
}