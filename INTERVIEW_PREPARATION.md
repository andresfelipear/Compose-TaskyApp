# Neo Financial - Android Developer Interview Preparation

## About Tasky - Your Offline-First Agenda Manager

**Tasky** is a production-ready, offline-first mobile application for managing daily agendas including events, tasks, and reminders. The app demonstrates enterprise-level Android development with a strong emphasis on data synchronization, modern architecture patterns, and robust offline capabilities.

### Key Highlights:

- **Offline-First Architecture**: Full CRUD operations work seamlessly offline with background sync
- **Modern Tech Stack**: 100% Kotlin, Jetpack Compose UI, Hilt DI, Room, Retrofit, Coroutines & Flows
- **Smart Notifications**: Intelligent reminder scheduling using AlarmManager with exact timing
- **Background Sync**: WorkManager-based synchronization with retry logic and conflict resolution
- **Clean Architecture**: Feature-based modularization with clear separation of Data/Domain/Presentation layers
- **Secure Authentication**: JWT-based auth with automatic refresh token handling
- **Real-time Updates**: Reactive data flows using Kotlin Flow for instant UI updates

### Technologies Used:

- **UI**: Jetpack Compose with Material 3, Custom composables
- **Architecture**: MVVM with MVI patterns, Clean Architecture principles
- **DI**: Dagger Hilt with scoped dependencies
- **Database**: Room with complex relationships and migrations (5 schema versions)
- **Networking**: Retrofit + OkHttp with custom interceptors for auth
- **Async**: Kotlin Coroutines, Flow, StateFlow, Channel
- **Background Work**: WorkManager for reliable sync operations
- **Image Loading**: Coil 3 for efficient image handling
- **Data Storage**: Encrypted DataStore for secure session management
- **Logging**: Timber for structured logging
- **Testing**: JUnit, Coroutines Test library

---

## Interview Questions & Answers

### 1. What's your approach to app architecture?

**Answer:**

"In Tasky, I implemented a **Clean Architecture** approach organized by **feature modules**, with each feature following a clear **three-layer pattern**: Presentation, Domain, and Data.

**Feature-Based Organization:**
The app is structured around core features like `auth` and `agenda`, where each feature is self-contained with its own layers. This promotes scalability and makes it easier for teams to work in parallel without conflicts.

**Layered Architecture:**

**Presentation Layer** uses an **enhanced MVVM pattern with MVI elements**:

- **Views (Composables)**: Pure UI rendering based on immutable state
- **ViewModels**: Hold UI state as `StateFlow`, process user actions as sealed interfaces
- **State Management**: Single `ViewState` data class represents the entire screen state
- **Events**: One-time events (navigation, errors) communicated via `Channel` converted to `Flow`
- **Actions**: User interactions modeled as sealed interfaces (e.g., `AgendaScreenAction`)

Here's an example from my AgendaViewModel:

```kotlin
@HiltViewModel
class AgendaViewModel @Inject constructor(
    private val sessionStorage: SessionStorage,
    private val agendaRepository: AgendaRepository,
    private val savedStateHandle: SavedStateHandle,
    private val syncAgendaScheduler: SyncAgendaScheduler
): ViewModel() {

    private val _state = MutableStateFlow(AgendaScreenState(
        selectedDate = selectedDateTimeStamp?.let { parseTimestampToLocalDate(it) }
            ?: LocalDate.now()
    ))

    val state = _state
        .onStart {
            loadInitialData()
            observeSelectedDateChanges()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _state.value
        )

    private val eventChannel = Channel<AgendaScreenEvent>()
    val event = eventChannel.receiveAsFlow()

    fun onAction(action: AgendaScreenAction) {
        when(action) {
            is AgendaScreenAction.OnDateChanged -> {
                _state.update { currentState ->
                    currentState.copy(selectedDate = action.date)
                }
            }
            // ... other actions
        }
    }
}
```

**Domain Layer** defines business logic contracts:

- Repository interfaces (not implementations)
- Domain models (pure Kotlin data classes)
- Business rules independent of frameworks

**Data Layer** handles all data operations:

- Repository implementations deciding between local/remote sources
- Data sources for Room and Retrofit
- Mappers converting between network DTOs, database entities, and domain models

**Key Benefits:**

- **Testability**: Each layer can be tested independently with mocked dependencies
- **Separation of Concerns**: Clear boundaries between UI, business logic, and data
- **Scalability**: Easy to add new features without affecting existing code
- **Team Collaboration**: Different team members can work on different layers simultaneously

**Offline-First Strategy:**
I implemented an `OfflineFirstAgendaRepository` that:

1. Always serves data from the local Room database first (instant response)
2. Queues local changes in a `PendingItemSyncEntity` table
3. Uses WorkManager to sync changes when connectivity returns
4. Handles conflict resolution with the backend as the source of truth

This architecture aligns perfectly with Neo's need for maintainable, testable code that can scale with a growing team."

---

### 2. How do you manage state in Jetpack Compose?

**Answer:**

"I use a **unidirectional data flow (UDF)** pattern that combines MVVM with MVI concepts for predictable state management.

**State Management Pattern:**

**1. Immutable State Container:**
Every screen has a single data class representing its entire state:

```kotlin
data class AgendaScreenState(
    val selectedDate: LocalDate,
    val initials: String = "",
    val showDatePicker: Boolean = false,
    val relatedDates: List<LocalDate> = getRelatedDates(LocalDate.now()),
    val agendaItems: List<AgendaItem> = emptyList(),
    val isDeletingItem: Boolean = false,
    val showDeleteConfirmationDialog: Boolean = false
)
```

**2. StateFlow for State Exposure:**
The ViewModel exposes state as `StateFlow` with proper lifecycle handling:

```kotlin
val state = _state
    .onStart {
        loadInitialData()
        observeSelectedDateChanges()
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),  // Stop after 5s inactivity
        initialValue = _state.value
    )
```

The `WhileSubscribed(5000)` keeps the flow active for 5 seconds after the UI stops collecting, which is perfect for configuration changes without reloading data.

**3. User Actions as Sealed Interfaces:**
All user interactions are modeled as actions:

```kotlin
sealed interface AgendaScreenAction {
    data class OnDateChanged(val date: LocalDate) : AgendaScreenAction
    data object OnLogout : AgendaScreenAction
    data class OnChangeTaskStatus(val agendaItemId: String) : AgendaScreenAction
}
```

**4. One-Time Events via Channel:**
For navigation or showing snackbars, I use Channels to avoid re-triggering events on recomposition:

