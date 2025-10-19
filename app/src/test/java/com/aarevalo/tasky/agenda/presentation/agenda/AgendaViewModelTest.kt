package com.aarevalo.tasky.agenda.presentation.agenda

import androidx.lifecycle.SavedStateHandle
import com.aarevalo.tasky.MainDispatcherRule
import com.aarevalo.tasky.agenda.domain.AgendaItemTestFactory
import com.aarevalo.tasky.agenda.domain.FakeAgendaRepository
import com.aarevalo.tasky.agenda.domain.FakeSyncAgendaScheduler
import com.aarevalo.tasky.agenda.domain.model.AgendaItemType
import com.aarevalo.tasky.core.domain.FakeSessionStorage
import com.aarevalo.tasky.core.domain.user.AuthenticatedUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

/**
 * Comprehensive unit tests for AgendaViewModel.
 * Tests cover:
 * - Date selection and filtering
 * - Agenda item operations (delete, update task status)
 * - Logout functionality
 * - Flow-based data loading
 * - Edge cases
 */
@ExperimentalCoroutinesApi
class AgendaViewModelTest {
    
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    
    private lateinit var viewModel: AgendaViewModel
    private lateinit var fakeAgendaRepository: FakeAgendaRepository
    private lateinit var fakeSyncScheduler: FakeSyncAgendaScheduler
    private lateinit var fakeSessionStorage: FakeSessionStorage
    private lateinit var savedStateHandle: SavedStateHandle
    
    @Before
    fun setup() {
        fakeAgendaRepository = FakeAgendaRepository()
        fakeSyncScheduler = FakeSyncAgendaScheduler()
        fakeSessionStorage = FakeSessionStorage()
        savedStateHandle = SavedStateHandle()
        
        // Set up authenticated user
        fakeSessionStorage.setSession(
            AuthenticatedUser(
                userId = "test-user-id",
                fullName = "John Doe",
                accessToken = "access-token",
                refreshToken = "refresh-token",
                accessTokenExpirationTimestamp = System.currentTimeMillis() + 3600000
            )
        )
        
        viewModel = AgendaViewModel(
            sessionStorage = fakeSessionStorage,
            agendaRepository = fakeAgendaRepository,
            savedStateHandle = savedStateHandle,
            syncAgendaScheduler = fakeSyncScheduler
        )
    }
    
    // ========================================
    // Initial State Tests
    // ========================================
    
    @Test
    fun `initial state has current date selected`() = runTest {
        // Assert
        val state = viewModel.state.value
        assertEquals(LocalDate.now(), state.selectedDate)
        assertTrue(state.agendaItems.isEmpty())
        assertFalse(state.showDatePicker)
    }
    
    @Test
    fun `initial state loads user initials from session`() = runTest {
        // Wait for initialization
        advanceUntilIdle()
        
        // Assert
        val state = viewModel.state.value
        assertEquals("JD", state.initials)  // John Doe -> JD
    }
    
    @Test
    fun `when user not logged in, emits go to login event`() = runTest {
        // Arrange - clear session
        fakeSessionStorage.clearSession()
        val events = mutableListOf<AgendaScreenEvent>()
        
        // Recreate ViewModel with no session
        viewModel = AgendaViewModel(
            sessionStorage = fakeSessionStorage,
            agendaRepository = fakeAgendaRepository,
            savedStateHandle = savedStateHandle,
            syncAgendaScheduler = fakeSyncScheduler
        )
        
        val job = launch {
            viewModel.event.collect { events.add(it) }
        }
        
        // Act
        advanceUntilIdle()
        
        // Assert
        assertTrue(events.any { it is AgendaScreenEvent.GoingBackToLoginScreen })
        
        job.cancel()
    }
    
    // ========================================
    // Date Selection Tests
    // ========================================
    
    @Test
    fun `when date changed, state is updated with new date`() = runTest {
        // Arrange
        val newDate = LocalDate.of(2024, 10, 19)
        
        // Act
        viewModel.onAction(AgendaScreenAction.OnDateChanged(newDate))
        advanceUntilIdle()
        
        // Assert
        assertEquals(newDate, viewModel.state.value.selectedDate)
    }
    
