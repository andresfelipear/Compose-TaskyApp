# Code Examples Reference - For Interview

Quick reference of actual code from Tasky that demonstrates key concepts.

---

## 1. ViewModel with State Management

**File**: `AgendaViewModel.kt`

```kotlin
@HiltViewModel
class AgendaViewModel @Inject constructor(
    private val sessionStorage: SessionStorage,
    private val agendaRepository: AgendaRepository,
    private val savedStateHandle: SavedStateHandle,
    private val syncAgendaScheduler: SyncAgendaScheduler
): ViewModel() {

    // Restore from SavedState if available (survives process death)
    private val selectedDateTimeStamp = savedStateHandle.get<Long>("selectedDate")

    // Mutable state (private)
    private val _state = MutableStateFlow(AgendaScreenState(
        selectedDate = selectedDateTimeStamp?.let { parseTimestampToLocalDate(it) }
            ?: LocalDate.now()
    ))

    // Public immutable state with lifecycle handling
    val state = _state
        .onStart {
            loadInitialData()
            observeSelectedDateChanges()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),  // 5s timeout
            initialValue = _state.value
        )

    // One-time events channel
    private val eventChannel = Channel<AgendaScreenEvent>()
    val event = eventChannel.receiveAsFlow()

    // Handle user actions
    fun onAction(action: AgendaScreenAction) {
        when(action) {
            is AgendaScreenAction.OnDateChanged -> {
                _state.update { currentState ->
                    currentState.copy(
                        selectedDate = action.date,
                        relatedDates = getRelatedDates(action.date)
                    )
                }
            }

            is AgendaScreenAction.OnDeleteAgendaItem -> {
                _state.update { it.copy(isDeletingItem = true) }

                viewModelScope.launch {
                    val result = agendaRepository.deleteAgendaItem(
                        _state.value.agendaItemIdToDelete,
                        agendaItem.type
                    )

                    _state.update { it.copy(isDeletingItem = false) }

                    when(result) {
                        is Result.Error -> {
                            eventChannel.send(
                                AgendaScreenEvent.Error(result.error.asUiText())
                            )
                        }
                        is Result.Success -> {
                            eventChannel.send(AgendaScreenEvent.SuccessDeleteAgendaItem)
                        }
                    }
                }
            }
        }
    }

    // Reactive data flow - switch streams when date changes
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeSelectedDateChanges() {
        viewModelScope.launch {
            _state.mapNotNull { it.selectedDate }
                .distinctUntilChanged()
                .onEach { selectedDate ->
                    // Save to SavedStateHandle for process death
                    savedStateHandle["selectedDate"] = parseLocalDateToTimestamp(selectedDate)
                }
                .flatMapLatest { selectedDate ->
                    // Cancel previous flow, start new one
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
}
```

**Key Points to Mention:**

- StateFlow with WhileSubscribed for lifecycle awareness
- SavedStateHandle for process death recovery
- Channel for one-time events (navigation, errors)
- Sealed interfaces for type-safe actions
- flatMapLatest for reactive data switching

---

## 2. State, Action, and Event Definitions

**Files**: `AgendaScreenState.kt`, `AgendaScreenAction.kt`, `AgendaScreenEvent.kt`

```kotlin
// Immutable state - single source of truth
data class AgendaScreenState(
    val selectedDate: LocalDate,
    val initials: String = "",
    val showDatePicker: Boolean = false,
    val relatedDates: List<LocalDate> = getRelatedDates(LocalDate.now()),
    val agendaItems: List<AgendaItem> = emptyList(),
    val isDeletingItem: Boolean = false,
    val showDeleteConfirmationDialog: Boolean = false,
    val agendaItemTypeToDelete: String = "",
    val agendaItemIdToDelete: String = ""
)

// User actions - what the user can do
sealed interface AgendaScreenAction {
    data class OnDateChanged(val date: LocalDate) : AgendaScreenAction
    data object OnChangeDatePickerVisibility : AgendaScreenAction
    data class OnDateSelectedCalendar(val date: LocalDate) : AgendaScreenAction
    data object OnLogout : AgendaScreenAction
    data class OnConfirmDeleteAgendaItem(
        val agendaItemId: String,
        val type: AgendaItemDetails
    ) : AgendaScreenAction
    data object OnDeleteAgendaItem : AgendaScreenAction
    data class OnChangeTaskStatus(val agendaItemId: String) : AgendaScreenAction
}

// One-time events - things that happen once
sealed interface AgendaScreenEvent {
    data object Success : AgendaScreenEvent
    data object SuccessLogout : AgendaScreenEvent
    data object SuccessDeleteAgendaItem : AgendaScreenEvent
    data object GoingBackToLoginScreen : AgendaScreenEvent
    data class Error(val errorMessage: UiText) : AgendaScreenEvent
}
```

