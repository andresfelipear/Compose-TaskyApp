package com.aarevalo.tasky.agenda.domain

import com.aarevalo.tasky.agenda.domain.model.AgendaItem
import com.aarevalo.tasky.agenda.domain.model.AgendaItemType
import com.aarevalo.tasky.core.domain.util.DataError
import com.aarevalo.tasky.core.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

/**
 * Fake implementation of LocalAgendaDataSource for testing.
 * Simulates local database operations in memory.
 */
class FakeLocalAgendaDataSource : LocalAgendaDataSource {
    
    private val agendaItems = MutableStateFlow<List<AgendaItem>>(emptyList())
    
    // Track method calls
    val insertCalls = mutableListOf<AgendaItem>()
    val updateCalls = mutableListOf<AgendaItem>()
    val deleteCalls = mutableListOf<Pair<String, AgendaItemType>>()
    val upsertCalls = mutableListOf<List<AgendaItem>>()
    val clearAllCalls = mutableListOf<Unit>()
    
    // Control flags
    var shouldThrowException = false
    
    override suspend fun upsertAgendaItem(agendaItem: AgendaItem): Result<String, DataError.Local> {
        insertCalls.add(agendaItem)
        
        if (shouldThrowException) {
            return Result.Error(DataError.Local.UNKNOWN)
        }
        
        // Upsert logic: update if exists, insert if new
        val existingIndex = agendaItems.value.indexOfFirst { it.id == agendaItem.id }
        agendaItems.value = if (existingIndex >= 0) {
            agendaItems.value.toMutableList().apply {
                set(existingIndex, agendaItem)
            }
        } else {
            agendaItems.value + agendaItem
        }
        
        return Result.Success(agendaItem.id)
    }
    
    override suspend fun upsertAgendaItems(items: List<AgendaItem>): Result<List<String>, DataError.Local> {
        upsertCalls.add(items)
        
        if (shouldThrowException) {
            return Result.Error(DataError.Local.UNKNOWN)
        }
        
        // Merge logic: update existing, add new
        val currentItems = agendaItems.value.toMutableList()
        items.forEach { newItem ->
            val existingIndex = currentItems.indexOfFirst { it.id == newItem.id }
            if (existingIndex >= 0) {
                currentItems[existingIndex] = newItem
            } else {
                currentItems.add(newItem)
            }
        }
        agendaItems.value = currentItems
        
        return Result.Success(items.map { it.id })
    }
    
    override suspend fun deleteAgendaItem(id: String, type: AgendaItemType) {
        deleteCalls.add(id to type)
        
        if (!shouldThrowException) {
            agendaItems.value = agendaItems.value.filter { it.id != id }
        }
    }
    
    override fun getAgendaItems(): Flow<List<AgendaItem>> {
        return agendaItems
    }
    
    override fun getAgendaItemsByDate(date: LocalDate): Flow<List<AgendaItem>> {
        return agendaItems.map { items ->
            items.filter { it.fromDate == date }
        }
    }
    
    override suspend fun getAgendaItemById(
        id: String,
        type: AgendaItemType
    ): AgendaItem? {
        return agendaItems.value.find { it.id == id && it.type == type }
    }
    
    override suspend fun deleteAllAgendaItems() {
        clearAllCalls.add(Unit)
        agendaItems.value = emptyList()
    }
    
    // Helper methods
    fun reset() {
        agendaItems.value = emptyList()
        shouldThrowException = false
        insertCalls.clear()
        updateCalls.clear()
        deleteCalls.clear()
        upsertCalls.clear()
        clearAllCalls.clear()
    }
    
    fun setItems(items: List<AgendaItem>) {
        agendaItems.value = items
    }
    
    fun getItemCount(): Int = agendaItems.value.size
    
    fun hasItem(id: String): Boolean = agendaItems.value.any { it.id == id }
}

