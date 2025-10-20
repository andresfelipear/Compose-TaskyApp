# Testing Quick Reference - For Interview

Quick code snippets you can reference when discussing testing.

---

## Test Structure Template

```kotlin
@ExperimentalCoroutinesApi
class MyViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: MyViewModel
    private lateinit var fakeRepository: FakeRepository

    @Before
    fun setup() {
        fakeRepository = FakeRepository()
        viewModel = MyViewModel(fakeRepository)
    }

    @Test
    fun `when action happens, state is updated`() = runTest {
        // Arrange
        val testData = "test"

        // Act
        viewModel.onAction(MyAction(testData))
        advanceUntilIdle()

        // Assert
        assertEquals(testData, viewModel.state.value.data)
    }
}
```

---

## Testing State Updates

```kotlin
@Test
fun `when email changes, state is updated`() = runTest {
    // Arrange
    val testEmail = "test@example.com"

    // Act
    viewModel.onAction(LoginScreenAction.OnEmailChanged(testEmail))
    advanceUntilIdle()

    // Assert
    assertEquals(testEmail, viewModel.state.value.email)
}
```

---

## Testing Events (One-Time)

```kotlin
@Test
fun `when login succeeds, success event is emitted`() = runTest {
    // Arrange
    val events = mutableListOf<LoginScreenEvent>()
    val job = launch {
        viewModel.event.collect { events.add(it) }
    }

    viewModel.onAction(LoginScreenAction.OnEmailChanged("test@example.com"))
    viewModel.state.value.passwordState.edit { append("Password123") }
    advanceUntilIdle()

    // Act
    viewModel.onAction(LoginScreenAction.OnLogin)
    advanceUntilIdle()

    // Assert
    assertTrue(events.any { it is LoginScreenEvent.Success })

    job.cancel()  // ⚠️ Important: cleanup
}
```

---

## Testing Error Scenarios

```kotlin
@Test
fun `when operation fails, error event is emitted and state is reset`() = runTest {
    // Arrange
    val events = mutableListOf<MyEvent>()
    val job = launch {
        viewModel.event.collect { events.add(it) }
    }

    fakeRepository.simulateNetworkError()

    // Act
    viewModel.onAction(MyAction.Execute)
    advanceUntilIdle()

    // Assert
    assertTrue(events.any { it is MyEvent.Error })
    assertFalse(viewModel.state.value.isLoading)  // State reset

    job.cancel()
}
```

---

## Testing Flows

```kotlin
@Test
fun `when local data changes, flow emits new data`() = runTest {
    // Arrange
    val emissions = mutableListOf<List<AgendaItem>>()
    val date = LocalDate.now()

    val job = launch {
        repository.getAgendaItemsByDate(date).collect {
            emissions.add(it)
        }
    }

    advanceUntilIdle()
    assertEquals(1, emissions.size)  // Initial emission

    // Act - simulate data change
    fakeLocalDataSource.setItems(listOf(newEvent))
    advanceUntilIdle()

    // Assert
    assertTrue(emissions.size >= 2)  // Initial + update
    assertEquals(1, emissions.last().size)

    job.cancel()
}
```

---

## Testing Repository (Offline-First)

```kotlin
@Test
fun `createItem when offline saves locally and returns success`() = runTest {
    // Arrange
    val event = AgendaItemTestFactory.createTestEvent()
    fakeRemoteDataSource.simulateNoInternet()

    // Act
    val result = repository.createAgendaItem(event)

    // Assert - offline-first: succeeds locally
    assertTrue(result is Result.Success)
    assertTrue(fakeLocalDataSource.hasItem(event.id))
}
```

---

## Testing Business Logic

```kotlin
@Test
fun `valid email returns true`() {
    assertTrue(validator.isValidEmailPattern("test@example.com"))
    assertTrue(validator.isValidEmailPattern("user+tag@example.co.uk"))
}

@Test
fun `invalid email returns false`() {
    assertFalse(validator.isValidEmailPattern(""))
    assertFalse(validator.isValidEmailPattern("notanemail"))
    assertFalse(validator.isValidEmailPattern("@example.com"))
}

@Test
fun `password with all criteria is valid`() {
    val result = validator.isValidPassword("Password123")
    assertTrue(result.isValid)
}

@Test
fun `password boundary - 8 chars invalid, 9 chars valid`() {
    assertFalse(validator.isValidPassword("Pass1wor").isValid)   // 8 chars
    assertTrue(validator.isValidPassword("Pass1word").isValid)    // 9 chars
}
```

---

## Fake Implementation Pattern