**Key Points:**

- Immutable state with all UI properties
- Sealed interfaces for compile-time safety
- Separation of state (persistent) vs events (one-time)

---

## 3. Offline-First Repository

**File**: `OfflineFirstAgendaRepository.kt`

```kotlin
class OfflineFirstAgendaRepository @Inject constructor(
    private val remoteAgendaSource: RemoteAgendaDataSource,
    private val localAgendaSource: LocalAgendaDataSource,
    private val sessionStorage: SessionStorage,
    private val applicationScope: CoroutineScope,
    private val pendingItemSyncDao: PendingItemSyncDao,
    private val syncAgendaScheduler: SyncAgendaScheduler,
    private val agendaItemJsonConverter: AgendaItemJsonConverter,
    private val dispatcher: DispatcherProvider,
    private val alarmScheduler: AlarmScheduler
): AgendaRepository {

    // Always return local data - instant response
    override fun getAgendaItemsByDate(date: LocalDate): Flow<List<AgendaItem>> {
        return localAgendaSource.getAgendaItemsByDate(date)
    }

    // Create: Local first, then background sync
    override suspend fun createAgendaItem(agendaItem: AgendaItem): EmptyResult<DataError> {
        return withContext(dispatcher.io) {
            try {
                // 1. Save to local database immediately
                localAgendaSource.insertAgendaItem(agendaItem)

                // 2. Schedule reminder
                scheduleReminder(agendaItem)

                // 3. Try remote sync (but don't fail if offline)
                val remoteResult = remoteAgendaSource.createAgendaItem(agendaItem)

                when(remoteResult) {
                    is Result.Error -> {
                        // Failed - queue for later sync
                        Timber.w("Remote create failed. Queuing for sync.")
                        applicationScope.launch {
                            syncAgendaScheduler.scheduleSyncAgenda(
                                syncType = SyncAgendaScheduler.SyncType.CreateItem(agendaItem)
                            )
                        }
                    }
                    is Result.Success -> {
                        Timber.d("Successfully created agenda item remotely")
                    }
                }

                // Return success regardless of remote status
                Result.Success(Unit)

            } catch (e: SQLException) {
                Timber.e(e, "Local database error")
                Result.Error(DataError.Local.DISK_FULL)
            }
        }
    }

    // Fetch from remote and merge with local
    override suspend fun fetchAgendaItems(): EmptyResult<DataError> {
        val currentUserId = sessionStorage.getSession()?.userId
        if (currentUserId == null) {
            return Result.Error(DataError.Network.UNAUTHORIZED).asEmptyDataResult()
        }

        return when(val result = remoteAgendaSource.fetchFullAgenda()) {
            is Result.Error -> {
                Timber.e("Error fetching agenda items remotely!")
                result.asEmptyDataResult()
            }
            is Result.Success -> {
                // Upsert to local database
                localAgendaSource.upsertAgendaItems(result.data)

                // Schedule reminders for future items
                result.data.forEach { item ->
                    scheduleReminder(item)
                }

                Timber.d("Successfully fetched and synced ${result.data.size} items")
                Result.Success(Unit)
            }
        }
    }

    // Sync all pending items
    override suspend fun syncPendingAgendaItems() {
        withContext(dispatcher.io) {
            val pendingSyncs = pendingItemSyncDao.getAllPendingSyncs()

            Timber.d("Syncing ${pendingSyncs.size} pending items")

            pendingSyncs.forEach { pendingSync ->
                applicationScope.launch {
                    when(pendingSync.operation) {
                        SyncOperation.CREATE -> {
                            syncAgendaScheduler.scheduleSyncAgenda(
                                SyncAgendaScheduler.SyncType.CreateItem(
                                    agendaItemJsonConverter.deserialize(pendingSync.agendaItemJson)
                                )
                            )
                        }
                        SyncOperation.UPDATE -> {
                            // Similar logic for update
                        }
                        SyncOperation.DELETE -> {
                            // Similar logic for delete
                        }
                    }
                }
            }
        }
    }

    // Smart reminder scheduling - only future times
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
            Timber.w("Skipping scheduling - reminder time is in the past")
            alarmScheduler.cancel(agendaItem.toAlarmItem())
        }
    }
}
```

