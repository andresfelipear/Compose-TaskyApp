# Interview Guide: Testing in Tasky

## Overview

Tasky includes **comprehensive unit tests** covering ViewModels, Repositories, and business logic. The tests demonstrate professional testing practices including edge case coverage, fake implementations, and proper coroutine testing.

---

## Test Coverage Summary

### ✅ **What's Tested**

| Component                        | Test File                             | Coverage  |
| -------------------------------- | ------------------------------------- | --------- |
| **LoginViewModel**               | `LoginViewModelTest.kt`               | 20+ tests |
| **AgendaViewModel**              | `AgendaViewModelTest.kt`              | 25+ tests |
| **OfflineFirstAgendaRepository** | `OfflineFirstAgendaRepositoryTest.kt` | 20+ tests |
| **InputValidator**               | `InputValidatorImplTest.kt`           | 25+ tests |

**Total: 90+ comprehensive unit tests** covering state management, business logic, error handling, and edge cases.

---

## Testing Architecture

### Test Infrastructure

```kotlin
// MainDispatcherRule - for ViewModel tests
@get:Rule
val mainDispatcherRule = MainDispatcherRule()

// TestDispatcherProvider - for repository tests
object TestDispatcherProvider : DispatcherProvider {
    private val testDispatcher = UnconfinedTestDispatcher()
    override val main = testDispatcher
    override val io = testDispatcher
    override val default = testDispatcher
}
```

### Fake Implementations (Not Mocks!)

**Why Fakes over Mocks?**

- ✅ **More realistic**: Fakes behave like real implementations
- ✅ **Reusable**: Same fake across multiple tests
- ✅ **Maintainable**: Easy to understand and modify
- ✅ **No framework dependency**: No Mockito or MockK needed
- ✅ **Better for Flow testing**: Emit real data streams

**Fakes Created:**

1. `FakeAuthRepository`
2. `FakeAgendaRepository`
3. `FakeSessionStorage`
4. `FakeInputValidator`
5. `FakeSyncAgendaScheduler`
6. `FakeLocalAgendaDataSource`
7. `FakeRemoteAgendaDataSource`
8. `FakeAlarmScheduler`
9. `FakeAgendaItemJsonConverter`

---

## Test Categories & Examples

### 1. ViewModel State Management Tests

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

**What this tests:**

- State updates correctly
- Actions are processed
- StateFlow emits new values

---

### 2. Event Emission Tests

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

    job.cancel()
}
```

**What this tests:**

- One-time events are emitted correctly
- Channel/Flow event pattern works
- Events can be collected and verified

---

### 3. Error Handling Tests

```kotlin
@Test
fun `when login fails with network error, error event is emitted`() = runTest {
    // Arrange
    val events = mutableListOf<LoginScreenEvent>()
    val job = launch {
        viewModel.event.collect { events.add(it) }
    }

    fakeAuthRepository.simulateNetworkError()
    viewModel.onAction(LoginScreenAction.OnEmailChanged("test@example.com"))
    viewModel.state.value.passwordState.edit { append("Password123") }

    // Act
    viewModel.onAction(LoginScreenAction.OnLogin)
    advanceUntilIdle()

    // Assert
    assertTrue(events.any { it is LoginScreenEvent.Error })
    assertFalse(viewModel.state.value.isLoading)  // Loading reset

    job.cancel()
}
```

**What this tests:**

- Error scenarios are handled
- Loading state is reset on error
- Error events are emitted
- State remains consistent

---

### 4. Edge Case Tests

```kotlin
@Test
fun `when multiple login attempts made rapidly, only processes sequentially`() = runTest {
    // Arrange
    viewModel.onAction(LoginScreenAction.OnEmailChanged("test@example.com"))
    viewModel.state.value.passwordState.edit { append("Password123") }
    advanceUntilIdle()

    // Act - rapid fire login attempts
    viewModel.onAction(LoginScreenAction.OnLogin)
    viewModel.onAction(LoginScreenAction.OnLogin)
    viewModel.onAction(LoginScreenAction.OnLogin)
    advanceUntilIdle()

    // Assert
    assertFalse(viewModel.state.value.isLoading)
    assertEquals(3, fakeAuthRepository.loginCalls.size)
}