```kotlin
private val eventChannel = Channel<AgendaScreenEvent>()
val event = eventChannel.receiveAsFlow()

sealed interface AgendaScreenEvent {
    data object Success : AgendaScreenEvent
    data object SuccessLogout : AgendaScreenEvent
    data class Error(val errorMessage: UiText) : AgendaScreenEvent
}
```

**5. Composable Collection:**
In the Composable, I collect state and events separately:

```kotlin
@Composable
fun AgendaScreen(viewModel: AgendaViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when(event) {
                is AgendaScreenEvent.Error -> {
                    // Show snackbar
                }
                is AgendaScreenEvent.SuccessLogout -> {
                    // Navigate to login
                }
            }
        }
    }

    // UI renders based on state
    AgendaContent(
        state = state,
        onAction = viewModel::onAction
    )
}
```

**State Updates:**
I use the `update` function for safe, concurrent state modifications:

```kotlin
_state.update { currentState ->
    currentState.copy(
        selectedDate = action.date,
        relatedDates = getRelatedDates(action.date)
    )
}
```

**Advanced: Reactive Data Flows**
For observing database changes, I use `flatMapLatest` to switch flows when dependencies change:

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
private fun observeSelectedDateChanges() {
    viewModelScope.launch {
        _state.mapNotNull { it.selectedDate }
            .distinctUntilChanged()
            .flatMapLatest { selectedDate ->
                agendaRepository.getAgendaItemsByDate(selectedDate)
            }
            .onEach { agendaItems ->
                _state.update { currentState ->
                    currentState.copy(agendaItems = agendaItems)
                }
            }
            .launchIn(viewModelScope)
    }
}
```

This automatically updates the UI when the database changes or when the selected date changes.

**Key Benefits:**

- **Predictable**: Single source of truth for screen state
- **Testable**: Easy to test state transitions
- **Survives Configuration Changes**: StateFlow with proper lifecycle
- **No Redundant Events**: Channel ensures one-time events aren't repeated
- **Type-Safe**: Sealed interfaces catch errors at compile time"

---

### 3. Explain how you use Coroutines and Flows.

**Answer:**

"I leverage Coroutines and Flows extensively throughout Tasky for asynchronous operations, reactive data streams, and background work coordination.

**1. Structured Concurrency with ViewModelScope:**
All UI-related coroutines are launched in `viewModelScope`, which automatically cancels when the ViewModel is cleared:

```kotlin
fun onAction(action: AgendaScreenAction) {
    when(action) {
        is AgendaScreenAction.OnDeleteAgendaItem -> {
            viewModelScope.launch {
                _state.update { it.copy(isDeletingItem = true) }

                val result = agendaRepository.deleteAgendaItem(
                    _state.value.agendaItemIdToDelete,
                    agendaItem.type
                )

                when(result) {
                    is Result.Error -> {
                        eventChannel.send(AgendaScreenEvent.Error(result.error.asUiText()))
                    }
                    is Result.Success -> {
                        eventChannel.send(AgendaScreenEvent.SuccessDeleteAgendaItem)
                    }
                }

                _state.update { it.copy(isDeletingItem = false) }
            }
        }
    }
}
```

**2. Application-Scoped Coroutines for Background Work:**
For operations that should continue beyond ViewModel lifecycle, I use an application-scoped CoroutineScope:

```kotlin
@Provides
@Singleton
fun provideCoroutineScope(): CoroutineScope {
    return CoroutineScope(Dispatchers.IO)
}

// In OfflineFirstAgendaRepository:
class OfflineFirstAgendaRepository @Inject constructor(
    private val applicationScope: CoroutineScope,
    // ... other dependencies
) {
    override suspend fun createAgendaItem(agendaItem: AgendaItem): EmptyResult<DataError> {
        // Save to local DB immediately for offline support
        localAgendaSource.insertAgendaItem(agendaItem)
        scheduleReminder(agendaItem)

        // Background sync that continues even if ViewModel is destroyed
        applicationScope.launch {
            syncAgendaScheduler.scheduleSyncAgenda(
                syncType = SyncAgendaScheduler.SyncType.CreateItem(agendaItem)
            )
        }

        return Result.Success(Unit)
    }
}
```

**3. Flows for Reactive Data Streams:**
Room DAOs return Flows that emit whenever data changes:

```kotlin
@Dao
interface EventDao {
    @Query("SELECT * FROM EventEntity WHERE date(from_timestamp / 1000, 'unixepoch', 'localtime') = :date")
    fun getEventsByDate(date: String): Flow<List<EventEntity>>
}

// Repository transforms and combines multiple Flow sources:
override fun getAgendaItemsByDate(date: LocalDate): Flow<List<AgendaItem>> {
    return localAgendaSource.getAgendaItemsByDate(date)
}
```

**4. Advanced Flow Operators:**

**flatMapLatest** for switching data streams:

```kotlin
_state.mapNotNull { it.selectedDate }
    .distinctUntilChanged()
    .flatMapLatest { selectedDate ->
        agendaRepository.getAgendaItemsByDate(selectedDate)
    }
    .onEach { agendaItems ->
        _state.update { it.copy(agendaItems = agendaItems) }
    }
    .launchIn(viewModelScope)
```

This cancels the previous Flow when the date changes and starts collecting from the new one.

**5. Flow Transformation and Combination:**
In my repository, I combine multiple database Flows:

```kotlin
override fun getAgendaItemsByDate(date: LocalDate): Flow<List<AgendaItem>> {
    val formattedDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE)

    return combine(
        eventDao.getEventsByDate(formattedDate),
        taskDao.getTasksByDate(formattedDate),
        reminderDao.getRemindersByDate(formattedDate)
    ) { events, tasks, reminders ->
        val eventItems = events.map { it.toDomainModel() }
        val taskItems = tasks.map { it.toDomainModel() }
        val reminderItems = reminders.map { it.toDomainModel() }

        (eventItems + taskItems + reminderItems).sortedBy { it.time }
    }
}
```

**6. Dispatcher Management:**
I use a `DispatcherProvider` abstraction for testability:

```kotlin
interface DispatcherProvider {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
}

object StandardDispatcherProvider : DispatcherProvider {
    override val main = Dispatchers.Main
    override val io = Dispatchers.IO
    override val default = Dispatchers.Default
}

