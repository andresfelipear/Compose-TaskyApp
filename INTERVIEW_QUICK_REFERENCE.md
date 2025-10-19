# Interview Quick Reference - Tasky App

## 30-Second Elevator Pitch

"Tasky is a production-ready, **offline-first agenda management app** I built using **Kotlin and Jetpack Compose**. It features full CRUD operations for events, tasks, and reminders with **intelligent background synchronization** using WorkManager, **secure JWT authentication**, and **reliable notifications**. The architecture follows **Clean Architecture principles** with feature-based modularization, **Hilt for DI**, **Room for persistence**, and **Coroutines/Flows** for reactive data. It demonstrates real-world challenges like conflict resolution, process death handling, and cross-device sync."

---

## Tech Stack at a Glance

| Category            | Technology                               |
| ------------------- | ---------------------------------------- |
| **Language**        | 100% Kotlin                              |
| **UI**              | Jetpack Compose + Material 3             |
| **Architecture**    | Clean Architecture (MVVM + MVI patterns) |
| **DI**              | Dagger Hilt                              |
| **Database**        | Room (5 schema versions)                 |
| **Networking**      | Retrofit + OkHttp + Moshi                |
| **Async**           | Coroutines + Flow + StateFlow + Channel  |
| **Background Work** | WorkManager                              |
| **Notifications**   | AlarmManager (setExactAndAllowWhileIdle) |
| **State Storage**   | Encrypted DataStore                      |
| **Image Loading**   | Coil 3                                   |
| **Logging**         | Timber                                   |
| **Navigation**      | Jetpack Navigation Compose               |
| **Testing**         | JUnit + Coroutines Test                  |

---

## Architecture - 3 Layers

```
┌─────────────────────────────────────┐
│     PRESENTATION LAYER              │
│  • Composables (Views)              │
│  • ViewModels                       │
│  • StateFlow<ViewState>             │
│  • Channel<OneTimeEvent>            │
│  • Sealed Interface Actions         │
└─────────────────────────────────────┘
              ↓ ↑
┌─────────────────────────────────────┐
│       DOMAIN LAYER                  │
│  • Repository Interfaces            │
│  • Domain Models                    │
│  • Business Rules                   │
└─────────────────────────────────────┘
              ↓ ↑
┌─────────────────────────────────────┐
│        DATA LAYER                   │
│  • Repository Implementations       │
│  • Remote DataSource (Retrofit)     │
│  • Local DataSource (Room)          │
│  • Mappers (DTO ↔ Entity ↔ Domain) │
└─────────────────────────────────────┘
```

---

## State Management Pattern

```kotlin
// 1. Single immutable state
data class AgendaScreenState(
    val selectedDate: LocalDate,
    val agendaItems: List<AgendaItem> = emptyList(),
    val isLoading: Boolean = false
)

// 2. StateFlow with lifecycle-aware collection
val state = _state.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = _state.value
)

// 3. User actions as sealed interface
sealed interface AgendaScreenAction {
    data class OnDateChanged(val date: LocalDate) : AgendaScreenAction
}

// 4. One-time events via Channel
private val eventChannel = Channel<AgendaScreenEvent>()
val event = eventChannel.receiveAsFlow()

// 5. Safe state updates
_state.update { it.copy(isLoading = true) }
```

---

## Offline-First Strategy

1. **Local First**: Room database serves all data instantly
2. **Optimistic Updates**: Save locally immediately
3. **Pending Sync Queue**: Track changes in `PendingItemSyncEntity`
4. **WorkManager Sync**: Dedicated workers per operation (Create/Update/Delete)
5. **Retry Logic**: Exponential backoff on failures
6. **Periodic Fetch**: Every 30 minutes, fetch remote updates
7. **Conflict Resolution**: Backend is source of truth

```kotlin
// User creates item
override suspend fun createAgendaItem(item: AgendaItem): EmptyResult<DataError> {
    // 1. Save locally (instant feedback)
    localAgendaSource.insertAgendaItem(item)

    // 2. Schedule reminder
    scheduleReminder(item)

    // 3. Queue background sync
    applicationScope.launch {
        syncAgendaScheduler.scheduleSyncAgenda(
            syncType = SyncAgendaScheduler.SyncType.CreateItem(item)
        )
    }

    return Result.Success(Unit)
}
```

