# Tasky - Best Practices Interview Cheat Sheet

Quick reference for your Neo Financial interview covering the 3 key patterns Philipp Lackner emphasizes plus your new centralized navigation.

---

## Pattern 1: ScreenRoot + Screen Separation

### Why?

- **Testability**: Screen can be tested without ViewModel
- **Preview Support**: Screen can be previewed with mock data
- **Reusability**: Same Screen, different Roots for different contexts

### Implementation:

```kotlin
// ScreenRoot - Stateful (ViewModel, navigation, side effects)
@Composable
fun LoginScreenRoot(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigate: (NavigationEvent) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) { event ->
        when(event) {
            is LoginScreenEvent.Success -> {
                onNavigate(NavigationEvent.NavigateToAgenda)
            }
        }
    }

    LoginScreen(
        onAction = viewModel::onAction,
        state = state
    )
}

// Screen - Stateless (pure UI)
@Composable
fun LoginScreen(
    onAction: (LoginScreenAction) -> Unit,
    state: LoginScreenState
) {
    // Pure UI rendering based on state
    // No ViewModel, no navigation, no side effects
}

// Preview works! ✅
@Preview
@Composable
fun LoginScreenPreview() {
    TaskyTheme {
        LoginScreen(
            onAction = {},
            state = LoginScreenState(email = "test@test.com")
        )
    }
}
```

**Interview Talking Point:**

> "I separate each screen into a Root and Screen composable. The Root handles framework concerns like ViewModels and side effects, while the Screen is a pure stateless composable. This makes the UI easy to preview and test with different states."

---

## Pattern 2: ObserveAsEvents for One-Time Events

### Why?

- **No duplicate events**: Events consumed once, even on recomposition
- **Lifecycle-aware**: Stops collecting when composable is destroyed
- **Clean separation**: State (persistent) vs Events (one-time)

### Implementation:

```kotlin
// ViewModel
private val eventChannel = Channel<AgendaScreenEvent>()
val event = eventChannel.receiveAsFlow()

// Emit events
eventChannel.send(AgendaScreenEvent.Success)

// Composable observes
ObserveAsEvents(viewModel.event) { event ->
    when(event) {
        is AgendaScreenEvent.SuccessLogout -> {
            keyboardController?.hide()
            Toast.makeText(context, "Logged out", Toast.LENGTH_LONG).show()
            onNavigate(NavigationEvent.NavigateToLogin)  // ✅ Only fires once
        }
    }
}
```

### ObserveAsEvents Implementation:

```kotlin
@Composable
fun <T> ObserveAsEvents(
    events: Flow<T>,
    key1: Any? = null,
    key2: Any? = null,
    onEvent: (T) -> Unit
) {
    val onEventRef = rememberUpdatedState(onEvent)

    LaunchedEffect(events, key1, key2) {
        events.collect { event ->
            onEventRef.value(event)  // Uses latest lambda
        }
    }
}
```

### Why NOT LaunchedEffect directly?

```kotlin
// ❌ WRONG - might re-trigger on recomposition
LaunchedEffect(true) {
    viewModel.event.collect { event ->
        navController.navigate(...)  // Might navigate multiple times!
    }
}
```

**Interview Talking Point:**

> "For one-time events like navigation or showing toasts, I use a custom `ObserveAsEvents` composable that wraps LaunchedEffect. It ensures events are consumed exactly once and uses rememberUpdatedState to keep the lambda reference fresh without restarting collection. This prevents bugs like duplicate navigation or multiple toasts on recomposition."

---

## Pattern 3: .onStart{}.stateIn() for Lazy Initialization

### Why?

- **Lazy**: Runs only when first UI collector subscribes
- **Survives rotation**: WhileSubscribed(5000) keeps flow active during config changes
- **Battery efficient**: Stops collecting after timeout when app backgrounded
- **Declarative**: All initialization in one place

### Implementation:

```kotlin
@HiltViewModel
class AgendaViewModel @Inject constructor(
    private val sessionStorage: SessionStorage,
    private val agendaRepository: AgendaRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _state = MutableStateFlow(AgendaScreenState(
        selectedDate = selectedDateTimeStamp?.let { parseTimestampToLocalDate(it) }
            ?: LocalDate.now()
    ))

    val state = _state
        .onStart {
            loadInitialData()         // ✅ Runs once when first subscriber
            observeSelectedDateChanges() // ✅ Set up reactive streams
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),  // ✅ 5s timeout
            initialValue = _state.value
        )
}
```

### WhileSubscribed(5000) Magic:

**Configuration Change (Screen Rotation):**

```
1. Screen rotates → Old composable destroyed
2. [Within 5 seconds] → New composable created
3. StateFlow still active (within grace period) → No restart! ✅
4. Data NOT reloaded → Smooth user experience ✅
```