@Test
fun `when empty credentials provided, login attempt still calls repository`() = runTest {
    // Act
    viewModel.onAction(LoginScreenAction.OnLogin)
    advanceUntilIdle()

    // Assert
    assertEquals(1, fakeAuthRepository.loginCalls.size)
}
```

**Edge cases covered:**

- ✅ Empty inputs
- ✅ Rapid-fire actions
- ✅ Very long strings
- ✅ Special characters
- ✅ Unicode characters
- ✅ Whitespace handling
- ✅ Null/missing data
- ✅ State changes during async operations

---

### 5. Flow/Reactive Data Tests

```kotlin
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
        time = date.atTime(14, 0).atZone(java.time.ZoneId.systemDefault())
    )
    fakeAgendaRepository.addItem(newEvent)
    advanceUntilIdle()

    // Assert - Flow should emit new data automatically
    assertEquals(1, viewModel.state.value.agendaItems.size)
}
```

**What this tests:**

- Reactive data updates
- Flow operators (flatMapLatest, distinctUntilChanged)
- Automatic UI updates when data changes

---

### 6. Business Logic Tests (InputValidator)

```kotlin
@Test
fun `valid email returns true`() {
    assertTrue(validator.isValidEmailPattern("test@example.com"))
    assertTrue(validator.isValidEmailPattern("user.name@example.com"))
    assertTrue(validator.isValidEmailPattern("user+tag@example.co.uk"))
}

@Test
fun `invalid email returns false`() {
    assertFalse(validator.isValidEmailPattern(""))
    assertFalse(validator.isValidEmailPattern("notanemail"))
    assertFalse(validator.isValidEmailPattern("@example.com"))
    assertFalse(validator.isValidEmailPattern("user@"))
}

@Test
fun `password with all criteria is valid`() {
    // Arrange
    val validPassword = "Password123"

    // Act
    val result = validator.isValidPassword(validPassword)

    // Assert
    assertTrue(result.isValid)
    assertTrue(result.errorMessage.isNullOrEmpty())
}

@Test
fun `password without uppercase returns invalid`() {
    // Arrange
    val password = "password123"

    // Act
    val result = validator.isValidPassword(password)

    // Assert
    assertFalse(result.isValid)
    assertNotNull(result.errorMessage)
}
```

**Business logic tested:**

- ✅ Email validation (regex)
- ✅ Password validation (length, uppercase, lowercase, digit)
- ✅ Edge cases (empty, special chars, unicode)

---

### 7. Repository/Offline-First Tests

```kotlin
@Test
fun `createAgendaItem saves to local database immediately`() = runTest {
    // Arrange
    val event = AgendaItemTestFactory.createTestEvent()

    // Act
    val result = repository.createAgendaItem(event)

    // Assert
    assertTrue(result is Result.Success)
    assertEquals(1, fakeLocalDataSource.insertCalls.size)
    assertTrue(fakeLocalDataSource.hasItem(event.id))
}

@Test
fun `createAgendaItem when offline saves locally and returns success`() = runTest {
    // Arrange
    val event = AgendaItemTestFactory.createTestEvent()
    fakeRemoteDataSource.simulateNoInternet()

    // Act
    val result = repository.createAgendaItem(event)

    // Assert - offline-first: succeeds locally even if remote fails
    assertTrue(result is Result.Success)
    assertTrue(fakeLocalDataSource.hasItem(event.id))
}

@Test
fun `fetchAgendaItems when online fetches from remote and updates local`() = runTest {
    // Arrange
    val remoteItems = AgendaItemTestFactory.createAgendaItemsForDate(LocalDate.now(), 3, 2, 1)
    fakeRemoteDataSource.setItems(remoteItems)

    // Act
    val result = repository.fetchAgendaItems()

    // Assert
    assertTrue(result is Result.Success)
    assertEquals(1, fakeRemoteDataSource.fetchFullAgendaCalls.size)
    assertEquals(6, fakeLocalDataSource.getItemCount())
}
```

**Offline-first patterns tested:**

- ✅ Local-first data access
- ✅ Offline create/update/delete
- ✅ Background sync behavior
- ✅ Error handling with fallback

---

## Test Infrastructure Details

### MainDispatcherRule

```kotlin
@ExperimentalCoroutinesApi
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
```

**Usage:**

```kotlin
@get:Rule
val mainDispatcherRule = MainDispatcherRule()
```

**Purpose:**

- Replaces Main dispatcher with TestDispatcher
- Allows ViewModels to run in tests
- Automatic cleanup after each test

---

### Fake Repository Pattern

```kotlin
class FakeAgendaRepository : AgendaRepository {
    private val agendaItems = MutableStateFlow<List<AgendaItem>>(emptyList())

