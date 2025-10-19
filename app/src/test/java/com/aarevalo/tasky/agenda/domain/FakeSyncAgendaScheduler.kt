package com.aarevalo.tasky.agenda.domain

/**
 * Fake implementation of SyncAgendaScheduler for testing.
 * Tracks scheduled sync operations without actually scheduling WorkManager jobs.
 */
class FakeSyncAgendaScheduler : SyncAgendaScheduler {
    
    // Track scheduled syncs
    val scheduledSyncs = mutableListOf<SyncAgendaScheduler.SyncType>()
    
    override suspend fun scheduleSyncAgenda(syncType: SyncAgendaScheduler.SyncType) {
        scheduledSyncs.add(syncType)
    }
    
    // Helper method (not in interface)
    fun cancelSyncForItem(agendaItemId: String) {
        scheduledSyncs.removeAll { syncType ->
            when (syncType) {
                is SyncAgendaScheduler.SyncType.CreateAgendaItem -> syncType.agendaItem.id == agendaItemId
                is SyncAgendaScheduler.SyncType.UpdateAgendaItem -> syncType.agendaItem.id == agendaItemId
                is SyncAgendaScheduler.SyncType.DeleteAgendaItem -> syncType.itemId == agendaItemId
                else -> false
            }
        }
    }
    
    override suspend fun cancelAllSyncs() {
        scheduledSyncs.clear()
    }
    
    // Helper methods
    fun reset() {
        scheduledSyncs.clear()
    }
    
    fun hasPendingSync(itemId: String): Boolean {
        return scheduledSyncs.any { syncType ->
            when (syncType) {
                is SyncAgendaScheduler.SyncType.CreateAgendaItem -> syncType.agendaItem.id == itemId
                is SyncAgendaScheduler.SyncType.UpdateAgendaItem -> syncType.agendaItem.id == itemId
                is SyncAgendaScheduler.SyncType.DeleteAgendaItem -> syncType.itemId == itemId
                else -> false
            }
        }
    }
    
    fun getCreateSyncs(): List<SyncAgendaScheduler.SyncType.CreateAgendaItem> {
        return scheduledSyncs.filterIsInstance<SyncAgendaScheduler.SyncType.CreateAgendaItem>()
    }
    
    fun getUpdateSyncs(): List<SyncAgendaScheduler.SyncType.UpdateAgendaItem> {
        return scheduledSyncs.filterIsInstance<SyncAgendaScheduler.SyncType.UpdateAgendaItem>()
    }
    
    fun getDeleteSyncs(): List<SyncAgendaScheduler.SyncType.DeleteAgendaItem> {
        return scheduledSyncs.filterIsInstance<SyncAgendaScheduler.SyncType.DeleteAgendaItem>()
    }
    
    fun getPeriodicFetchSyncs(): List<SyncAgendaScheduler.SyncType.PeriodicFetch> {
        return scheduledSyncs.filterIsInstance<SyncAgendaScheduler.SyncType.PeriodicFetch>()
    }
}