**App Backgrounding:**

```
1. User switches to another app
2. After 5 seconds → StateFlow stops collecting
3. Saves battery and resources ✅
```

**Interview Talking Point:**

> "I use `.onStart{}` with `stateIn()` for ViewModel initialization. The onStart block runs only when the first UI collector subscribes, and `WhileSubscribed(5000)` gives a 5-second grace period that prevents unnecessary restarts during configuration changes while still stopping work when the app goes to the background. This is both performant and battery-efficient."

---

## Pattern 4: Centralized Navigation (NEW!)

### Why?

- **Testability**: Screens testable without mocking NavController
- **Separation of Concerns**: Screens emit intent, MainActivity navigates
- **Maintainability**: All navigation logic in one place
- **Analytics**: Easy to add at single point

### Implementation:

```kotlin
// 1. Define Navigation Events
sealed interface NavigationEvent {
    data object NavigateToLogin : NavigationEvent
    data object NavigateToAgenda : NavigationEvent
    data class NavigateToAgendaDetail(
        val agendaItemId: String?,
        val isEditable: Boolean,
        val type: String
    ) : NavigationEvent
}

// 2. Centralized Handler (MainActivity)
private fun handleNavigationEvent(
    event: NavigationEvent,
    navController: NavHostController
) {
    when(event) {
        is NavigationEvent.NavigateToLogin -> {
            navController.navigate(Destination.Route.LoginRoute) {
                popUpTo(Destination.Graph.AuthGraph) { inclusive = true }
            }
        }
        is NavigationEvent.NavigateToAgenda -> {
            navController.navigate(Destination.Route.AgendaRoute) {
                popUpTo(Destination.Graph.AuthGraph) { inclusive = true }
            }
        }
        // All other navigation...
    }
}

// 3. Screens Emit Events
@Composable
fun LoginScreenRoot(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigate: (NavigationEvent) -> Unit  // ✅ Not NavController
) {
    ObserveAsEvents(viewModel.event) { event ->
        when(event) {
            is LoginScreenEvent.Success -> {
                onNavigate(NavigationEvent.NavigateToAgenda)  // ✅ Emit intent
            }
        }
    }
}

// 4. NavHost Wiring
NavHost(navController = navController, ...) {
    composable<LoginRoute> {
        LoginScreenRoot(
            onNavigate = { event ->
                handleNavigationEvent(event, navController)
            }
        )
    }
}
```

**Interview Talking Point:**

> "I implement centralized navigation where screens emit `NavigationEvents` through a callback instead of directly accessing NavController. The MainActivity handles all navigation in a single `handleNavigationEvent()` function. This makes screens testable without mocking, provides a single place for navigation analytics, and follows separation of concerns—screens are responsible for UI, not navigation."

---

## Complete Example: Putting It All Together