    // Control behavior
    var shouldReturnError = false
    var errorToReturn: DataError = DataError.Network.UNKNOWN

    // Track calls
    val createAgendaItemCalls = mutableListOf<AgendaItem>()

    override suspend fun createAgendaItem(agendaItem: AgendaItem): EmptyResult<DataError> {
        createAgendaItemCalls.add(agendaItem)

        return if (shouldReturnError) {
            Result.Error(errorToReturn)
        } else {
            agendaItems.value = agendaItems.value + agendaItem
            Result.Success(Unit)
        }
    }

    override fun getAgendaItemsByDate(date: LocalDate): Flow<List<AgendaItem>> {
        return agendaItems.map { items ->
            items.filter { it.time.toLocalDate() == date }
        }
    }

    // Helper methods for tests
    fun reset() {
        agendaItems.value = emptyList()
        shouldReturnError = false
        createAgendaItemCalls.clear()
    }

    fun simulateNetworkError() {
        shouldReturnError = true
        errorToReturn = DataError.Network.NO_INTERNET
    }
}
```

---

## Test File Structure

```
app/src/test/java/com/aarevalo/tasky/
├── MainDispatcherRule.kt                    # ViewModel test rule
├── TestDispatcherProvider.kt                # Repository test dispatcher
│
├── auth/
│   ├── domain/
│   │   ├── FakeAuthRepository.kt           # Fake for AuthRepository
│   │   └── FakeInputValidator.kt           # Fake for InputValidator
│   ├── data/
│   │   └── util/
│   │       └── InputValidatorImplTest.kt   # 25+ business logic tests
│   └── presentation/
│       └── login/
│           └── LoginViewModelTest.kt        # 20+ ViewModel tests
│
├── agenda/
│   ├── domain/
│   │   ├── FakeAgendaRepository.kt         # Fake for AgendaRepository
│   │   ├── FakeSyncAgendaScheduler.kt      # Fake for sync scheduler
│   │   ├── FakeLocalAgendaDataSource.kt    # Fake for local data source
│   │   ├── FakeRemoteAgendaDataSource.kt   # Fake for remote data source
│   │   ├── FakeAlarmScheduler.kt           # Fake for alarm scheduler
│   │   ├── FakeAgendaItemJsonConverter.kt  # Fake for JSON converter
│   │   └── AgendaItemTestFactory.kt        # Test data factory
│   ├── data/
│   │   └── OfflineFirstAgendaRepositoryTest.kt  # 20+ repository tests
│   └── presentation/
│       └── agenda/
│           └── AgendaViewModelTest.kt       # 25+ ViewModel tests
│
└── core/
    └── domain/
        └── FakeSessionStorage.kt              # Fake for SessionStorage