---

## Key Flow Patterns

```kotlin
// flatMapLatest - switch streams when input changes
_state.mapNotNull { it.selectedDate }
    .distinctUntilChanged()
    .flatMapLatest { date ->
        repository.getAgendaItemsByDate(date)
    }
    .onEach { items ->
        _state.update { it.copy(agendaItems = items) }
    }
    .launchIn(viewModelScope)

// combine - merge multiple flows
combine(
    eventDao.getEventsByDate(date),
    taskDao.getTasksByDate(date),
    reminderDao.getRemindersByDate(date)
) { events, tasks, reminders ->
    (events + tasks + reminders).sortedBy { it.time }
}
```

---

## Dependency Injection Highlights

```kotlin
// Module organization
@Module
@InstallIn(SingletonComponent::class)
object TaskyAgendaModule {

    // Qualifiers for multiple bindings
    @Provides
    @Singleton
    @Named("authenticated")
    fun provideOkHttpClient(
        authTokenInterceptor: AuthTokenInterceptor,
        apiKeyInterceptor: ApiKeyInterceptor
    ): OkHttpClient { /* ... */ }

    // Interface to implementation
    @Provides
    @Singleton
    fun provideAgendaRepository(
        remoteDataSource: RemoteAgendaDataSource,
        localDataSource: LocalAgendaDataSource,
        // ... other deps
    ): AgendaRepository {
        return OfflineFirstAgendaRepository(/* ... */)
    }
}

// ViewModel injection
@HiltViewModel
class AgendaViewModel @Inject constructor(
    private val agendaRepository: AgendaRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel()

// WorkManager injection
@HiltWorker
class CreateAgendaItemWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val remoteDataSource: RemoteAgendaDataSource
) : CoroutineWorker(context, params)
```

---

## Compose Best Practices

```kotlin
// Stateless composables
@Composable
fun AgendaItem(
    agendaItem: AgendaItem,
    onEditClick: (String) -> Unit,
    modifier: Modifier = Modifier
) { /* ... */ }

// Collect state safely
@Composable
fun AgendaScreen(viewModel: AgendaViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when(event) {
                is AgendaScreenEvent.Error -> { /* ... */ }
            }
        }
    }
}

// Performance - stable keys in lists
LazyColumn {
    items(
        items = agendaItems,
        key = { it.id }  // Stable key
    ) { item ->
        AgendaItem(agendaItem = item)
    }
}

// Derived state
val groupedItems by remember(agendaItems) {
    derivedStateOf {
        agendaItems.groupBy { it.type }
    }
}
```

---

## Testing Strategy

```kotlin
// ViewModel test with fake repository
@Test
fun `when date changes, should update selected date`() = runTest {
    // Arrange
    val viewModel = AgendaViewModel(
        agendaRepository = FakeAgendaRepository(),
        sessionStorage = FakeSessionStorage(),
        savedStateHandle = SavedStateHandle()
    )

    // Act
    val newDate = LocalDate.of(2024, 10, 19)
    viewModel.onAction(AgendaScreenAction.OnDateChanged(newDate))

    // Assert
    assertEquals(newDate, viewModel.state.value.selectedDate)
}

// Fake implementation
class FakeAgendaRepository : AgendaRepository {
    private val items = MutableStateFlow<List<AgendaItem>>(emptyList())
    var shouldReturnError = false

    override fun getAgendaItemsByDate(date: LocalDate): Flow<List<AgendaItem>> {
        return items.map { it.filter { item -> item.date == date } }
    }
}

// TestDispatcherProvider
object TestDispatcherProvider : DispatcherProvider {
    private val testDispatcher = UnconfinedTestDispatcher()
    override val main = testDispatcher
    override val io = testDispatcher
    override val default = testDispatcher
}
```

---

## Background Work & Notifications