**Key Points:**

- Local-first strategy for instant UI response
- Optimistic updates with background sync
- Error handling doesn't block user actions
- Coroutine dispatchers for proper thread management
- Application scope for work that outlives ViewModel

---

## 4. Hilt Dependency Injection

**File**: `TaskyAgendaModule.kt`

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object TaskyAgendaModule {

    // Retrofit API with custom OkHttpClient
    @Provides
    @Singleton
    fun provideAgendaApi(
        moshi: Moshi,
        @Named("authenticated") client: OkHttpClient  // Uses qualifier
    ): TaskyAgendaApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()
            .create()
    }

    // Room database
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AgendaDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AgendaDatabase::class.java,
            "agenda_database"
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    // DAOs from database
    @Provides
    @Singleton
    fun provideEventDao(database: AgendaDatabase): EventDao = database.eventDao

    @Provides
    @Singleton
    fun provideTaskDao(database: AgendaDatabase): TaskDao = database.taskDao

    // Data sources
    @Provides
    @Singleton
    fun provideLocalAgendaDataSource(
        database: AgendaDatabase,
        eventDao: EventDao,
        taskDao: TaskDao,
        reminderDao: ReminderDao,
        attendeeDao: AttendeeDao,
        photoDao: PhotoDao
    ): LocalAgendaDataSource = RoomLocalAgendaDataSource(
        database, eventDao, taskDao, reminderDao, attendeeDao, photoDao
    )

    @Provides
    @Singleton
    fun provideRemoteAgendaDataSource(
        api: TaskyAgendaApi,
        photoByteLoader: PhotoByteLoader
    ): RemoteAgendaDataSource = RetrofitRemoteAgendaDataSource(api, photoByteLoader)

    // Repository: interface to implementation
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

    // Application-scoped coroutine scope
    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope {
        return CoroutineScope(Dispatchers.IO)
    }
}
```

**File**: `AppModule.kt`

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Encrypted DataStore for session
    private val Context.userDataStore by dataStore(
        fileName = "user_preferences.json",
        serializer = EncryptAuthenticatedUser,
        corruptionHandler = ReplaceFileCorruptionHandler(
            produceNewData = { AuthenticatedUserSerializable() }
        )
    )

    @Singleton
    @Provides
    fun provideSessionStorage(
        userDataStore: DataStore<AuthenticatedUserSerializable>
    ): SessionStorage = SessionStorageSerializer(userDataStore)

    // Two OkHttpClients with qualifiers
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

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }
}
```

**Key Points:**

- @Named qualifiers for multiple bindings of same type
- Singleton scope for app-lifetime dependencies
- Interface → Implementation binding
- Application scope CoroutineScope for background work

---

## 5. WorkManager with Hilt