```

---

## For Your Interview

### Q: "How do you approach testing in Android?"

> "I implement **comprehensive unit tests** at multiple levels in Tasky:
>
> **1. ViewModel Tests** - Test state management, user actions, event emission, and error handling. For example, my LoginViewModel has 20+ tests covering everything from successful login to edge cases like rapid-fire login attempts and special characters in passwords.
>
> **2. Repository Tests** - Test the offline-first pattern, ensuring data is saved locally immediately and synced in the background. I test both success and failure scenarios, like what happens when the network is unavailable.
>
> **3. Business Logic Tests** - Test utility classes like InputValidator with 25+ tests covering valid/invalid emails, password criteria, unicode characters, and boundary conditions.
>
> **4. Fake Implementations** - I use fakes instead of mocks because they're more maintainable and realistic. For example, FakeAgendaRepository actually stores items in a MutableStateFlow, so it behaves like the real repository with Flow emissions.
>
> **5. Test Infrastructure** - I have MainDispatcherRule for ViewModel tests and TestDispatcherProvider for repository tests, ensuring coroutines work properly in tests.
>
> **Total coverage: 90+ unit tests** with focus on edge cases and real-world scenarios."

---

### Q: "Why fakes instead of mocks?"

> "I prefer **fakes over mocks** for several reasons:
>
> **1. More realistic**: Fakes behave like real implementations. For example, my FakeAgendaRepository actually emits Flow updates when data changes, just like the real Room database would.
>
> **2. Reusable**: I can use the same fake across dozens of tests with different configurations using control flags like `shouldReturnError` or `simulateNetworkError()`.
>
> **3. Better for Flow testing**: Fakes can emit real Flow values, making reactive data testing straightforward. With mocks, Flow testing is complex and fragile.
>
> **4. Self-documenting**: Reading a fake implementation shows exactly how the real component should behave.
>
> **5. Maintainable**: When the interface changes, I update the fake once, and all tests still work.
>
> Here's an example from my tests:
>
> ```kotlin
> // Setup
> fakeAuthRepository.simulateNetworkError()
> viewModel.onAction(LoginScreenAction.OnLogin)
>
> // Verify
> assertTrue(events.any { it is LoginScreenEvent.Error })
> ```
>
> Much cleaner than setting up mock expectations!"

---

### Q: "Show me a test that demonstrates edge case coverage"

> "Sure! Here's a test from InputValidatorImplTest that shows comprehensive edge case coverage:
>
> ```kotlin
> @Test
> fun `password boundary - exactly 8 characters invalid, 9 valid`() {
>     // Arrange
>     val password8 = 'Pass1wor'   // 8 chars
>     val password9 = 'Pass1word'  // 9 chars
>
>     // Act
>     val result8 = validator.isValidPassword(password8)
>     val result9 = validator.isValidPassword(password9)
>
>     // Assert
>     assertFalse(result8.isValid)  // Too short
>     assertTrue(result9.isValid)   // Valid
> }
> ```
>
> I also test:
>
> - Empty strings
> - Unicode characters ('Pässwörd1')
> - Emojis ('Password1😀')
> - Very long strings (100+ characters)
> - Special characters
> - Whitespace handling
> - Multiple @ symbols in email
> - Rapid user interactions
>
> These edge cases catch bugs that users actually encounter in production."

---

### Q: "How do you test coroutines and Flows?"

> "I use Kotlin's coroutine test library with `runTest` and `advanceUntilIdle()`:
>
> **Testing Coroutines:**
>
> ```kotlin
> @Test
> fun `when login executes, loading state is managed correctly`() = runTest {
>     // Act
>     viewModel.onAction(LoginScreenAction.OnLogin)
>
>     // Assert - loading starts
>     assertTrue(viewModel.state.value.isLoading)
>
>     advanceUntilIdle()  // Wait for coroutines to complete
>
>     // Assert - loading stops
>     assertFalse(viewModel.state.value.isLoading)
> }
> ```
>
> **Testing Flows:**
>
> ```kotlin
> @Test
> fun `when local data changes, flow emits new data`() = runTest {
>     val emissions = mutableListOf<List<AgendaItem>>()
>
>     val job = launch {
>         repository.getAgendaItemsByDate(date).collect {
>             emissions.add(it)
>         }
>     }
>
>     fakeLocalDataSource.setItems(listOf(newEvent))
>     advanceUntilIdle()
>
>     assertTrue(emissions.size >= 2)  // Initial + update
>     assertEquals(1, emissions.last().size)
>
>     job.cancel()
> }
> ```
>
> **Key patterns:**
>
> - `runTest` provides TestScope with virtual time
> - `advanceUntilIdle()` runs all pending coroutines
> - `launch` in test scope for collecting Flows
> - Proper cleanup with `job.cancel()`"

---

### Q: "How do you test the offline-first pattern?"

> "I have specific tests that verify the offline-first behavior:
>
> ```kotlin
> @Test
> fun `createAgendaItem when offline saves locally and returns success`() = runTest {
>     // Arrange
>     val event = AgendaItemTestFactory.createTestEvent()
>     fakeRemoteDataSource.simulateNoInternet()
>
>     // Act
>     val result = repository.createAgendaItem(event)
>
>     // Assert - should succeed because it saves locally
>     assertTrue(result is Result.Success)
>     assertTrue(fakeLocalDataSource.hasItem(event.id))
>     // Remote call failed, but local succeeded
> }
> ```
>
> **Tests cover:**
>
> 1. **Local-first access**: Data served from local database immediately
> 2. **Offline operations**: Create/update/delete work without network
> 3. **Background sync**: Verify sync is scheduled when remote fails
> 4. **Reminder scheduling**: Alarms scheduled for future events
> 5. **Data consistency**: Local and remote stay in sync
>
> These tests prove the offline-first pattern works correctly."

---

## Test Execution

### Running Tests

```bash
# Run all unit tests
./gradlew test