// Usage in repository:
suspend fun processPhotos(photos: List<Photo>): List<ByteArray> =
    withContext(dispatcher.io) {
        photos.map { photo ->
            async {
                photoByteLoader.loadPhotoBytes(photo.uri)
            }
        }.awaitAll()
    }
```

**7. Error Handling:**
I use a custom `Result` sealed class for explicit error handling:

```kotlin
sealed interface Result<out D, out E: Error> {
    data class Success<out D>(val data: D) : Result<D, Nothing>
    data class Error<out E: Error>(val error: E) : Result<Nothing, E>
}

// Usage:
suspend fun fetchAgendaItems(): EmptyResult<DataError> {
    return when(val result = remoteAgendaSource.fetchFullAgenda()) {
        is Result.Error -> {
            Timber.e("Error fetching agenda items remotely!")
            result.asEmptyDataResult()
        }
        is Result.Success -> {
            // Process data
            Result.Success(Unit)
        }
    }
}
```

**8. Parallel Async Operations:**
For independent operations that can run concurrently:

```kotlin
suspend fun syncAllPendingItems() = coroutineScope {
    val createDeferred = async { syncCreateOperations() }
    val updateDeferred = async { syncUpdateOperations() }
    val deleteDeferred = async { syncDeleteOperations() }

    awaitAll(createDeferred, updateDeferred, deleteDeferred)
}
```

**Key Benefits:**

- **Cancellation Handling**: Structured concurrency prevents leaks
- **Reactive UI**: Flow automatically updates UI when data changes
- **Performance**: Parallel operations with async/await
- **Testability**: DispatcherProvider allows using TestDispatchers
- **Type Safety**: Explicit Result types instead of exceptions"

---

### 4. How do you handle dependency injection?

**Answer:**

"I use **Dagger Hilt** for dependency injection throughout Tasky, taking advantage of its compile-time verification, performance, and Android-specific optimizations.

**Architecture:**

**1. Module Organization:**
I organize DI modules by feature for clarity:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object TaskyAgendaModule {

    @Provides
    @Singleton
    fun provideAgendaApi(
        moshi: Moshi,
        @Named("authenticated") client: OkHttpClient
    ): TaskyAgendaApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()
            .create()
    }

    @Provides
    @Singleton
    fun provideAgendaRepository(
        remoteAgendaDataSource: RemoteAgendaDataSource,
        localAgendaSource: LocalAgendaDataSource,
        sessionStorage: SessionStorage,
        pendingItemSyncDao: PendingItemSyncDao,
        coroutineScope: CoroutineScope,
        syncAgendaScheduler: SyncAgendaScheduler,
        agendaItemJsonConverter: AgendaItemJsonConverter,
        alarmScheduler: AlarmScheduler
    ): AgendaRepository {
        return OfflineFirstAgendaRepository(
            remoteAgendaDataSource,
            localAgendaSource,
            sessionStorage,
            coroutineScope,
            pendingItemSyncDao,
            syncAgendaScheduler,
            agendaItemJsonConverter,
            StandardDispatcherProvider,
            alarmScheduler
        )
    }
}
```

**2. Qualifiers for Multiple Bindings:**
I use `@Named` qualifiers to differentiate between similar dependencies:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @Named("unauthenticated")
    fun provideUnauthenticatedOkHttpClient(
        apiKeyInterceptor: ApiKeyInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(apiKeyInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named("authenticated")
    fun provideOkHttpClient(
        authTokenInterceptor: AuthTokenInterceptor,
        apiKeyInterceptor: ApiKeyInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(authTokenInterceptor)
            .build()
    }
}
```

The authentication module uses the unauthenticated client (to avoid circular dependencies), while all other APIs use the authenticated client.

**3. ViewModel Injection:**
Hilt provides seamless ViewModel injection:

```kotlin
@HiltViewModel
class AgendaViewModel @Inject constructor(
    private val sessionStorage: SessionStorage,
    private val agendaRepository: AgendaRepository,
    private val savedStateHandle: SavedStateHandle,
    private val syncAgendaScheduler: SyncAgendaScheduler
): ViewModel() {
    // ViewModel implementation
}

// In Composable:
@Composable
fun AgendaScreen(viewModel: AgendaViewModel = hiltViewModel()) {
    // Screen implementation
}
```

**4. Application Setup:**
Simple setup in the Application class:

```kotlin
@HiltAndroidApp
class TaskyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
```

**5. Database and DAO Injection:**
Room database and DAOs are provided through Hilt:

```kotlin
@Provides
@Singleton
fun provideDatabase(
    @ApplicationContext context: Context
): AgendaDatabase {
    return Room.databaseBuilder(
        context.applicationContext,
        AgendaDatabase::class.java,
        "agenda_database"
    )
        .fallbackToDestructiveMigration(true)
        .build()
}

@Provides
@Singleton
fun provideEventDao(database: AgendaDatabase): EventDao = database.eventDao

@Provides
@Singleton
fun provideTaskDao(database: AgendaDatabase): TaskDao = database.taskDao
```

**6. WorkManager Integration:**
For WorkManager, I use Hilt's WorkManager integration:

```kotlin
@HiltWorker
class CreateAgendaItemWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val remoteAgendaDataSource: RemoteAgendaDataSource,
    private val pendingItemSyncDao: PendingItemSyncDao,
    private val agendaItemJsonConverter: AgendaItemJsonConverter
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Worker implementation
    }
}
```

**7. Testing Support:**
For testing, I program to interfaces which makes mocking easy:

```kotlin
interface AgendaRepository {
    suspend fun createAgendaItem(agendaItem: AgendaItem): EmptyResult<DataError>
    fun getAgendaItemsByDate(date: LocalDate): Flow<List<AgendaItem>>
    // ... other methods
}

// Test implementation:
class FakeAgendaRepository : AgendaRepository {
    private val agendaItems = mutableListOf<AgendaItem>()

    override suspend fun createAgendaItem(agendaItem: AgendaItem): EmptyResult<DataError> {
        agendaItems.add(agendaItem)
        return Result.Success(Unit)
    }

