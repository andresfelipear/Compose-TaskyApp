package com.aarevalo.tasky.agenda.domain

import com.aarevalo.tasky.agenda.domain.model.AgendaItem
import com.aarevalo.tasky.agenda.domain.model.AgendaItemType
import com.aarevalo.tasky.core.domain.util.DataError
import com.aarevalo.tasky.core.domain.util.EmptyResult
import com.aarevalo.tasky.core.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

/**
 * Fake implementation of AgendaRepository for testing.
 * Stores data in memory and allows control over success/failure scenarios.
 */
class FakeAgendaRepository : AgendaRepository {
    
    private val agendaItems = MutableStateFlow<List<AgendaItem>>(emptyList())
    
    // Control flags
    var shouldReturnError = false
    var errorToReturn: DataError = DataError.Network.UNKNOWN
    
    // Track method calls
    val createAgendaItemCalls = mutableListOf<AgendaItem>()
    val updateAgendaItemCalls = mutableListOf<AgendaItem>()
    val deleteAgendaItemCalls = mutableListOf<Pair<String, AgendaItemType>>()
    val fetchAgendaItemsCalls = mutableListOf<Unit>()
    val syncPendingItemsCalls = mutableListOf<Unit>()
    val scheduleReminderCalls = mutableListOf<AgendaItem>()
    val cancelReminderCalls = mutableListOf<Pair<String, AgendaItemType>>()
    val logoutCalls = mutableListOf<Unit>()
    
    override suspend fun createAgendaItem(agendaItem: AgendaItem): EmptyResult<DataError> {
        createAgendaItemCalls.add(agendaItem)
        
        return if (shouldReturnError) {
            Result.Error(errorToReturn)
        } else {
            agendaItems.value = agendaItems.value + agendaItem
            Result.Success(Unit)
        }
    }
    
    override suspend fun updateAgendaItem(
        agendaItem: AgendaItem,
        isGoing: Boolean,
        deletedPhotoKeys: List<String>
    ): EmptyResult<DataError> {
        updateAgendaItemCalls.add(agendaItem)
        
        return if (shouldReturnError) {
            Result.Error(errorToReturn)
        } else {
            agendaItems.value = agendaItems.value.map { item ->
                if (item.id == agendaItem.id) agendaItem else item
            }
            Result.Success(Unit)
        }
    }
    
    override suspend fun deleteAgendaItem(
        id: String,
        type: AgendaItemType
    ): EmptyResult<DataError> {
        deleteAgendaItemCalls.add(id to type)
        
        return if (shouldReturnError) {
            Result.Error(errorToReturn)
        } else {
            agendaItems.value = agendaItems.value.filter { it.id != id }
            Result.Success(Unit)
        }
    }
    
    override suspend fun fetchAgendaItems(): EmptyResult<DataError> {
        fetchAgendaItemsCalls.add(Unit)
        
        return if (shouldReturnError) {
            Result.Error(errorToReturn)
        } else {
            Result.Success(Unit)
        }
    }
    
    override suspend fun syncPendingAgendaItems() {
        syncPendingItemsCalls.add(Unit)
    }
    
    override fun getAgendaItemsByDate(date: LocalDate): Flow<List<AgendaItem>> {
        return agendaItems.map { items ->
            items.filter { item ->
                item.fromDate == date
            }
        }
    }
    
    override fun getAllAgendaItems(): Flow<List<AgendaItem>> {
        return agendaItems
    }
    
    override suspend fun getAgendaItemById(agendaItemId: String): AgendaItem? {
        return agendaItems.value.find { it.id == agendaItemId }
    }
    
    override suspend fun getAttendee(email: String): Result<com.aarevalo.tasky.agenda.domain.model.Attendee?, DataError.Network> {
        return if (shouldReturnError) {
            Result.Error(errorToReturn as DataError.Network)
        } else {
            Result.Success(null)  // Simplified for tests
        }
    }
    
    override suspend fun scheduleReminder(agendaItem: AgendaItem) {
        scheduleReminderCalls.add(agendaItem)
    }
    
    override suspend fun cancelReminder(agendaItemId: String, itemType: AgendaItemType) {
        cancelReminderCalls.add(agendaItemId to itemType)
    }
    
    override suspend fun logout(): EmptyResult<DataError.Network> {
        logoutCalls.add(Unit)
        
        return if (shouldReturnError) {
            Result.Error(errorToReturn as DataError.Network)
        } else {
            agendaItems.value = emptyList()
            Result.Success(Unit)
        }
    }
    
    // Helper methods for test setup
    fun reset() {
        agendaItems.value = emptyList()
        shouldReturnError = false
        errorToReturn = DataError.Network.UNKNOWN
        createAgendaItemCalls.clear()
        updateAgendaItemCalls.clear()
        deleteAgendaItemCalls.clear()
        fetchAgendaItemsCalls.clear()
        syncPendingItemsCalls.clear()
        scheduleReminderCalls.clear()
        cancelReminderCalls.clear()
        logoutCalls.clear()
    }
    
    fun setItems(items: List<AgendaItem>) {
        agendaItems.value = items
    }
    
    fun addItem(item: AgendaItem) {
        agendaItems.value = agendaItems.value + item
    }
    
    fun simulateNetworkError() {
        shouldReturnError = true
        errorToReturn = DataError.Network.NO_INTERNET
    }
    
    fun simulateUnauthorized() {
        shouldReturnError = true
        errorToReturn = DataError.Network.UNAUTHORIZED
    }
    
    fun getItemCount(): Int = agendaItems.value.size
    
    fun hasItem(id: String): Boolean = agendaItems.value.any { it.id == id }
}