# Run specific test class
./gradlew test --tests LoginViewModelTest

# Run with coverage report
./gradlew testDebugUnitTest jacocoTestReport
```

### Expected Output

```
LoginViewModelTest:
✅ initial state is correct
✅ when email changes, state is updated
✅ when login succeeds, success event is emitted
✅ when login fails with network error, error event is emitted
✅ complete login flow - success scenario
... (20+ more tests)

AgendaViewModelTest:
✅ initial state has current date selected
✅ when date changed, loads agenda items for that date
✅ when delete succeeds, success event is emitted
✅ when items added to repository, UI automatically updates
... (25+ more tests)

InputValidatorImplTest:
✅ valid email returns true
✅ password with all criteria is valid
✅ password boundary - exactly 8 characters invalid, 9 valid
... (25+ more tests)

Total: 90+ tests PASSED ✅
```

---

## Key Testing Principles Demonstrated

### 1. Arrange-Act-Assert Pattern

```kotlin
@Test
fun testName() = runTest {
    // Arrange - setup test data
    val testData = createTestData()

    // Act - perform the action
    val result = performAction()

    // Assert - verify the outcome
    assertEquals(expected, result)
}
```

### 2. Test Isolation

- Each test is independent
- `@Before` setup ensures clean state
- Fakes have `reset()` methods

### 3. Descriptive Test Names

```kotlin
`when login succeeds, success event is emitted`
`when delete agenda item confirmed, shows confirmation dialog`
`password boundary - exactly 8 characters invalid, 9 valid`
```

### 4. Edge Case Coverage

- Empty/null inputs
- Boundary conditions
- Concurrent operations
- Error scenarios
- Unicode/special characters

### 5. Test Data Factories

```kotlin
object AgendaItemTestFactory {
    fun createTestEvent(...)
    fun createTestTask(...)
    fun createTestReminder(...)
    fun createAgendaItemsForDate(...)
}
```

---

## Code Locations for Interview

- **ViewModel Tests**: `app/src/test/java/com/aarevalo/tasky/auth/presentation/login/LoginViewModelTest.kt`
- **Business Logic Tests**: `app/src/test/java/com/aarevalo/tasky/auth/data/util/InputValidatorImplTest.kt`
- **Repository Tests**: `app/src/test/java/com/aarevalo/tasky/agenda/data/OfflineFirstAgendaRepositoryTest.kt`
- **Test Infrastructure**: `app/src/test/java/com/aarevalo/tasky/MainDispatcherRule.kt`
- **Fakes**: `app/src/test/java/com/aarevalo/tasky/*/domain/Fake*.kt`

---

## What This Demonstrates

✅ **Professional testing practices** - Industry-standard patterns  
✅ **Edge case coverage** - Not just happy path  
✅ **Maintainable tests** - Fakes over mocks  
✅ **Coroutine expertise** - Proper async testing  
✅ **Testable architecture** - Design for testability  
✅ **Production-ready code** - Tests prove quality

---

## Interview Talking Points

1. **"I write tests for every ViewModel and critical business logic"**
2. **"My architecture is designed for testability with dependency injection and interfaces"**
3. **"I use fakes instead of mocks for better maintainability and realistic behavior"**
4. **"I test edge cases extensively - empty inputs, errors, concurrent operations"**
5. **"My tests prove the offline-first pattern works correctly"**
6. **"90+ unit tests provide confidence when refactoring or adding features"**

This is **exactly** what Neo Financial is looking for in an intermediate+ Android developer! 🚀