**File**: `CreateAgendaItemWorker.kt`

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
        val itemId = inputData.getString(KEY_AGENDA_ITEM_ID)
            ?: return Result.failure()

        Timber.d("Starting create sync for item: $itemId")

        // Get pending sync item from database
        val pendingSync = pendingItemSyncDao.getPendingSyncById(itemId)
        if (pendingSync == null) {
            Timber.w("Pending sync not found for item: $itemId")
            return Result.success() // Already synced
        }

        // Deserialize the agenda item
        val agendaItem = agendaItemJsonConverter.deserialize(pendingSync.agendaItemJson)

        // Try to create remotely
        return when(val result = remoteAgendaDataSource.createAgendaItem(agendaItem)) {
            is Result.Error -> {
                Timber.e("Remote create failed: ${result.error}. Will retry.")
                Result.retry()  // WorkManager will retry with backoff
            }
            is Result.Success -> {
                // Success - remove from pending queue
                pendingItemSyncDao.deletePendingSync(itemId)
                Timber.d("Successfully synced create for item: $itemId")
                Result.success()
            }
        }
    }

    companion object {
        const val KEY_AGENDA_ITEM_ID = "agenda_item_id"
    }
}
```

**File**: `SyncAgendaWorkerScheduler.kt`

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
                // Save to pending sync table
                val pendingSync = PendingItemSyncEntity(
                    id = UUID.randomUUID().toString(),
                    itemId = syncType.agendaItem.id,
                    itemType = syncType.agendaItem.type.name,
                    operation = SyncOperation.CREATE,
                    agendaItemJson = agendaItemJsonConverter.serialize(syncType.agendaItem),
                    timestamp = System.currentTimeMillis()
                )
                pendingItemSyncDao.insertPendingSync(pendingSync)

                // Schedule WorkManager job
                val workRequest = OneTimeWorkRequestBuilder<CreateAgendaItemWorker>()
                    .setInputData(workDataOf(
                        CreateAgendaItemWorker.KEY_AGENDA_ITEM_ID to syncType.agendaItem.id
                    ))
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

**Key Points:**

- @HiltWorker with @AssistedInject for dependency injection
- Pending sync queue in database
- Exponential backoff retry strategy
- Network constraints for battery efficiency
- Unique work names to avoid duplicates

---

## 6. Composable with State Collection

**File**: `AgendaScreen.kt` (simplified)

```kotlin
@Composable
fun AgendaScreen(
    viewModel: AgendaViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
    onNavigateToDetail: (String, AgendaItemType) -> Unit
) {
    // Collect state lifecycle-aware
    val state by viewModel.state.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Collect one-time events
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when(event) {
                is AgendaScreenEvent.Error -> {
                    snackbarHostState.showSnackbar(
                        message = event.errorMessage.asString(context)
                    )
                }
                is AgendaScreenEvent.SuccessLogout -> {
                    onNavigateToLogin()
                }
                is AgendaScreenEvent.GoingBackToLoginScreen -> {
                    onNavigateToLogin()
                }
                is AgendaScreenEvent.SuccessDeleteAgendaItem -> {
                    snackbarHostState.showSnackbar("Item deleted")
                }
                is AgendaScreenEvent.Success -> {
                    // Handle success
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            AddAgendaItemButton(
                onClick = { type ->
                    viewModel.onAction(
                        AgendaScreenAction.OnCreateAgendaItemClick(type)
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header with user initials
            AgendaScreenHeader(
                initials = state.initials,
                selectedDate = state.selectedDate,
                onDatePickerClick = {
                    viewModel.onAction(AgendaScreenAction.OnChangeDatePickerVisibility)
                },
                onLogoutClick = {
                    viewModel.onAction(AgendaScreenAction.OnLogout)
                }
            )

            // Calendar day selector
            CalendarDaysSelector(
                selectedDate = state.selectedDate,
                relatedDates = state.relatedDates,
                onDateSelected = { date ->
                    viewModel.onAction(AgendaScreenAction.OnDateChanged(date))
                }
            )

            // Agenda items list
            AgendaList(
                agendaItems = state.agendaItems,
                onItemClick = { id, type ->
                    onNavigateToDetail(id, type)
                },
                onEditClick = { id, type ->
                    viewModel.onAction(
                        AgendaScreenAction.OnEditAgendaItemClick(id, type)
                    )
                },
                onDeleteClick = { id, type ->
                    viewModel.onAction(
                        AgendaScreenAction.OnConfirmDeleteAgendaItem(id, type)
                    )
                }
            )
        }

        // Delete confirmation dialog
        if (state.showDeleteConfirmationDialog) {
            DeleteAgendaItemDialog(
                itemType = state.agendaItemTypeToDelete,
                isDeleting = state.isDeletingItem,
                onConfirm = {
                    viewModel.onAction(AgendaScreenAction.OnDeleteAgendaItem)
                },
                onDismiss = {
                    viewModel.onAction(AgendaScreenAction.OnChangeDeleteDialogVisibility)
                }
            )
        }
    }
}
```

**Key Points:**

- `collectAsStateWithLifecycle()` for safe state collection
- LaunchedEffect for one-time event collection
- Stateless composables receive state and callbacks
- Action dispatching through `onAction`

---

## 7. Flow Operators

**From**: `AgendaViewModel.kt` and `OfflineFirstAgendaRepository.kt`

```kotlin
// flatMapLatest - switch to new flow when upstream changes
@OptIn(ExperimentalCoroutinesApi::class)
private fun observeSelectedDateChanges() {
    viewModelScope.launch {
        _state.mapNotNull { it.selectedDate }  // Extract date from state
            .distinctUntilChanged()  // Only emit when date actually changes
            .onEach { selectedDate ->
                // Side effect: save to SavedStateHandle
                savedStateHandle["selectedDate"] = parseLocalDateToTimestamp(selectedDate)
            }
            .flatMapLatest { selectedDate ->
                // Cancel previous flow, start new one
                agendaRepository.getAgendaItemsByDate(selectedDate)
            }
            .onEach { agendaItems ->
                // Update state with new items
                _state.update { it.copy(agendaItems = agendaItems) }
            }
            .launchIn(viewModelScope)  // Collect in viewModelScope
    }
}

// combine - merge multiple flows
fun getAgendaItemsByDate(date: LocalDate): Flow<List<AgendaItem>> {
    val formattedDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE)

    return combine(
        eventDao.getEventsByDate(formattedDate),
        taskDao.getTasksByDate(formattedDate),
        reminderDao.getRemindersByDate(formattedDate)
    ) { events, tasks, reminders ->
        // Combine results from all three DAOs
        val eventItems = events.map { it.toDomainModel() }
        val taskItems = tasks.map { it.toDomainModel() }
        val reminderItems = reminders.map { it.toDomainModel() }

        // Return sorted combined list
        (eventItems + taskItems + reminderItems).sortedBy { it.time }
    }
}

// map - transform flow values
val groupedItems: Flow<Map<AgendaItemType, List<AgendaItem>>> =
    agendaRepository.getAllAgendaItems()
        .map { items ->
            items.groupBy { it.type }
        }

// filter - filter flow values
val upcomingItems = agendaRepository.getAllAgendaItems()
    .map { items ->
        items.filter { it.time.isAfter(ZonedDateTime.now()) }
    }
```

**Key Points:**

- `flatMapLatest` for switching data streams
- `combine` for merging multiple flows
- `distinctUntilChanged` to avoid redundant updates
- `mapNotNull` + `onEach` for side effects
- `launchIn` for controlled collection

---

## 8. Alarm Scheduling

**File**: `AlarmManagerAlarmScheduler.kt`

```kotlin
class AlarmManagerAlarmScheduler(
    private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(alarmItem: AlarmItem) {
        // Create intent for BroadcastReceiver
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("ALARM_ID", alarmItem.id)
            putExtra("ALARM_TITLE", alarmItem.title)
            putExtra("ALARM_DESCRIPTION", alarmItem.description)
            putExtra("ALARM_TYPE", alarmItem.type.name)
        }

        // Create PendingIntent with unique request code
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmItem.hashCode(),  // Unique per item
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Schedule exact alarm that works even in Doze mode
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmItem.time,  // Timestamp in millis
            pendingIntent
        )

        Timber.d("Alarm scheduled for ${alarmItem.title} at ${Date(alarmItem.time)}")
    }

    override fun cancel(alarmItem: AlarmItem) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmItem.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        Timber.d("Alarm cancelled for ${alarmItem.title}")
    }
}
```

**Key Points:**

- `setExactAndAllowWhileIdle` for Doze mode compatibility
- Unique request codes using hashCode
- PendingIntent.FLAG_IMMUTABLE for security
- RTC_WAKEUP to wake device

---

## 9. Custom Result Type

**File**: `Result.kt` (in core/domain/util)

```kotlin
sealed interface Result<out D, out E: Error> {
    data class Success<out D>(val data: D) : Result<D, Nothing>
    data class Error<out E: Error>(val error: E) : Result<Nothing, E>
}

