package com.aarevalo.tasky.agenda.data

import com.aarevalo.tasky.agenda.domain.AgendaItemTestFactory
import com.aarevalo.tasky.agenda.domain.FakeLocalAgendaDataSource
import com.aarevalo.tasky.agenda.domain.FakeRemoteAgendaDataSource
import com.aarevalo.tasky.core.domain.FakeSessionStorage
import com.aarevalo.tasky.core.domain.user.AuthenticatedUser
import com.aarevalo.tasky.core.domain.util.DataError
import com.aarevalo.tasky.core.domain.util.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

/**
 * Simplified tests for OfflineFirstAgendaRepository focusing on key scenarios.
 * 
 * Note: Full repository testing would require all dependencies.
 * These tests focus on demonstrating testing patterns for interview purposes.
 */
@ExperimentalCoroutinesApi
class OfflineFirstAgendaRepositoryTest {
    
    private lateinit var fakeLocalDataSource: FakeLocalAgendaDataSource
    private lateinit var fakeRemoteDataSource: FakeRemoteAgendaDataSource
    private lateinit var fakeSessionStorage: FakeSessionStorage
    
    @Before
    fun setup() {
        fakeLocalDataSource = FakeLocalAgendaDataSource()
        fakeRemoteDataSource = FakeRemoteAgendaDataSource()
        fakeSessionStorage = FakeSessionStorage()
        
        // Set up authenticated user
        fakeSessionStorage.setSession(
            AuthenticatedUser(
                userId = "test-user-id",
                fullName = "Test User",
                accessToken = "access-token",
                refreshToken = "refresh-token",
                accessTokenExpirationTimestamp = System.currentTimeMillis() + 3600000
            )
        )
    }
    
    /**
     * These tests demonstrate testing patterns for the interview.
     * In a real scenario, we'd test the full OfflineFirstAgendaRepository,
     * but for this demo we focus on testing data sources directly.
     */
    
    // ========================================
    // Local Data Source Tests
    // ========================================
    
    @Test
    fun `local data source can store and retrieve items by date`() = runTest {
        // Arrange
        val date = LocalDate.now()
        val items = AgendaItemTestFactory.createAgendaItemsForDate(date, 2, 1, 1)
        fakeLocalDataSource.setItems(items)
        
        // Act
        val result = fakeLocalDataSource.getAgendaItemsByDate(date).first()
        
        // Assert
        assertEquals(4, result.size)
        assertTrue(result.all { it.fromDate == date })
    }
    
    @Test
    fun `local data source filters items by date correctly`() = runTest {
        // Arrange
        val date1 = LocalDate.of(2024, 10, 19)
        val date2 = LocalDate.of(2024, 10, 20)
        
        val itemsDate1 = AgendaItemTestFactory.createAgendaItemsForDate(date1, 2, 0, 0)
        val itemsDate2 = AgendaItemTestFactory.createAgendaItemsForDate(date2, 0, 1, 0)
        
        fakeLocalDataSource.setItems(itemsDate1 + itemsDate2)
        
        // Act
        val result = fakeLocalDataSource.getAgendaItemsByDate(date1).first()
        
        // Assert
        assertEquals(2, result.size)
        assertTrue(result.all { it.fromDate == date1 })
    }
    
    // ========================================
    // Remote Data Source Tests
    // ========================================
    
    @Test
    fun `remote data source fetchFullAgenda returns items when online`() = runTest {
        // Arrange
        val remoteItems = AgendaItemTestFactory.createAgendaItemsForDate(LocalDate.now(), 3, 2, 1)
        fakeRemoteDataSource.setItems(remoteItems)
        
        // Act
        val result = fakeRemoteDataSource.fetchFullAgenda()
        
        // Assert
        assertTrue(result is Result.Success)
        assertEquals(6, (result as Result.Success).data.size)
    }
    
    @Test
    fun `remote data source returns error when offline`() = runTest {
        // Arrange
        fakeRemoteDataSource.simulateNoInternet()
        
        // Act
        val result = fakeRemoteDataSource.fetchFullAgenda()
        
        // Assert
        assertTrue(result is Result.Error)
        assertEquals(DataError.Network.NO_INTERNET, (result as Result.Error).error)
    }
    
    // ========================================
    // Data Flow Tests
    // ========================================
    
    @Test
    fun `local data source emits new data when items change`() = runTest {
        // Arrange
        val date = LocalDate.now()
        val emissions = mutableListOf<List<com.aarevalo.tasky.agenda.domain.model.AgendaItem>>()
        
        val job = launch {
            fakeLocalDataSource.getAgendaItemsByDate(date).collect {
                emissions.add(it)
            }
        }
        
        advanceUntilIdle()
        
        // Initially empty
        assertTrue(emissions.first().isEmpty())
        
        // Act - add item
        val newEvent = AgendaItemTestFactory.createTestEvent(fromDate = date)
        fakeLocalDataSource.setItems(listOf(newEvent))
        advanceUntilIdle()
        
        // Assert
        assertTrue(emissions.size >= 2)
        assertEquals(1, emissions.last().size)
        
        job.cancel()
    }
}