```kotlin
// AlarmManager for exact timing
alarmManager.setExactAndAllowWhileIdle(
    AlarmManager.RTC_WAKEUP,
    alarmItem.time,
    pendingIntent
)

// Smart scheduling (future times only)
val isTimeInFuture = reminderTimeMillis > currentTimeMillis
if (isTimeInFuture) {
    alarmScheduler.schedule(agendaItem.toAlarmItem())
}

// WorkManager with constraints and backoff
val workRequest = OneTimeWorkRequestBuilder<CreateAgendaItemWorker>()
    .setInputData(workDataOf(KEY_AGENDA_ITEM_ID to itemId))
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
    .enqueueUniqueWork("create_$itemId", ExistingWorkPolicy.REPLACE, workRequest)
```

---

## Configuration Changes & Process Death

```kotlin
// ViewModel survives config changes automatically
@HiltViewModel
class AgendaViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,  // Survives process death
    private val agendaRepository: AgendaRepository
): ViewModel()

// Restore from SavedStateHandle
private val selectedDateTimeStamp = savedStateHandle.get<Long>("selectedDate")
private val _state = MutableStateFlow(AgendaScreenState(
    selectedDate = selectedDateTimeStamp?.let { parseTimestampToLocalDate(it) }
        ?: LocalDate.now()
))

// Save to SavedStateHandle on changes
_state.mapNotNull { it.selectedDate }
    .distinctUntilChanged()
    .onEach { selectedDate ->
        savedStateHandle["selectedDate"] = parseLocalDateToTimestamp(selectedDate)
    }
    .launchIn(viewModelScope)

// WhileSubscribed with timeout prevents restarts during config changes
val state = _state.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),  // 5s grace period
    initialValue = _state.value
)
```

---

## Key Features Implemented

✅ **Offline-First Architecture**  
✅ **JWT Auth with Refresh Tokens**  
✅ **Background Sync with WorkManager**  
✅ **Exact Alarms with AlarmManager**  
✅ **Encrypted DataStore for Sessions**  
✅ **Room Database with Migrations**  
✅ **Reactive UI with Flows**  
✅ **Material 3 Theming (Light/Dark)**  
✅ **Process Death Handling**  
✅ **Custom Interceptors for Auth**  
✅ **Photo Upload with Multipart**  
✅ **Conflict Resolution Strategy**  
✅ **Cross-Device Sync**  
✅ **Attendee Management**  
✅ **Structured Logging with Timber**

---

## Challenges Solved

1. **Offline Sync**: Implemented pending sync queue with WorkManager retry logic
2. **Conflict Resolution**: Backend as source of truth with last-write-wins for updates
3. **Notification Reliability**: Used setExactAndAllowWhileIdle for Doze mode compatibility
4. **State Management**: Unidirectional data flow with separation of state and one-time events
5. **Process Death**: SavedStateHandle + DataStore + Room for complete state persistence
6. **Auth Token Refresh**: Custom interceptor with automatic token refresh on 401
7. **Performance**: LazyColumn, derivedStateOf, stable keys, WhileSubscribed optimization
8. **Memory Leaks**: Structured concurrency with viewModelScope and lifecycle-aware collection

---

## Questions to Ask Neo

1. Code review process and team collaboration
2. GraphQL implementation on Android
3. CI/CD pipeline and deployment frequency
4. Current technical challenges
5. Testing strategy and coverage goals
6. GitHub Copilot usage guidelines
7. Onboarding process for new devs
8. Approach to technical debt

---

## Confidence Boosters

✅ **Stack Match**: 100% alignment with Neo's tech stack  
✅ **Production-Ready**: Real-world features, not tutorial code  
✅ **Architecture**: Scalable Clean Architecture with team-friendly modularity  
✅ **Testing**: Designed for testability from the ground up  
✅ **Performance**: Optimized for battery, memory, and UI responsiveness  
✅ **Modern Patterns**: Latest Compose, Flow operators, and Kotlin idioms  
✅ **Complex Problem-Solving**: Offline-first, sync, notifications—not trivial features

---

## Final Reminders

- **Be Specific**: Reference actual code from Tasky when answering questions
- **Show, Don't Tell**: Pull up the codebase if needed to demonstrate
- **Problem → Solution**: Frame answers as challenges you solved
- **Growth Mindset**: Express excitement about learning GraphQL at Neo
- **Team Player**: Emphasize code review, collaboration, and maintainability
- **Customer Focus**: Connect technical decisions to user experience

**You've got this!** 🚀