    @Test
    fun `when date changed, related dates are calculated`() = runTest {
        // Arrange
        val newDate = LocalDate.of(2024, 10, 19)
        
        // Act
        viewModel.onAction(AgendaScreenAction.OnDateChanged(newDate))
        advanceUntilIdle()
        
        // Assert
        val state = viewModel.state.value
        assertTrue(state.relatedDates.contains(newDate))
        // Should have 31 days (15 before + selected + 15 after)
        assertEquals(31, state.relatedDates.size)
    }
    
    @Test
    fun `when date changed, loads agenda items for that date`() = runTest {
        // Arrange
        val targetDate = LocalDate.of(2024, 10, 19)
        val itemsForDate = AgendaItemTestFactory.createAgendaItemsForDate(
            date = targetDate,
            eventCount = 2,
            taskCount = 1,
            reminderCount = 1
        )
        fakeAgendaRepository.setItems(itemsForDate)
        
        // Act
        viewModel.onAction(AgendaScreenAction.OnDateChanged(targetDate))
        advanceUntilIdle()
        
        // Assert
        val state = viewModel.state.value
        assertEquals(4, state.agendaItems.size)  // 2 events + 1 task + 1 reminder
    }
    
    @Test
    fun `when date picker visibility toggled, state reflects change`() = runTest {
        // Arrange
        val initialVisibility = viewModel.state.value.showDatePicker
        
        // Act
        viewModel.onAction(AgendaScreenAction.OnChangeDatePickerVisibility)
        advanceUntilIdle()
        
        // Assert
        assertEquals(!initialVisibility, viewModel.state.value.showDatePicker)
    }
    
    @Test
    fun `when date selected from calendar, date picker closes and date updates`() = runTest {
        // Arrange
        val newDate = LocalDate.of(2024, 12, 25)
        viewModel.onAction(AgendaScreenAction.OnChangeDatePickerVisibility)  // Open picker
        advanceUntilIdle()
        
        // Act
        viewModel.onAction(AgendaScreenAction.OnDateSelectedCalendar(newDate))
        advanceUntilIdle()
        
        // Assert
        val state = viewModel.state.value
        assertEquals(newDate, state.selectedDate)
        assertFalse(state.showDatePicker)  // Should close after selection
    }
    
    // ========================================
    // Agenda Item Operations Tests
    // ========================================
    
    @Test
    fun `when task status changed, repository is called to update`() = runTest {
        // Arrange
        val task = AgendaItemTestFactory.createTestTask(
            id = "task-1",
            isDone = false
        )
        fakeAgendaRepository.addItem(task)
        viewModel.onAction(AgendaScreenAction.OnDateChanged(task.fromDate))
        advanceUntilIdle()
        
        // Act
        viewModel.onAction(AgendaScreenAction.OnChangeTaskStatus(task.id))
        advanceUntilIdle()
        
        // Assert
        assertEquals(1, fakeAgendaRepository.updateAgendaItemCalls.size)
        val updatedTask = fakeAgendaRepository.updateAgendaItemCalls.first()
        val taskDetails = updatedTask.details as com.aarevalo.tasky.agenda.presentation.agenda_detail.AgendaItemDetails.Task
        assertTrue(taskDetails.isDone)  // Should be toggled to true
    }
    
    @Test
    fun `when delete agenda item confirmed, shows confirmation dialog`() = runTest {
        // Arrange
        val event = AgendaItemTestFactory.createTestEvent(id = "event-1")
        fakeAgendaRepository.addItem(event)
        
        // Act
        viewModel.onAction(
            AgendaScreenAction.OnConfirmDeleteAgendaItem(
                agendaItemId = event.id,
                type = com.aarevalo.tasky.agenda.presentation.agenda_detail.AgendaItemDetails.Event()
            )
        )
        advanceUntilIdle()
        
        // Assert
        val state = viewModel.state.value
        assertTrue(state.showDeleteConfirmationDialog)
        assertEquals(event.id, state.agendaItemIdToDelete)
        assertEquals("Event", state.agendaItemTypeToDelete)
    }
    