```kotlin
// ========================================
// 1. State & Events (ViewModel)
// ========================================
data class LoginScreenState(
    val email: String = "",
    val isLoading: Boolean = false,
    val isValidEmail: Boolean = true
)

sealed interface LoginScreenAction {
    data class OnEmailChanged(val email: String) : LoginScreenAction
    data object OnLogin : LoginScreenAction
    data object OnGoToRegister : LoginScreenAction
}

sealed interface LoginScreenEvent {
    data object Success : LoginScreenEvent
    data class Error(val message: UiText) : LoginScreenEvent
}

// ========================================
// 2. ViewModel
// ========================================
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

    private val _state = MutableStateFlow(LoginScreenState())

    val state = _state
        .onStart {
            // Pattern 3: Lazy initialization
            observeEmailValidation()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),  // 5s grace period
            initialValue = _state.value
        )

    // Pattern 2: One-time events via Channel
    private val eventChannel = Channel<LoginScreenEvent>()
    val event = eventChannel.receiveAsFlow()

    fun onAction(action: LoginScreenAction) {
        when(action) {
            is LoginScreenAction.OnEmailChanged -> {
                _state.update { it.copy(email = action.email) }
            }
            is LoginScreenAction.OnLogin -> {
                login()
            }
            else -> Unit
        }
    }

    private fun login() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            when(val result = authRepository.login(_state.value.email)) {
                is Result.Success -> {
                    eventChannel.send(LoginScreenEvent.Success)  // One-time event
                }
                is Result.Error -> {
                    eventChannel.send(LoginScreenEvent.Error(result.error.asUiText()))
                }
            }

            _state.update { it.copy(isLoading = false) }
        }
    }
}

// ========================================
// 3. ScreenRoot (Stateful)
// ========================================
@Composable
fun LoginScreenRoot(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigate: (NavigationEvent) -> Unit  // Pattern 4: Centralized navigation
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Pattern 2: ObserveAsEvents for one-time events
    ObserveAsEvents(viewModel.event) { event ->
        when(event) {
            is LoginScreenEvent.Success -> {
                Toast.makeText(context, "Logged in!", Toast.LENGTH_LONG).show()
                onNavigate(NavigationEvent.NavigateToAgenda)  // Pattern 4
            }
            is LoginScreenEvent.Error -> {
                Toast.makeText(context, event.message.asString(context), Toast.LENGTH_LONG).show()
            }
        }
    }

    // Pattern 1: Delegate to stateless Screen
    LoginScreen(
        onAction = { action ->
            when(action) {
                is LoginScreenAction.OnGoToRegister -> {
                    onNavigate(NavigationEvent.NavigateToRegister)  // Pattern 4
                }
                else -> Unit
            }
            viewModel.onAction(action)
        },
        state = state
    )
}

// ========================================
// 4. Screen (Stateless)
// ========================================
@Composable
fun LoginScreen(
    onAction: (LoginScreenAction) -> Unit,
    state: LoginScreenState
) {
    // Pattern 1: Pure stateless UI
    Column {
        TaskyInputTextField(
            text = state.email,
            onValueChange = { onAction(LoginScreenAction.OnEmailChanged(it)) },
            isValidInput = state.isValidEmail
        )

        TaskyActionButton(
            text = "Login",
            onClick = { onAction(LoginScreenAction.OnLogin) },
            isLoading = state.isLoading,
            isEnabled = state.isValidEmail
        )

        TaskySurface(
            onClick = { onAction(LoginScreenAction.OnGoToRegister) },
            text = "Sign Up"
        )
    }
}

// ========================================
// 5. Preview (Works because Screen is stateless!)
// ========================================
@Preview
@Composable
fun LoginScreenPreview() {
    TaskyTheme {
        LoginScreen(
            onAction = {},
            state = LoginScreenState(
                email = "test@example.com",
                isLoading = false
            )
        )
    }
}
```

---

## Quick Decision Tree

**When to use each pattern:**

```
Need to show a screen?
├─ Create ScreenRoot (stateful) + Screen (stateless) ✅ Pattern 1
│
├─ Need to navigate?
│  └─ Emit NavigationEvent in Root ✅ Pattern 4
│
├─ Need one-time event (toast, navigation, dialog)?
│  └─ Use Channel + ObserveAsEvents ✅ Pattern 2
│
└─ Need to initialize ViewModel when UI subscribes?
   └─ Use .onStart{}.stateIn() ✅ Pattern 3
```

---

## Files to Reference in Interview

1. **Pattern 1 (ScreenRoot + Screen):**

   - `LoginScreen.kt` lines 46-89 (Root) and 92-199 (Screen)

2. **Pattern 2 (ObserveAsEvents):**

   - `LoginScreen.kt` lines 55-76
   - `ObserveAsEvents.kt` implementation

3. **Pattern 3 (.onStart + stateIn):**

   - `AgendaViewModel.kt` lines 37-58
   - `LoginViewModel.kt` lines 37-45

4. **Pattern 4 (Centralized Navigation):**
   - `NavigationEvent.kt` - event definitions
   - `MainActivity.kt` lines 172-229 - centralized handler
   - `LoginScreen.kt` line 48 - onNavigate parameter

---

## Interview Soundbites

**On Architecture:**

> "I follow modern Android best practices with a clear separation between stateful and stateless composables, centralized navigation, and unidirectional data flow. This makes the codebase testable, maintainable, and scalable for team environments."

**On State Management:**

> "I use StateFlow with WhileSubscribed to survive configuration changes, Channels for one-time events to prevent duplicates, and lazy initialization with .onStart{} to only load data when the UI subscribes."

**On Testing:**

> "My architecture is designed for testability. Screens are pure functions of state, ViewModels can be tested without Android framework, and navigation can be tested by verifying emitted events—no mocking required."

**On Team Collaboration:**

> "These patterns make it easy for teams to work in parallel. Navigation logic is centralized, screens follow consistent patterns, and the architecture is self-documenting through sealed interfaces and clear naming."

---

## Final Confidence Booster

You're not just following tutorials—you've implemented **production-ready patterns** that companies like Neo Financial use in their apps. You can:

1. ✅ **Explain WHY** each pattern exists (not just HOW)
2. ✅ **Show CODE** from your real app
3. ✅ **Discuss TRADEOFFS** (e.g., savedStateHandle exception)
4. ✅ **Connect to PRINCIPLES** (SRP, testability, maintainability)

**You're ready!** 🚀
