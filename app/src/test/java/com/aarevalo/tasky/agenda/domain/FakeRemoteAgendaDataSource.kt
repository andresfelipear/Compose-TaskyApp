package com.aarevalo.tasky.agenda.domain

import com.aarevalo.tasky.agenda.domain.model.AgendaItem
import com.aarevalo.tasky.agenda.domain.model.AgendaItemType
import com.aarevalo.tasky.agenda.domain.model.Attendee
import com.aarevalo.tasky.core.domain.util.DataError
import com.aarevalo.tasky.core.domain.util.Result
import java.time.LocalDate
import java.time.ZonedDateTime

/**
 * Fake implementation of RemoteAgendaDataSource for testing.
 * Simulates API operations without actual network calls.
 */
class FakeRemoteAgendaDataSource : RemoteAgendaDataSource {
    
    private val remoteItems = mutableListOf<AgendaItem>()
    
    // Control flags
    var shouldReturnError = false
    var errorToReturn: DataError.Network = DataError.Network.UNKNOWN
    
    // Track method calls
    val fetchFullAgendaCalls = mutableListOf<Unit>()
    val createAgendaItemCalls = mutableListOf<AgendaItem>()
    val updateAgendaItemCalls = mutableListOf<AgendaItem>()
    val deleteAgendaItemCalls = mutableListOf<Pair<String, AgendaItemType>>()
    val addAttendeeCalls = mutableListOf<Pair<String, String>>()
    val deleteAttendeeCalls = mutableListOf<String>()
    
    override suspend fun fetchFullAgenda(): Result<List<AgendaItem>, DataError.Network> {
        fetchFullAgendaCalls.add(Unit)
        
        return if (shouldReturnError) {
            Result.Error(errorToReturn)
        } else {
            Result.Success(remoteItems.toList())
        }
    }
    
    override suspend fun fetchAgendaItems(date: LocalDate): Result<List<AgendaItem>, DataError.Network> {
        return if (shouldReturnError) {
            Result.Error(errorToReturn)
        } else {
            val itemsForDate = remoteItems.filter { it.fromDate == date }
            Result.Success(itemsForDate)
        }
    }
    
    override suspend fun fetchAgendaItem(
        agendaItemId: String,
        type: AgendaItemType
    ): Result<AgendaItem?, DataError.Network> {
        val item = remoteItems.find { it.id == agendaItemId && it.type == type }
        return if (shouldReturnError) {
            Result.Error(errorToReturn)
        } else {
            Result.Success(item)
        }
    }
    
    override suspend fun createAgendaItem(agendaItem: AgendaItem): Result<AgendaItem?, DataError.Network> {
        createAgendaItemCalls.add(agendaItem)
        
        return if (shouldReturnError) {
            Result.Error(errorToReturn)
        } else {
            remoteItems.add(agendaItem)
            Result.Success(agendaItem)
        }
    }
    
    override suspend fun updateAgendaItem(
        agendaItem: AgendaItem,
        deletedPhotoKeys: List<String>,
        isGoing: Boolean
    ): Result<AgendaItem?, DataError.Network> {
        updateAgendaItemCalls.add(agendaItem)
        
        return if (shouldReturnError) {
            Result.Error(errorToReturn)
        } else {
            val index = remoteItems.indexOfFirst { it.id == agendaItem.id }
            if (index >= 0) {
                remoteItems[index] = agendaItem
            }
            Result.Success(agendaItem)
        }
    }
    
    override suspend fun fetchAttendee(email: String): Result<Attendee?, DataError.Network> {
        return if (shouldReturnError) {
            Result.Error(errorToReturn)
        } else {
            Result.Success(
                Attendee(
                    email = email,
                    fullName = "Test Attendee",
                    userId = "attendee-id",
                    eventId = "event-id",
                    isGoing = true,
                    remindAt = ZonedDateTime.now()
                )
            )
        }
    }
    
    override suspend fun deleteAttendee(eventId: String): Result<Unit, DataError.Network> {
        deleteAttendeeCalls.add(eventId)
        
        return if (shouldReturnError) {
            Result.Error(errorToReturn)
        } else {
            Result.Success(Unit)
        }
    }
    
    override suspend fun syncAgenda(
        deletedEventIds: List<String>,
        deletedTaskIds: List<String>,
        deletedReminderIds: List<String>
    ): Result<Unit, DataError.Network> {
        return if (shouldReturnError) {
            Result.Error(errorToReturn)
        } else {
            Result.Success(Unit)
        }
    }
    
    override suspend fun deleteAgendaItem(
        agendaItemId: String,
        itemType: AgendaItemType
    ): Result<Unit, DataError.Network> {
        deleteAgendaItemCalls.add(agendaItemId to itemType)
        
        return if (shouldReturnError) {
            Result.Error(errorToReturn)
        } else {
            remoteItems.removeAll { it.id == agendaItemId }
            Result.Success(Unit)
        }
    }
    
    override suspend fun logout(refreshToken: String): Result<Unit, DataError.Network> {
        return if (shouldReturnError) {
            Result.Error(errorToReturn)
        } else {
            Result.Success(Unit)
        }
    }
    
    // Helper methods
    fun reset() {
        remoteItems.clear()
        shouldReturnError = false
        errorToReturn = DataError.Network.UNKNOWN
        fetchFullAgendaCalls.clear()
        createAgendaItemCalls.clear()
        updateAgendaItemCalls.clear()
        deleteAgendaItemCalls.clear()
        addAttendeeCalls.clear()
        deleteAttendeeCalls.clear()
    }
    
    fun setItems(items: List<AgendaItem>) {
        remoteItems.clear()
        remoteItems.addAll(items)
    }
    
    fun addItem(item: AgendaItem) {
        remoteItems.add(item)
    }
    
    fun simulateNoInternet() {
        shouldReturnError = true
        errorToReturn = DataError.Network.NO_INTERNET
    }
    
    fun simulateServerError() {
        shouldReturnError = true
        errorToReturn = DataError.Network.SERVER_ERROR
    }
    
    fun simulateUnauthorized() {
        shouldReturnError = true
        errorToReturn = DataError.Network.UNAUTHORIZED
    }
    
    fun getItemCount(): Int = remoteItems.size
}