    // ... other fake implementations
}
```

**8. Scoping:**
I use `@Singleton` for dependencies that should live for the app's lifetime and leverage Hilt's automatic scoping for ViewModels.

**Key Benefits:**

- **Compile-Time Safety**: Errors caught at compilation, not runtime
- **Performance**: Zero runtime overhead compared to reflection-based DI
- **Android Integration**: Built-in support for ViewModels, WorkManager, etc.
- **Testability**: Easy to replace implementations for testing
- **Maintainability**: Clear dependency graphs, easy to understand
- **Scalability**: Adding new dependencies doesn't require boilerplate"

---

### 5. How do you ensure code quality in a team setting?

**Answer:**

"I employ multiple strategies to ensure code quality, from architectural patterns to tooling and processes:

**1. Architecture & Design Patterns:**

**Clean Architecture** enforces separation of concerns:

- Domain layer defines contracts that other layers implement
- Repository pattern abstracts data sources
- ViewModel isolates business logic from UI

This makes code easier to review because each layer has a clear, single responsibility.

**2. Type Safety & Sealed Interfaces:**
I use Kotlin's type system to catch errors at compile time:

```kotlin
sealed interface Result<out D, out E: Error> {
    data class Success<out D>(val data: D) : Result<D, Nothing>
    data class Error<out E: Error>(val error: E) : Result<Nothing, E>
}

sealed interface AgendaScreenAction {
    data class OnDateChanged(val date: LocalDate) : AgendaScreenAction
    data object OnLogout : AgendaScreenAction
}
```

This eliminates entire classes of runtime errors and makes code self-documenting.

**3. Immutable State:**
All state is immutable, using data classes with `copy()`:

```kotlin
data class AgendaScreenState(
    val selectedDate: LocalDate,
    val agendaItems: List<AgendaItem> = emptyList(),
    val isDeletingItem: Boolean = false
)

// Updates are explicit and traceable
_state.update { it.copy(isDeletingItem = true) }
```

This prevents subtle bugs from mutable state and makes state transitions easy to track in code reviews.

**4. Dependency Injection for Testability:**
All dependencies are injected, making testing and mocking straightforward:

```kotlin
class AgendaViewModel @Inject constructor(
    private val agendaRepository: AgendaRepository,  // Interface, not implementation
    private val sessionStorage: SessionStorage,
    private val syncAgendaScheduler: SyncAgendaScheduler
) : ViewModel()
```

**5. Custom Abstractions for Platform Dependencies:**
I abstract platform-specific code for testability:

```kotlin
interface DispatcherProvider {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
}

// Test implementation:
class TestDispatcherProvider : DispatcherProvider {
    override val main = UnconfinedTestDispatcher()
    override val io = UnconfinedTestDispatcher()
    override val default = UnconfinedTestDispatcher()
}
```

**6. Structured Logging:**
I use Timber for structured logging throughout the app:

```kotlin
class OfflineFirstAgendaRepository {
    override suspend fun deleteAgendaItem(id: String, type: AgendaItemType): EmptyResult<DataError> {
        Timber.d("Deleting agenda item - ID: %s, Type: %s", id, type)

        return when(val result = remoteAgendaDataSource.deleteAgendaItem(id, type)) {
            is Result.Error -> {
                Timber.e("Failed to delete remotely! Error: %s. Queuing for sync.", result.error)
                // Queue for later sync
            }
            is Result.Success -> {
                Timber.d("Successfully deleted agenda item %s", id)
                Result.Success(Unit)
            }
        }
    }
}
```

This helps with debugging and makes code reviews more informative.

**7. Error Handling Strategy:**
Explicit error handling with custom Result types:

```kotlin
when(val result = agendaRepository.deleteAgendaItem(id, type)) {
    is Result.Error -> {
        eventChannel.send(AgendaScreenEvent.Error(result.error.asUiText()))
    }
    is Result.Success -> {
        eventChannel.send(AgendaScreenEvent.SuccessDeleteAgendaItem)
    }
}
```

This makes error cases explicit and forces developers to handle them.

**8. Code Documentation:**
I document complex business logic and architectural decisions:

```kotlin
/**
 * Offline-first repository that prioritizes local data and syncs with remote when possible.
 *
 * Local changes are queued in PendingItemSyncEntity and processed by WorkManager.
 * The backend is considered the source of truth after successful synchronization.
 */
class OfflineFirstAgendaRepository
```

**9. Testing Practices:**

**Unit Tests** for ViewModels and Repositories:

```kotlin
@Test
fun `when date changes, should load agenda items for new date`() = runTest {
    // Arrange
    val testDate = LocalDate.of(2024, 10, 19)
    val fakeRepo = FakeAgendaRepository()
    val viewModel = AgendaViewModel(fakeRepo, /* ... */)

    // Act
    viewModel.onAction(AgendaScreenAction.OnDateChanged(testDate))

    // Assert
    assertEquals(testDate, viewModel.state.value.selectedDate)
}
```

**10. Code Review Checklist:**
In a team, I'd advocate for reviewing:

- **Architecture**: Does it fit the app's layered structure?
- **State Management**: Is state immutable and properly scoped?
- **Error Handling**: Are all error cases handled?
- **Testing**: Are critical paths covered by tests?
- **Performance**: Are there unnecessary recompositions or database queries?
- **Naming**: Are variables, functions, and classes clearly named?
- **Documentation**: Is complex logic documented?

**11. Static Analysis:**
I'd use tools like:

- **Detekt** for Kotlin code analysis
- **Lint** for Android-specific issues
- **LeakCanary** for memory leak detection

**12. PR Practices:**

- Small, focused PRs (single feature or fix)
- Descriptive PR titles and descriptions
- Self-review before requesting review
- Address feedback constructively
- Use PR templates for consistency

**13. CI/CD Integration:**
Automated checks in CI:

- Build verification
- Unit test execution
- Lint checks
- Code coverage reports

**Key Point for Neo:**
Given Neo's emphasis on code reviews and high-quality code, I'm comfortable with rigorous review processes. My architecture naturally supports this by making code easier to understand and test. The use of sealed interfaces, immutable state, and dependency injection means reviewers can focus on business logic rather than untangling complex state management or dependencies."

---

## Additional Interview Questions & Answers

### 6. How do you handle offline-first architecture and data synchronization?

**Answer:**

"Tasky's offline-first architecture is one of its core strengths. Here's how I implemented it:

**1. Local-First Data Access:**
The repository always serves data from Room database first:

```kotlin
override fun getAgendaItemsByDate(date: LocalDate): Flow<List<AgendaItem>> {
    // Always returns local data - instant response
    return localAgendaSource.getAgendaItemsByDate(date)
}
```

**2. Optimistic UI Updates:**
When users create/update/delete items, they're saved to the local database immediately:

```kotlin
override suspend fun createAgendaItem(agendaItem: AgendaItem): EmptyResult<DataError> {
    // Save locally first - instant feedback
    localAgendaSource.insertAgendaItem(agendaItem)
    scheduleReminder(agendaItem)

    // Queue for background sync
    applicationScope.launch {
        syncAgendaScheduler.scheduleSyncAgenda(
            syncType = SyncAgendaScheduler.SyncType.CreateItem(agendaItem)
        )
    }

    return Result.Success(Unit)
}
```

**3. Pending Sync Queue:**
Local changes are tracked in a `PendingItemSyncEntity` table:

```kotlin
@Entity
data class PendingItemSyncEntity(
    @PrimaryKey val id: String,
    val itemId: String,
    val itemType: String,
    val operation: SyncOperation,  // CREATE, UPDATE, DELETE
    val agendaItemJson: String,  // Serialized item
    val timestamp: Long
)
```

**4. WorkManager for Reliable Sync:**
I use dedicated Workers for each operation type:

```kotlin
@HiltWorker
class CreateAgendaItemWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val remoteAgendaDataSource: RemoteAgendaDataSource,
    private val pendingItemSyncDao: PendingItemSyncDao,
    private val agendaItemJsonConverter: AgendaItemJsonConverter
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val itemId = inputData.getString(KEY_AGENDA_ITEM_ID) ?: return Result.failure()

        val pendingSync = pendingItemSyncDao.getPendingSyncById(itemId)
            ?: return Result.success()

        val agendaItem = agendaItemJsonConverter.deserialize(pendingSync.agendaItemJson)

        return when(val result = remoteAgendaDataSource.createAgendaItem(agendaItem)) {
            is Result.Error -> {
                // Network error - retry with backoff
                Result.retry()
            }
            is Result.Success -> {
                // Success - remove from pending queue
                pendingItemSyncDao.deletePendingSync(itemId)
                Result.success()
            }
        }
    }
}
```

**5. Retry Logic with Backoff:**
Workers are configured with exponential backoff:

```kotlin
val workRequest = OneTimeWorkRequestBuilder<CreateAgendaItemWorker>()
    .setInputData(workDataOf(KEY_AGENDA_ITEM_ID to agendaItem.id))
    .setConstraints(
        Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    )
    .setBackoffCriteria(
        BackoffPolicy.EXPONENTIAL,
        WorkRequest.MIN_BACKOFF_MILLIS,
        TimeUnit.MILLISECONDS
    )
    .build()