typealias EmptyResult<E> = Result<Unit, E>

// Extension functions
fun <T, E: Error> Result<T, E>.asEmptyDataResult(): EmptyResult<E> {
    return when(this) {
        is Result.Error -> Result.Error(error)
        is Result.Success -> Result.Success(Unit)
    }
}

// Usage in repository
override suspend fun deleteAgendaItem(
    id: String,
    type: AgendaItemType
): EmptyResult<DataError> {
    return when(val result = remoteAgendaDataSource.deleteAgendaItem(id, type)) {
        is Result.Error -> {
            Timber.e("Delete failed: ${result.error}")
            result.asEmptyDataResult()
        }
        is Result.Success -> {
            localAgendaSource.deleteAgendaItem(id, type)
            cancelReminder(id, type)
            Result.Success(Unit)
        }
    }
}

// Error types
sealed interface DataError : Error {
    enum class Network : DataError {
        REQUEST_TIMEOUT,
        TOO_MANY_REQUESTS,
        NO_INTERNET,
        SERVER_ERROR,
        SERIALIZATION,
        UNAUTHORIZED,
        UNKNOWN
    }

    enum class Local : DataError {
        DISK_FULL,
        UNKNOWN
    }
}
```

**Key Points:**

- Type-safe error handling
- Explicit error cases
- No exceptions for expected failures
- Composable error types

---

## 10. Testing Example

**File**: `AgendaViewModelTest.kt` (example)

```kotlin
class AgendaViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: AgendaViewModel
    private lateinit var fakeRepository: FakeAgendaRepository
    private lateinit var fakeSessionStorage: FakeSessionStorage
    private lateinit var fakeSyncScheduler: FakeSyncScheduler

    @Before
    fun setup() {
        fakeRepository = FakeAgendaRepository()
        fakeSessionStorage = FakeSessionStorage()
        fakeSyncScheduler = FakeSyncScheduler()

        viewModel = AgendaViewModel(
            sessionStorage = fakeSessionStorage,
            agendaRepository = fakeRepository,
            savedStateHandle = SavedStateHandle(),
            syncAgendaScheduler = fakeSyncScheduler
        )
    }

    @Test
    fun `when date changes, should update selected date and load items`() = runTest {
        // Arrange
        val newDate = LocalDate.of(2024, 10, 19)
        val testItems = listOf(
            createTestEvent(date = newDate),
            createTestTask(date = newDate)
        )
        fakeRepository.setItemsForDate(newDate, testItems)

        // Act
        viewModel.onAction(AgendaScreenAction.OnDateChanged(newDate))
        advanceUntilIdle()  // Wait for coroutines

        // Assert
        assertEquals(newDate, viewModel.state.value.selectedDate)
        assertEquals(2, viewModel.state.value.agendaItems.size)
    }

    @Test
    fun `when delete action executed, should emit success event on success`() = runTest {
        // Arrange
        val itemId = "test-id"
        val events = mutableListOf<AgendaScreenEvent>()
        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.event.collect { events.add(it) }
        }

        // Prepare item for deletion
        viewModel.onAction(
            AgendaScreenAction.OnConfirmDeleteAgendaItem(itemId, AgendaItemType.EVENT)
        )

        // Act
        viewModel.onAction(AgendaScreenAction.OnDeleteAgendaItem)
        advanceUntilIdle()

        // Assert
        assertTrue(events.any { it is AgendaScreenEvent.SuccessDeleteAgendaItem })
        job.cancel()
    }

    @Test
    fun `when repository returns error, should emit error event`() = runTest {
        // Arrange
        fakeRepository.shouldReturnError = true
        val events = mutableListOf<AgendaScreenEvent>()
        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.event.collect { events.add(it) }
        }

        // Act
        viewModel.onAction(AgendaScreenAction.OnLogout)
        advanceUntilIdle()

        // Assert
        assertTrue(events.any { it is AgendaScreenEvent.Error })
        job.cancel()
    }
}