    @Test
    fun `when delete agenda item executed, item is deleted from repository`() = runTest {
        // Arrange
        val event = AgendaItemTestFactory.createTestEvent(id = "event-1")
        fakeAgendaRepository.addItem(event)
        viewModel.onAction(AgendaScreenAction.OnDateChanged(event.fromDate))
        advanceUntilIdle()
        
        // Confirm delete
        viewModel.onAction(
            AgendaScreenAction.OnConfirmDeleteAgendaItem(
                agendaItemId = event.id,
                type = com.aarevalo.tasky.agenda.presentation.agenda_detail.AgendaItemDetails.Event()
            )
        )
        advanceUntilIdle()
        
        // Act
        viewModel.onAction(AgendaScreenAction.OnDeleteAgendaItem)
        advanceUntilIdle()
        
        // Assert
        assertEquals(1, fakeAgendaRepository.deleteAgendaItemCalls.size)
        val (deletedId, deletedType) = fakeAgendaRepository.deleteAgendaItemCalls.first()
        assertEquals(event.id, deletedId)
        assertEquals(AgendaItemType.EVENT, deletedType)
    }
    
    @Test
    fun `when delete succeeds, success event is emitted and dialog closes`() = runTest {
        // Arrange
        val events = mutableListOf<AgendaScreenEvent>()
        val job = launch {
            viewModel.event.collect { events.add(it) }
        }
        
        val event = AgendaItemTestFactory.createTestEvent(id = "event-1")
        fakeAgendaRepository.addItem(event)
        viewModel.onAction(AgendaScreenAction.OnDateChanged(event.fromDate))
        advanceUntilIdle()
        
        viewModel.onAction(
            AgendaScreenAction.OnConfirmDeleteAgendaItem(
                agendaItemId = event.id,
                type = com.aarevalo.tasky.agenda.presentation.agenda_detail.AgendaItemDetails.Event()
            )
        )
        advanceUntilIdle()
        
        // Act
        viewModel.onAction(AgendaScreenAction.OnDeleteAgendaItem)
        advanceUntilIdle()
        
        // Assert
        assertTrue(events.any { it is AgendaScreenEvent.SuccessDeleteAgendaItem })
        assertFalse(viewModel.state.value.showDeleteConfirmationDialog)
        assertFalse(viewModel.state.value.isDeletingItem)
        
        job.cancel()
    }
    
    @Test
    fun `when delete fails, error event is emitted`() = runTest {
        // Arrange
        val events = mutableListOf<AgendaScreenEvent>()
        val job = launch {
            viewModel.event.collect { events.add(it) }
        }
        
        fakeAgendaRepository.simulateNetworkError()
        val event = AgendaItemTestFactory.createTestEvent(id = "event-1")
        fakeAgendaRepository.addItem(event)
        viewModel.onAction(AgendaScreenAction.OnDateChanged(event.fromDate))
        advanceUntilIdle()
        
        viewModel.onAction(
            AgendaScreenAction.OnConfirmDeleteAgendaItem(
                agendaItemId = event.id,
                type = com.aarevalo.tasky.agenda.presentation.agenda_detail.AgendaItemDetails.Event()
            )
        )
        advanceUntilIdle()
        
        // Act
        viewModel.onAction(AgendaScreenAction.OnDeleteAgendaItem)
        advanceUntilIdle()
        
        // Assert
        assertTrue(events.any { it is AgendaScreenEvent.Error })
        
        job.cancel()
    }
    
    @Test
    fun `when delete dialog dismissed, dialog closes and state resets`() = runTest {
        // Arrange
        viewModel.onAction(
            AgendaScreenAction.OnConfirmDeleteAgendaItem(
                agendaItemId = "test-id",
                type = com.aarevalo.tasky.agenda.presentation.agenda_detail.AgendaItemDetails.Event()
            )
        )
        advanceUntilIdle()
        
        // Act
        viewModel.onAction(AgendaScreenAction.OnChangeDeleteDialogVisibility)
        advanceUntilIdle()
        
        // Assert
        assertFalse(viewModel.state.value.showDeleteConfirmationDialog)
    }
    