```kotlin
class FakeAgendaRepository : AgendaRepository {
    private val items = MutableStateFlow<List<AgendaItem>>(emptyList())

    // Control flags
    var shouldReturnError = false
    var errorToReturn: DataError = DataError.Network.UNKNOWN

    // Track calls
    val createCalls = mutableListOf<AgendaItem>()

    override suspend fun createAgendaItem(item: AgendaItem): EmptyResult<DataError> {
        createCalls.add(item)

        return if (shouldReturnError) {
            Result.Error(errorToReturn)
        } else {
            items.value = items.value + item
            Result.Success(Unit)
        }
    }

    override fun getAgendaItemsByDate(date: LocalDate): Flow<List<AgendaItem>> {
        return items.map { it.filter { item -> item.time.toLocalDate() == date } }
    }

    // Helper methods
    fun reset() {
        items.value = emptyList()
        shouldReturnError = false
        createCalls.clear()
    }

    fun simulateNetworkError() {
        shouldReturnError = true
        errorToReturn = DataError.Network.NO_INTERNET
    }
}
```

---

## Test Data Factory

```kotlin
object AgendaItemTestFactory {
    fun createTestEvent(
        id: String = UUID.randomUUID().toString(),
        title: String = "Test Event",
        time: ZonedDateTime = ZonedDateTime.now().plusHours(1)
    ): AgendaItem {
        return AgendaItem(
            id = id,
            title = title,
            // ... other properties
        )
    }

    fun createAgendaItemsForDate(
        date: LocalDate,
        eventCount: Int = 1,
        taskCount: Int = 1,
        reminderCount: Int = 1
    ): List<AgendaItem> {
        // Creates multiple items for testing
    }
}

// Usage
val testEvent = AgendaItemTestFactory.createTestEvent(title = "Meeting")
val testItems = AgendaItemTestFactory.createAgendaItemsForDate(LocalDate.now(), 3, 2, 1)
```

---

## Edge Cases to Mention

```kotlin
// Empty inputs
@Test
fun `when empty email provided, validation fails`()

// Boundary conditions
@Test
fun `password boundary - exactly 8 characters invalid, 9 valid`()

// Concurrent operations
@Test
fun `when multiple login attempts made rapidly, only processes sequentially`()

// Special characters
@Test
fun `password with unicode characters handles correctly`()

// Offline scenarios
@Test
fun `createAgendaItem when offline saves locally and returns success`()

// State consistency during async
@Test
fun `when state changes during login, consistency is maintained`()

// Error recovery
@Test
fun `complete login flow - failure scenario with retry`()
```

---

## Common Test Patterns

### Pattern 1: State Verification

```kotlin
@Test
fun testName() = runTest {
    viewModel.onAction(SomeAction)
    advanceUntilIdle()
    assertEquals(expected, viewModel.state.value.property)
}
```

### Pattern 2: Event Collection

```kotlin
@Test
fun testName() = runTest {
    val events = mutableListOf<Event>()
    val job = launch {
        viewModel.event.collect { events.add(it) }
    }

    // ... trigger action
    advanceUntilIdle()

    assertTrue(events.any { it is ExpectedEvent })
    job.cancel()
}
```

### Pattern 3: Flow Testing

```kotlin
@Test
fun testName() = runTest {
    val emissions = mutableListOf<Data>()
    val job = launch {
        repository.getData().collect { emissions.add(it) }
    }

    // ... change data
    advanceUntilIdle()

    assertEquals(expectedCount, emissions.size)
    job.cancel()
}
```

### Pattern 4: Error Simulation

```kotlin
@Test
fun testName() = runTest {
    fakeRepository.simulateNetworkError()

    val result = viewModel.performAction()

    assertTrue(result is Result.Error)
}
```

---

## Interview Soundbites

**On Testing Philosophy:**

> "I write tests for everything that has business logic - ViewModels, repositories, and utility classes. My tests aren't just happy path; I focus heavily on edge cases like offline scenarios, empty inputs, and concurrent operations."

**On Test Structure:**

> "I follow Arrange-Act-Assert pattern with descriptive test names that read like specifications. When someone reads 'when login fails with network error, error event is emitted', they immediately understand what's being tested."

**On Fakes vs Mocks:**

> "I prefer fakes over mocks because they're more maintainable and behave like real implementations. My FakeAgendaRepository actually uses Flow and emits data just like the real Room database would."

**On Coverage:**

> "Tasky has 90+ unit tests covering ViewModels, repositories, and business logic. I test success paths, error scenarios, edge cases, and reactive data flows. This gives me confidence to refactor and add features without breaking existing functionality."

---

## Pro Tips

1. **If asked to write a test live:**

   - Start with `@Test fun` descriptive name
   - Arrange → Act → Assert structure
   - Use `advanceUntilIdle()` after async actions
   - Remember to `job.cancel()` if collecting

2. **If asked about specific test:**

   - Show InputValidator tests (impressive edge coverage)
   - Or Flow testing in AgendaViewModel
   - Or offline-first repository tests

3. **If asked about TDD:**
   - "I write tests for all new features and bugs"
   - "Tests serve as documentation and regression prevention"
   - "Having 90+ tests means I can refactor with confidence"

---

## Stats to Remember

- **90+ unit tests** across the project
- **20+ tests** for LoginViewModel
- **25+ tests** for AgendaViewModel
- **25+ tests** for InputValidator (business logic)
- **20+ tests** for OfflineFirstAgendaRepository
- **9 Fake implementations** for clean testing
- **100% Kotlin coroutine tests** using runTest

**This demonstrates you write production-quality, testable code!** 🎯