```

**6. Periodic Background Fetch:**
A periodic worker checks for server updates every 30 minutes:

```kotlin
@HiltWorker
class PeriodicFetchAgendaWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val remoteAgendaDataSource: RemoteAgendaDataSource,
    private val localAgendaSource: LocalAgendaDataSource
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return when(val result = remoteAgendaDataSource.fetchFullAgenda()) {
            is Result.Error -> Result.retry()
            is Result.Success -> {
                // Merge remote data with local
                localAgendaSource.upsertAgendaItems(result.data)
                Result.success()
            }
        }
    }
}
```

**7. Conflict Resolution:**
The backend is treated as the source of truth. When syncing:

- Local creates are sent to the server and get server-assigned IDs if needed
- Local updates overwrite server data (last-write-wins)
- Periodic fetches ensure we receive updates from other devices
- On conflicts, server data takes precedence after fetch

**Key Benefits:**

- **Instant UI Response**: No waiting for network
- **Works Offline**: Full CRUD operations without connectivity
- **Reliable Sync**: WorkManager guarantees eventual consistency
- **Battery Efficient**: Intelligent batching and constraints
- **Handles Edge Cases**: App crashes, force closes, or network interruptions don't lose data"

---

### 7. Explain your approach to UI development with Jetpack Compose.

**Answer:**

"I follow Compose best practices focusing on reusability, performance, and declarative patterns:

**1. Stateless Composables:**
I separate state from UI by creating stateless composables:

```kotlin
@Composable
fun AgendaItem(
    agendaItem: AgendaItem,
    onOpenClick: (String) -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Pure UI rendering
    Card(modifier = modifier) {
        Column {
            Text(agendaItem.title)
            Text(agendaItem.description)
            Row {
                IconButton(onClick = { onOpenClick(agendaItem.id) }) {
                    Icon(Icons.Default.OpenInNew, null)
                }
                IconButton(onClick = { onEditClick(agendaItem.id) }) {
                    Icon(Icons.Default.Edit, null)
                }
            }
        }
    }
}
```

**2. Composition over Inheritance:**
I create reusable components that can be composed:

```kotlin
@Composable
fun TaskyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            isError = isError,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            modifier = Modifier.fillMaxWidth()
        )
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
```

**3. Remember and Keys:**
I use `remember` for derived state and keys for list performance:

```kotlin
@Composable
fun CalendarDaysSelector(
    selectedDate: LocalDate,
    relatedDates: List<LocalDate>,
    onDateSelected: (LocalDate) -> Unit
) {
    LazyRow {
        items(
            items = relatedDates,
            key = { it.toEpochDay() }  // Stable key for better performance
        ) { date ->
            CalendarDay(
                date = date,
                isSelected = date == selectedDate,
                onClick = { onDateSelected(date) }
            )
        }
    }
}
```

**4. Side Effects:**
I use the appropriate effect for each scenario:

```kotlin
@Composable
fun AgendaScreen(viewModel: AgendaViewModel = hiltViewModel()) {
    // LaunchedEffect for one-time events
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when(event) {
                is AgendaScreenEvent.Error -> {
                    snackbarHostState.showSnackbar(event.errorMessage.asString())
                }
            }
        }
    }

    // DisposableEffect for cleanup
    DisposableEffect(Unit) {
        onDispose {
            // Cleanup if needed
        }
    }
}
```

**5. Performance Optimization:**

- Stable parameters to prevent recomposition
- `LazyColumn`/`LazyRow` for large lists
- `derivedStateOf` for expensive computations

```kotlin
@Composable
fun AgendaList(agendaItems: List<AgendaItem>) {
    val groupedItems by remember(agendaItems) {
        derivedStateOf {
            agendaItems.groupBy { it.type }
        }
    }

    LazyColumn {
        groupedItems.forEach { (type, items) ->
            item(key = type) {
                Text(type.name, style = MaterialTheme.typography.titleMedium)
            }
            items(
                items = items,
                key = { it.id }
            ) { item ->
                AgendaItem(agendaItem = item, /*...*/)
            }
        }
    }
}
```

**6. Material 3 and Theming:**
Full Material 3 support with dynamic theming:

```kotlin
@Composable
fun TaskyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