    // ========================================
    // Logout Tests
    // ========================================
    
    @Test
    fun `when logout succeeds, success logout event is emitted`() = runTest {
        // Arrange
        val events = mutableListOf<AgendaScreenEvent>()
        val job = launch {
            viewModel.event.collect { events.add(it) }
        }
        
        // Act
        viewModel.onAction(AgendaScreenAction.OnLogout)
        advanceUntilIdle()
        
        // Assert
        assertTrue(events.any { it is AgendaScreenEvent.SuccessLogout })
        assertEquals(1, fakeAgendaRepository.logoutCalls.size)
        
        job.cancel()
    }
    
    @Test
    fun `when logout fails, error event is emitted`() = runTest {
        // Arrange
        val events = mutableListOf<AgendaScreenEvent>()
        val job = launch {
            viewModel.event.collect { events.add(it) }
        }
        
        fakeAgendaRepository.simulateNetworkError()
        
        // Act
        viewModel.onAction(AgendaScreenAction.OnLogout)
        advanceUntilIdle()
        
        // Assert
        assertTrue(events.any { it is AgendaScreenEvent.Error })
        
        job.cancel()
    }
    
    // ========================================
    // Data Loading Tests
    // ========================================
    
    @Test
    fun `when initialized, fetches and syncs pending items`() = runTest {
        // Wait for initialization
        advanceUntilIdle()
        
        // Assert
        assertEquals(1, fakeAgendaRepository.syncPendingItemsCalls.size)
        assertEquals(1, fakeAgendaRepository.fetchAgendaItemsCalls.size)
    }
    
    @Test
    fun `when initialized, schedules periodic fetch`() = runTest {
        // Wait for initialization
        advanceUntilIdle()
        
        // Assert
        assertTrue(fakeSyncScheduler.scheduledSyncs.isNotEmpty())
        val periodicFetches = fakeSyncScheduler.getPeriodicFetchSyncs()
        assertTrue(periodicFetches.isNotEmpty())
    }
    
    @Test
    fun `when items exist for selected date, they appear in state`() = runTest {
        // Arrange
        val date = LocalDate.now()
        val items = AgendaItemTestFactory.createAgendaItemsForDate(
            date = date,
            eventCount = 3,
            taskCount = 2,
            reminderCount = 1
        )
        fakeAgendaRepository.setItems(items)
        
        // Act
        viewModel.onAction(AgendaScreenAction.OnDateChanged(date))
        advanceUntilIdle()
        
        // Assert
        val state = viewModel.state.value
        assertEquals(6, state.agendaItems.size)
    }
    
    @Test
    fun `when date changed, only items for that date are shown`() = runTest {
        // Arrange
        val date1 = LocalDate.of(2024, 10, 19)
        val date2 = LocalDate.of(2024, 10, 20)
        
        val itemsForDate1 = AgendaItemTestFactory.createAgendaItemsForDate(date1, 2, 1, 1)
        val itemsForDate2 = AgendaItemTestFactory.createAgendaItemsForDate(date2, 1, 1, 1)
        
        fakeAgendaRepository.setItems(itemsForDate1 + itemsForDate2)
        
        // Act
        viewModel.onAction(AgendaScreenAction.OnDateChanged(date1))
        advanceUntilIdle()
        
        // Assert
        val state = viewModel.state.value
        assertEquals(4, state.agendaItems.size)  // Only date1 items
        assertTrue(state.agendaItems.all { it.fromDate == date1 })
    }
    
    // ========================================
    // SavedStateHandle Tests
    // ========================================
    
    @Test
    fun `when date changed, date is saved to SavedStateHandle`() = runTest {
        // Arrange
        val newDate = LocalDate.of(2024, 10, 19)
        
        // Act
        viewModel.onAction(AgendaScreenAction.OnDateChanged(newDate))
        advanceUntilIdle()
        
        // Assert
        val savedTimestamp = savedStateHandle.get<Long>("selectedDate")
        assertNotNull(savedTimestamp)
    }
    