// Fake Repository
class FakeAgendaRepository : AgendaRepository {
    private val itemsByDate = mutableMapOf<LocalDate, MutableList<AgendaItem>>()
    var shouldReturnError = false

    fun setItemsForDate(date: LocalDate, items: List<AgendaItem>) {
        itemsByDate[date] = items.toMutableList()
    }

    override fun getAgendaItemsByDate(date: LocalDate): Flow<List<AgendaItem>> {
        return flow {
            emit(itemsByDate[date] ?: emptyList())
        }
    }

    override suspend fun deleteAgendaItem(
        id: String,
        type: AgendaItemType
    ): EmptyResult<DataError> {
        return if (shouldReturnError) {
            Result.Error(DataError.Network.SERVER_ERROR)
        } else {
            itemsByDate.values.forEach { it.removeIf { item -> item.id == id } }
            Result.Success(Unit)
        }
    }
}

// Test Dispatcher Rule
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

**Key Points:**

- Fake implementations over mocks
- `runTest` for coroutine testing
- `advanceUntilIdle()` to wait for async work
- `MainDispatcherRule` for ViewModel tests
- Event collection testing with background job

---

## Quick Reference During Interview

When asked about specific topics, you can quickly reference:

1. **State Management** → Example #1, #2
2. **Architecture** → Example #3, #4
3. **Dependency Injection** → Example #4
4. **Background Work** → Example #5
5. **Compose** → Example #6
6. **Flows** → Example #7
7. **Notifications** → Example #8
8. **Error Handling** → Example #9
9. **Testing** → Example #10

Keep this document open during your interview for quick code reference!