**7. Navigation:**
Type-safe navigation with Jetpack Navigation Compose:

```kotlin
sealed class Destination(val route: String) {
    data object Login : Destination("login")
    data object Agenda : Destination("agenda")
    data class AgendaDetail(val itemId: String, val type: String) :
        Destination("agenda_detail/{itemId}/{type}")
}
```

**8. Preview Support:**
I create comprehensive previews for development:

```kotlin
@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AgendaItemPreview() {
    TaskyTheme {
        AgendaItem(
            agendaItem = AgendaItem.Event(
                id = "1",
                title = "Team Meeting",
                description = "Discuss Q4 goals",
                time = ZonedDateTime.now()
            ),
            onOpenClick = {},
            onEditClick = {},
            onDeleteClick = {}
        )
    }
}
```

**Key Benefits:**

- **Reusability**: Components can be used across screens
- **Performance**: Proper optimization prevents unnecessary recompositions
- **Testability**: Stateless composables are easy to test
- **Maintainability**: Clear separation of state and UI"

---

### 8. How do you handle background work and notifications?

**Answer:**

"I use a combination of WorkManager and AlarmManager for reliable background operations and notifications:

**1. Alarm Scheduling:**
I created an `AlarmScheduler` interface with an `AlarmManager` implementation:

```kotlin
class AlarmManagerAlarmScheduler(
    private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(alarmItem: AlarmItem) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("ALARM_ID", alarmItem.id)
            putExtra("ALARM_TITLE", alarmItem.title)
            putExtra("ALARM_DESCRIPTION", alarmItem.description)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmItem.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmItem.time,
            pendingIntent
        )
    }

    override fun cancel(alarmItem: AlarmItem) {
        // Cancel alarm implementation
    }
}
```

**2. Smart Reminder Scheduling:**
Only schedule reminders for future times:

```kotlin
override suspend fun scheduleReminder(agendaItem: AgendaItem) {
    val currentUserId = sessionStorage.getSession()?.userId
    if(currentUserId == null) {
        alarmScheduler.cancel(agendaItem.toAlarmItem())
        return
    }

    val reminderTimeMillis = agendaItem.remindAt.toInstant().toEpochMilli()
    val currentTimeMillis = ZonedDateTime.now().toInstant().toEpochMilli()
    val isTimeInFuture = reminderTimeMillis > currentTimeMillis

    if (isTimeInFuture) {
        alarmScheduler.schedule(agendaItem.toAlarmItem())
        Timber.d("Reminder scheduled for item %s at %s", agendaItem.id, agendaItem.remindAt)
    } else {
        Timber.w("Skipping scheduling reminder for item %s as time is in the past", agendaItem.id)
        alarmScheduler.cancel(agendaItem.toAlarmItem())
    }
}
```

**3. Notification Permissions:**
Handle Android 13+ notification permissions:

```kotlin
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // Request permission
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            REQUEST_CODE_NOTIFICATIONS
        )
    }
}
```

**4. WorkManager for Sync:**
Dedicated workers for each sync operation:

```kotlin
class SyncAgendaWorkerScheduler(
    private val context: Context,
    private val pendingItemSyncDao: PendingItemSyncDao,
    private val sessionStorage: SessionStorage,
    private val coroutineScope: CoroutineScope,
    private val agendaItemJsonConverter: AgendaItemJsonConverter
) : SyncAgendaScheduler {

    override suspend fun scheduleSyncAgenda(syncType: SyncAgendaScheduler.SyncType) {
        when(syncType) {
            is SyncAgendaScheduler.SyncType.CreateItem -> {
                // Queue the item for sync
                val pendingSync = PendingItemSyncEntity(
                    id = UUID.randomUUID().toString(),
                    itemId = syncType.agendaItem.id,
                    itemType = syncType.agendaItem.type.name,
                    operation = SyncOperation.CREATE,
                    agendaItemJson = agendaItemJsonConverter.serialize(syncType.agendaItem),
                    timestamp = System.currentTimeMillis()
                )
                pendingItemSyncDao.insertPendingSync(pendingSync)

                // Schedule worker
                val workRequest = OneTimeWorkRequestBuilder<CreateAgendaItemWorker>()
                    .setInputData(workDataOf(KEY_AGENDA_ITEM_ID to syncType.agendaItem.id))
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()
                    )
                    .setBackoffCriteria(
                        BackoffPolicy.EXPONENTIAL,
                        WorkRequest.MIN_BACKOFF_MILLIS,
                        TimeUnit.MILLISECONDS
                    )
                    .build()

                WorkManager.getInstance(context)
                    .enqueueUniqueWork(
                        "create_${syncType.agendaItem.id}",
                        ExistingWorkPolicy.REPLACE,
                        workRequest
                    )
            }
            is SyncAgendaScheduler.SyncType.PeriodicFetch -> {
                // Schedule periodic fetch
                val workRequest = PeriodicWorkRequestBuilder<PeriodicFetchAgendaWorker>(
                    syncType.interval.inWholeMinutes,
                    TimeUnit.MINUTES
                )
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()
                    )
                    .build()

                WorkManager.getInstance(context)
                    .enqueueUniquePeriodicWork(
                        "periodic_fetch_agenda",
                        ExistingPeriodicWorkPolicy.KEEP,
                        workRequest
                    )
            }
        }
    }
}
```

**5. Reschedule on Boot:**
Reschedule alarms after device reboot:

```kotlin
@HiltWorker
class RescheduleAlarmWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val localAgendaDataSource: LocalAgendaDataSource,
    private val alarmScheduler: AlarmScheduler
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val allItems = localAgendaDataSource.getAgendaItems().first()

        allItems.forEach { item ->
            val isTimeInFuture = item.remindAt.toInstant().toEpochMilli() >
                                 System.currentTimeMillis()
            if (isTimeInFuture) {
                alarmScheduler.schedule(item.toAlarmItem())
            }
        }

        return Result.success()
    }
}
```

**Key Benefits:**

- **Reliability**: AlarmManager.setExactAndAllowWhileIdle ensures timely delivery
- **Battery Efficient**: WorkManager batches work and respects system constraints
- **Doze Mode Compatible**: Alarms fire even in Doze
- **Persistence**: WorkManager guarantees task execution even after app restarts"

---

### 9. How do you approach testing in Android?

**Answer:**

"I structure tests at multiple levels to ensure comprehensive coverage:

**1. Unit Tests for ViewModels:**