    @Test
    fun `when ViewModel recreated with saved date, restores from SavedStateHandle`() = runTest {
        // Arrange
        val savedDate = LocalDate.of(2024, 10, 19)
        val savedTimestamp = savedDate.atStartOfDay().atZone(
            java.time.ZoneId.systemDefault()
        ).toInstant().toEpochMilli()
        
        val savedState = SavedStateHandle(mapOf("selectedDate" to savedTimestamp))
        
        // Act
        val newViewModel = AgendaViewModel(
            sessionStorage = fakeSessionStorage,
            agendaRepository = fakeAgendaRepository,
            savedStateHandle = savedState,
            syncAgendaScheduler = fakeSyncScheduler
        )
        advanceUntilIdle()
        
        // Assert
        assertEquals(savedDate, newViewModel.state.value.selectedDate)
    }
    
    // ========================================
    // Edge Cases
    // ========================================
    
    @Test
    fun `when delete attempted on empty item id, nothing happens`() = runTest {
        // Arrange - no item selected for deletion
        
        // Act
        viewModel.onAction(AgendaScreenAction.OnDeleteAgendaItem)
        advanceUntilIdle()
        
        // Assert - no deletion should occur
        assertEquals(0, fakeAgendaRepository.deleteAgendaItemCalls.size)
    }
    
    @Test
    fun `when task status changed on non-existent task, handles gracefully`() = runTest {
        // Act & Assert - should not crash
        try {
            viewModel.onAction(AgendaScreenAction.OnChangeTaskStatus("non-existent-id"))
            advanceUntilIdle()
            
            // If it doesn't crash, the test passes
            // In production, this might throw or be a no-op
        } catch (e: Exception) {
            // Expected behavior - task not found
            assertTrue(e is IllegalArgumentException || e is NoSuchElementException)
        }
    }
    
    @Test
    fun `when multiple dates selected rapidly, each loads correct items`() = runTest {
        // Arrange
        val date1 = LocalDate.of(2024, 10, 19)
        val date2 = LocalDate.of(2024, 10, 20)
        val date3 = LocalDate.of(2024, 10, 21)
        
        fakeAgendaRepository.setItems(
            AgendaItemTestFactory.createAgendaItemsForDate(date1, 1, 0, 0) +
            AgendaItemTestFactory.createAgendaItemsForDate(date2, 0, 2, 0) +
            AgendaItemTestFactory.createAgendaItemsForDate(date3, 0, 0, 3)
        )
        
        // Act
        viewModel.onAction(AgendaScreenAction.OnDateChanged(date1))
        viewModel.onAction(AgendaScreenAction.OnDateChanged(date2))
        viewModel.onAction(AgendaScreenAction.OnDateChanged(date3))
        advanceUntilIdle()
        
        // Assert - should show date3 items (flatMapLatest cancels previous)
        val state = viewModel.state.value
        assertEquals(date3, state.selectedDate)
        assertEquals(3, state.agendaItems.size)  // 3 reminders
    }
    
    @Test
    fun `when items added to repository, UI automatically updates`() = runTest {
        // Arrange
        val date = LocalDate.now()
        viewModel.onAction(AgendaScreenAction.OnDateChanged(date))
        advanceUntilIdle()
        
        // Initially no items
        assertEquals(0, viewModel.state.value.agendaItems.size)
        
        // Act - add item to repository (simulating background sync)
        val newEvent = AgendaItemTestFactory.createTestEvent(
            fromDate = date,
            fromTime = java.time.LocalTime.of(14, 0)
        )
        fakeAgendaRepository.addItem(newEvent)
        advanceUntilIdle()
        
        // Assert - Flow should emit new data automatically
        assertEquals(1, viewModel.state.value.agendaItems.size)
    }
    
    @Test
    fun `when task status update fails, error event is emitted`() = runTest {
        // Arrange
        val events = mutableListOf<AgendaScreenEvent>()
        val job = launch {
            viewModel.event.collect { events.add(it) }
        }
        
        val task = AgendaItemTestFactory.createTestTask(id = "task-1")
        fakeAgendaRepository.addItem(task)
        viewModel.onAction(AgendaScreenAction.OnDateChanged(task.fromDate))
        advanceUntilIdle()
        
        fakeAgendaRepository.simulateNetworkError()
        
        // Act
        viewModel.onAction(AgendaScreenAction.OnChangeTaskStatus(task.id))
        advanceUntilIdle()
        
        // Assert
        assertTrue(events.any { it is AgendaScreenEvent.Error })
        
        job.cancel()
    }
    
    @Test
    fun `when task status update succeeds, success event is emitted`() = runTest {
        // Arrange
        val events = mutableListOf<AgendaScreenEvent>()
        val job = launch {
            viewModel.event.collect { events.add(it) }
        }
        
        val task = AgendaItemTestFactory.createTestTask(id = "task-1")
        fakeAgendaRepository.addItem(task)
        viewModel.onAction(AgendaScreenAction.OnDateChanged(task.fromDate))
        advanceUntilIdle()
        
        // Act
        viewModel.onAction(AgendaScreenAction.OnChangeTaskStatus(task.id))
        advanceUntilIdle()
        
        // Assert
        assertTrue(events.any { it is AgendaScreenEvent.Success })
        
        job.cancel()
    }
    
    // ========================================
    // Integration Tests
    // ========================================
    
    @Test
    fun `complete flow - user selects date, views items, deletes one`() = runTest {
        // Arrange
        val events = mutableListOf<AgendaScreenEvent>()
        val job = launch {
            viewModel.event.collect { events.add(it) }
        }
        
        val targetDate = LocalDate.of(2024, 10, 19)
        val items = AgendaItemTestFactory.createAgendaItemsForDate(targetDate, 2, 1, 1)
        fakeAgendaRepository.setItems(items)
        
        // User selects date
        viewModel.onAction(AgendaScreenAction.OnDateChanged(targetDate))
        advanceUntilIdle()
        
        // Assert items loaded
        assertEquals(4, viewModel.state.value.agendaItems.size)
        
        // User confirms delete
        val itemToDelete = items.first()
        viewModel.onAction(
            AgendaScreenAction.OnConfirmDeleteAgendaItem(
                agendaItemId = itemToDelete.id,
                type = com.aarevalo.tasky.agenda.presentation.agenda_detail.AgendaItemDetails.Event()
            )
        )
        advanceUntilIdle()
        
        // User executes delete
        viewModel.onAction(AgendaScreenAction.OnDeleteAgendaItem)
        advanceUntilIdle()
        
        // Assert
        assertTrue(events.any { it is AgendaScreenEvent.SuccessDeleteAgendaItem })
        assertEquals(3, viewModel.state.value.agendaItems.size)  // One less
        
        job.cancel()
    }
    
    @Test
    fun `complete flow - user navigates through multiple dates`() = runTest {
        // Arrange
        val date1 = LocalDate.of(2024, 10, 19)
        val date2 = LocalDate.of(2024, 10, 20)
        val date3 = LocalDate.of(2024, 10, 21)
        
        fakeAgendaRepository.setItems(
            AgendaItemTestFactory.createAgendaItemsForDate(date1, 2, 0, 0) +
            AgendaItemTestFactory.createAgendaItemsForDate(date2, 0, 3, 0) +
            AgendaItemTestFactory.createAgendaItemsForDate(date3, 0, 0, 1)
        )
        
        // Act - navigate through dates
        viewModel.onAction(AgendaScreenAction.OnDateChanged(date1))
        advanceUntilIdle()
        assertEquals(2, viewModel.state.value.agendaItems.size)
        
        viewModel.onAction(AgendaScreenAction.OnDateChanged(date2))
        advanceUntilIdle()
        assertEquals(3, viewModel.state.value.agendaItems.size)
        
        viewModel.onAction(AgendaScreenAction.OnDateChanged(date3))
        advanceUntilIdle()
        assertEquals(1, viewModel.state.value.agendaItems.size)
    }
}