```kotlin
class AgendaViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: AgendaViewModel
    private lateinit var fakeRepository: FakeAgendaRepository
    private lateinit var fakeSessionStorage: FakeSessionStorage

    @Before
    fun setup() {
        fakeRepository = FakeAgendaRepository()
        fakeSessionStorage = FakeSessionStorage()

        viewModel = AgendaViewModel(
            sessionStorage = fakeSessionStorage,
            agendaRepository = fakeRepository,
            savedStateHandle = SavedStateHandle(),
            syncAgendaScheduler = FakeSyncScheduler()
        )
    }

    @Test
    fun `when user logs out, should emit logout event`() = runTest {
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
        job.cancel()
    }

    @Test
    fun `when date changes, should update selected date in state`() = runTest {
        // Arrange
        val newDate = LocalDate.of(2024, 10, 19)

        // Act
        viewModel.onAction(AgendaScreenAction.OnDateChanged(newDate))

        // Assert
        assertEquals(newDate, viewModel.state.value.selectedDate)
    }
}
```

**2. Fake Implementations:**

```kotlin
class FakeAgendaRepository : AgendaRepository {
    private val agendaItems = MutableStateFlow<List<AgendaItem>>(emptyList())
    private val pendingItems = mutableListOf<AgendaItem>()

    var shouldReturnError = false

    override suspend fun createAgendaItem(agendaItem: AgendaItem): EmptyResult<DataError> {
        return if (shouldReturnError) {
            Result.Error(DataError.Network.SERVER_ERROR)
        } else {
            pendingItems.add(agendaItem)
            agendaItems.update { it + agendaItem }
            Result.Success(Unit)
        }
    }

    override fun getAgendaItemsByDate(date: LocalDate): Flow<List<AgendaItem>> {
        return agendaItems.map { items ->
            items.filter { it.time.toLocalDate() == date }
        }
    }

    // ... other methods
}
```

**3. Test Dispatcher Provider:**

```kotlin
object TestDispatcherProvider : DispatcherProvider {
    private val testDispatcher = UnconfinedTestDispatcher()

    override val main = testDispatcher
    override val io = testDispatcher
    override val default = testDispatcher
}

// MainDispatcherRule for ViewModels:
class MainDispatcherRule(
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
```

**4. Repository Tests:**

```kotlin
class OfflineFirstAgendaRepositoryTest {

    private lateinit var repository: OfflineFirstAgendaRepository
    private lateinit var fakeRemoteDataSource: FakeRemoteAgendaDataSource
    private lateinit var fakeLocalDataSource: FakeLocalAgendaDataSource

    @Test
    fun `when create item offline, should save locally and queue for sync`() = runTest {
        // Arrange
        val agendaItem = createTestEvent()
        fakeRemoteDataSource.shouldReturnError = true

        // Act
        val result = repository.createAgendaItem(agendaItem)

        // Assert
        assertTrue(result is Result.Success)
        assertTrue(fakeLocalDataSource.contains(agendaItem))
        assertTrue(fakeSyncScheduler.hasPendingSync(agendaItem.id))
    }
}
```

**5. UI Tests with Compose:**

```kotlin
class AgendaScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when agenda items loaded, should display items`() {
        // Arrange
        val testItems = listOf(
            createTestEvent("Meeting"),
            createTestTask("Buy groceries")
        )

        // Act
        composeTestRule.setContent {
            TaskyTheme {
                AgendaList(agendaItems = testItems)
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Meeting").assertIsDisplayed()
        composeTestRule.onNodeWithText("Buy groceries").assertIsDisplayed()
    }

    @Test
    fun `when delete button clicked, should show confirmation dialog`() {
        // Arrange & Act
        composeTestRule.setContent {
            /* ... */
        }

        composeTestRule.onNodeWithContentDescription("Delete").performClick()

        // Assert
        composeTestRule.onNodeWithText("Delete agenda item?").assertIsDisplayed()
    }
}
```

**6. Testing Flows:**

```kotlin
@Test
fun `repository emits updated items when database changes`() = runTest {
    // Arrange
    val date = LocalDate.now()
    val items = mutableListOf<List<AgendaItem>>()

    val job = launch {
        repository.getAgendaItemsByDate(date).collect {
            items.add(it)
        }
    }

    // Act
    repository.createAgendaItem(createTestEvent())
    advanceUntilIdle()

    // Assert
    assertEquals(2, items.size) // Initial empty + after insert
    assertEquals(1, items.last().size)

    job.cancel()
}
```

**7. Testing Coroutines:**

```kotlin
@Test
fun `when parallel operations execute, should complete all`() = runTest {
    // Arrange
    val items = List(10) { createTestEvent(it.toString()) }

    // Act
    val results = coroutineScope {
        items.map { item ->
            async {
                repository.createAgendaItem(item)
            }
        }.awaitAll()
    }

    // Assert
    assertTrue(results.all { it is Result.Success })
    assertEquals(10, fakeLocalDataSource.getCount())
}
```

**Testing Strategy:**

- **Unit Tests**: ViewModels, repositories, domain logic
- **Integration Tests**: Database operations, network calls
- **UI Tests**: Critical user flows
- **Fake Implementations**: Prefer over mocks for better maintainability

**Key Benefits:**

- **Fast**: Unit tests run in milliseconds
- **Reliable**: Deterministic with TestDispatchers
- **Maintainable**: Fakes are reusable across tests
- **Comprehensive**: Cover state transitions, error cases, and async flows"

---

### 10. How do you handle configuration changes and process death?

**Answer:**

"I use a combination of ViewModel, SavedStateHandle, and persistent storage:

**1. ViewModel Survives Configuration Changes:**
ViewModels automatically survive configuration changes:

```kotlin
@HiltViewModel
class AgendaViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val agendaRepository: AgendaRepository
): ViewModel() {

    // State survives rotation automatically
    private val _state = MutableStateFlow(AgendaScreenState())
    val state = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = _state.value
    )
}
```

**2. SavedStateHandle for Process Death:**
For UI state that needs to survive process death:

```kotlin
@HiltViewModel
class AgendaViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    // ...
): ViewModel() {

    // Restore from saved state if available
    private val selectedDateTimeStamp = savedStateHandle.get<Long>("selectedDate")
    private val _state = MutableStateFlow(AgendaScreenState(
        selectedDate = selectedDateTimeStamp?.let { parseTimestampToLocalDate(it) }
            ?: LocalDate.now()
    ))

    // Save to SavedStateHandle when it changes
    private fun observeSelectedDateChanges() {
        viewModelScope.launch {
            _state.mapNotNull { it.selectedDate }
                .distinctUntilChanged()
                .onEach { selectedDate ->
                    savedStateHandle["selectedDate"] = parseLocalDateToTimestamp(selectedDate)
                }
                .launchIn(viewModelScope)
        }
    }
}
```

**3. DataStore for Session Data:**
Critical data like auth tokens are in encrypted DataStore:

```kotlin
class SessionStorageSerializer(
    private val userDataStore: DataStore<AuthenticatedUserSerializable>
) : SessionStorage {

    override suspend fun saveSession(user: AuthenticatedUser) {
        userDataStore.updateData { current ->
            AuthenticatedUserSerializable(
                userId = user.userId,
                fullName = user.fullName,
                accessToken = user.accessToken,
                refreshToken = user.refreshToken,
                accessTokenExpirationTimestamp = user.accessTokenExpirationTimestamp
            )
        }
    }

    override suspend fun getSession(): AuthenticatedUser? {
        return userDataStore.data
            .map { it.toAuthenticatedUser() }
            .firstOrNull()
    }
}
```

**4. Room Database for Data:**
All user data is in Room, automatically survives process death:

```kotlin
override fun getAgendaItemsByDate(date: LocalDate): Flow<List<AgendaItem>> {
    // Database persists across process death
    return localAgendaSource.getAgendaItemsByDate(date)
}
```

**5. WorkManager for Background Tasks:**
WorkManager persists scheduled work across process death:

```kotlin
val workRequest = OneTimeWorkRequestBuilder<CreateAgendaItemWorker>()
    .setInputData(workDataOf(KEY_AGENDA_ITEM_ID to agendaItem.id))
    .build()

WorkManager.getInstance(context).enqueue(workRequest)
// This work will resume after process death
```

**6. WhileSubscribed with Timeout:**
The 5-second timeout in StateFlow prevents unnecessary work during configuration changes:

```kotlin
val state = _state
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),  // 5s grace period
        initialValue = _state.value
    )
```

During rotation, the new Activity subscribes within 5 seconds, so the Flow doesn't stop and restart.

**7. Compose Navigation with SavedState:**
Navigation state is saved automatically:

```kotlin
val navController = rememberNavController()
// NavController handles its own SavedState
```

**Testing Process Death:**
I test process death scenarios using:

- \"Don't keep activities\" developer option
- Background process limit in developer options
- Manual process kill via `adb shell am kill`

**Key Benefits:**

- **Seamless UX**: No data loss during rotation
- **Survives Process Death**: Critical state persisted
- **Battery Efficient**: WhileSubscribed stops work when not needed
- **Secure**: Encrypted storage for sensitive data"

---

## Key Talking Points for Your Interview

### Technical Strengths to Highlight:

1. **Modern Stack Alignment**: "My Tasky app uses the exact stack Neo requires: Kotlin, Jetpack Compose, Coroutines, Flows, and Hilt. I'm productive on day one."

2. **Production-Ready Code**: "Tasky isn't a tutorial app—it handles real-world challenges like offline-first architecture, data synchronization, background work, and reliable notifications."

3. **Architecture Experience**: "I've implemented Clean Architecture with feature-based modulari zation, which scales well for teams. Each feature is self-contained with clear layer separation."

4. **Problem-Solving**: "When implementing offline-first, I had to solve complex challenges like conflict resolution, pending sync queues, and ensuring data consistency across devices."

5. **Testing Mindset**: "I design for testability from the start—dependency injection, interface abstractions, and fake implementations make testing straightforward."

6. **Performance Awareness**: "In Compose, I optimize recompositions using stable keys, remember, derivedStateOf, and lazy lists for large datasets."

7. **Code Quality**: "I use type-safe state management, immutable data structures, sealed interfaces for exhaustive when statements, and structured logging for maintainability."

8. **Continuous Learning**: "I stay current with Android updates—I've implemented Material 3, the latest Compose patterns, Room schema migrations, and Android 13+ notification permissions."

### Questions to Ask Neo:

1. "What does the code review process look like at Neo? How do you balance velocity with code quality?"

2. "How do you approach technical debt? Is there dedicated time for refactoring?"

3. "What's the team structure for Android development? How do Android, iOS, and backend teams collaborate?"

4. "What's your approach to testing? What's the current test coverage, and what are the goals?"

5. "How do you handle GraphQL on Android? Are you using Apollo or a custom solution?"

6. "What CI/CD tools do you use? What's the deployment frequency?"

7. "How do you use GitHub Copilot in the team? Any guidelines or best practices?"

8. "What are the biggest technical challenges the Android team is currently facing?"

9. "How does Neo approach innovation? Is there time allocated for exploring new technologies or patterns?"

10. "What does the onboarding process look like for new Android developers?"

### Behavioral Responses:

**High-Pressure Environment:**
"I thrive in fast-paced environments. In Tasky, I managed the complexity of offline-first architecture, background sync, notifications, and a polished UI. Breaking big problems into smaller, manageable pieces helps me maintain velocity without sacrificing quality."

**Team Collaboration:**
"I value code reviews as learning opportunities. I'm comfortable giving and receiving feedback. In reviews, I focus on architecture, testing, and maintainability—not just syntax."

**Growth Mindset:**
"I'm always learning. For Tasky, I deepened my knowledge of WorkManager, AlarmManager, and advanced Flow operators. At Neo, I'm excited to learn GraphQL and contribute to a production app with millions of users."

**Customer Focus:**
"Everything in Tasky is designed for user experience—instant offline responses, reliable notifications, and smooth UI interactions. I align technical decisions with user needs."

---

## Summary

You have a **strong portfolio** with Tasky that directly aligns with Neo's requirements. Your experience with:

- ✅ **Kotlin** with modern idioms
- ✅ **Jetpack Compose** with custom components
- ✅ **Clean Architecture** with MVVM/MVI
- ✅ **Hilt** for dependency injection
- ✅ **Coroutines & Flows** for async operations
- ✅ **Room** for local persistence
- ✅ **Retrofit** for networking
- ✅ **WorkManager** for background sync
- ✅ **Complex state management**
- ✅ **Production-ready features** (offline-first, auth, notifications)

...demonstrates you can contribute immediately as an intermediate Android developer.

**Confidence**: Walk into the interview knowing you've solved real, production-level problems. Tasky is a solid foundation that showcases your abilities.

Good luck with your interview! 🚀
