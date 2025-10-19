# Master Interview Guide - Neo Financial Android Developer Position

## 🎯 One Document to Rule Them All

This is your **master reference** combining everything from the preparation session.

---

## Table of Contents

1. [30-Second Pitch](#30-second-pitch)
2. [Tasky Overview](#tasky-overview)
3. [Top 10 Q&A](#top-10-qa)
4. [Testing Approach](#testing-approach)
5. [Best Practices (4 Patterns)](#4-key-patterns)
6. [Tech Stack](#tech-stack)
7. [Questions for Neo](#questions-for-neo)
8. [Code to Demo](#code-to-demo)
9. [Day-Of Checklist](#day-of-checklist)

---

## 30-Second Pitch

"I'm an Android developer with a focus on modern Kotlin and Jetpack Compose. I built **Tasky**, an offline-first agenda app using **Kotlin, Compose, Hilt, Room, and Coroutines**—matching Neo's stack perfectly.

The app features **offline-first architecture** with background sync, **90+ unit tests** with edge case coverage, **Clean Architecture** with feature modules, and **centralized navigation** for testability.

I've solved real-world challenges like conflict resolution, exact alarm scheduling, and cross-device sync. I'm excited about Neo's mission to build better financial futures for Canadians and ready to contribute day one."

---

## Tasky Overview

**What:** Offline-first agenda management app (events, tasks, reminders)

**Tech Stack:**

- 100% Kotlin
- Jetpack Compose + Material 3
- Hilt for DI
- Room (5 migrations)
- Retrofit + OkHttp
- Coroutines + Flow
- WorkManager
- Encrypted DataStore

**Key Features:**

- Offline-first with background sync
- JWT auth with refresh tokens
- Exact alarms (Doze-compatible)
- Cross-device sync
- 90+ unit tests

---

## Top 10 Q&A

### 1. What's your approach to app architecture?

**Quick Answer:**

> "Clean Architecture organized by feature. Each feature (auth, agenda) has Data/Domain/Presentation layers. I use MVVM with MVI elements for state management."

**Key Points:**

- Feature-based modules (auth/, agenda/, core/)
- Three layers: Presentation → Domain → Data
- Repository pattern for data abstraction
- ViewModels expose StateFlow for state, Channel for events
- Sealed interfaces for type safety

**Code:** `AgendaViewModel.kt` lines 37-58

---

### 2. How do you manage state in Compose?

**Quick Answer:**

> "Unidirectional data flow: StateFlow for persistent state, Channel for one-time events, sealed interfaces for actions. Use WhileSubscribed(5000) to survive rotation."

**Key Points:**

- Single immutable `ViewState` data class
- `StateFlow` with `WhileSubscribed(5000)`
- `Channel` → `Flow` for one-time events
- Actions as sealed interfaces
- `update {}` for safe state mutations

**Code:** `AgendaScreenState.kt`, `AgendaViewModel.kt`

---

### 3. Explain Coroutines and Flows.

**Quick Answer:**

> "ViewModelScope for UI operations, application scope for background work. Use flatMapLatest for switching streams, combine for merging, and proper dispatcher management."

**Key Points:**

- `viewModelScope` for UI-related work
- `applicationScope` for operations beyond ViewModel
- `flatMapLatest` for switching data streams
- `combine` for merging multiple Flows
- `DispatcherProvider` for testability

**Code:** `AgendaViewModel.kt` lines 222-245 (flatMapLatest)

---

### 4. How do you handle dependency injection?

**Quick Answer:**

> "Dagger Hilt with feature modules. Use @Named qualifiers for multiple bindings, @Singleton for app-lifetime dependencies, and leverage Hilt's ViewModel/WorkManager integration."

**Key Points:**

- Module per feature (`TaskyAgendaModule`, `AppModule`)
- @Named for qualified bindings (authenticated/unauthenticated OkHttp)
- Interface → Implementation binding
- @HiltViewModel, @HiltWorker integration

**Code:** `TaskyAgendaModule.kt`, `AppModule.kt`

---

### 5. How do you ensure code quality?

**Quick Answer:**

> "Type-safe sealed interfaces, immutable state, dependency injection, comprehensive testing (90+ tests), and structured logging."

**Key Points:**

- Sealed interfaces for compile-time safety
- Immutable data classes with `copy()`
- DI for testability
- 90+ unit tests with edge cases
- Timber for structured logging
- Fakes over mocks

**Code:** All test files

---

### 6. How do you handle offline scenarios?

**Quick Answer:**

> "Offline-first: Room database serves all data first. Local changes queued in PendingItemSyncEntity, synced via WorkManager with retry logic."

**Key Points:**

- Local-first data access (instant response)
- Optimistic UI updates
- Pending sync queue
- WorkManager with exponential backoff
- Periodic fetch every 30 minutes
- Backend as source of truth

**Code:** `OfflineFirstAgendaRepository.kt`

---

### 7. How do you approach testing? (NEW!)

**Quick Answer:**

> "Multi-level testing with fakes for maintainability. 90+ unit tests covering ViewModels, repositories, and business logic. Focus on edge cases."

**Key Points:**

- 20+ tests for LoginViewModel
- 25+ tests for AgendaViewModel
- 25+ tests for InputValidator
- 20+ tests for Repository
- Fakes instead of mocks
- Edge case coverage (empty, unicode, concurrent)

**Code:** `LoginViewModelTest.kt`, `InputValidatorImplTest.kt`

---

### 8. Explain your navigation architecture.

**Quick Answer:**

> "Centralized navigation: screens emit NavigationEvents via callback. MainActivity handles all navigation in one place. Makes screens testable without NavController."

**Key Points:**

- NavigationEvent sealed interface
- `handleNavigationEvent()` in MainActivity
- Screens receive `onNavigate: (NavigationEvent) -> Unit`
- Hybrid pattern for savedStateHandle
- Easy to add analytics

**Code:** `MainActivity.kt` lines 172-229

---

### 9. How do you handle background work?

**Quick Answer:**

> "WorkManager for sync (network constraints, exponential backoff). AlarmManager.setExactAndAllowWhileIdle for notifications in Doze mode."

**Key Points:**

- Dedicated workers per operation (Create/Update/Delete)
- Network constraints for battery efficiency
- Exponential backoff retry
- Exact alarms for timely notifications
- RescheduleAlarmWorker after reboot

**Code:** `CreateAgendaItemWorker.kt`, `AlarmManagerAlarmScheduler.kt`

---

### 10. How do you handle configuration changes?

**Quick Answer:**

> "ViewModel survives rotation automatically. SavedStateHandle for UI state. WhileSubscribed(5000) gives 5s grace period. DataStore for session."

**Key Points:**

- ViewModel automatic survival
- SavedStateHandle for process death
- `WhileSubscribed(5000)` prevents restart during rotation
- Encrypted DataStore for auth tokens
- Room for all user data

**Code:** `AgendaViewModel.kt` lines 44-46

---

## Testing Approach

### Philosophy

> "I write tests for everything with business logic. Tests aren't just happy path—I focus on edge cases like offline scenarios, empty inputs, concurrent operations."

### What's Tested (90+ Tests)

- **ViewModels** (45+ tests)

  - State management
  - Event emission
  - Error handling
  - User flows

- **Repositories** (20+ tests)

  - Offline-first behavior
  - Data synchronization
  - Error fallback

- **Business Logic** (25+ tests)
  - Input validation
  - Edge cases
  - Boundary conditions

### Why Fakes Over Mocks

> "Fakes behave like real implementations and are reusable. My FakeAgendaRepository actually emits Flow updates just like Room would."

### Example Test

```kotlin
@Test
fun `when login succeeds, success event is emitted`() = runTest {
    val events = mutableListOf<LoginScreenEvent>()
    val job = launch {
        viewModel.event.collect { events.add(it) }
    }

    viewModel.onAction(LoginScreenAction.OnLogin)
    advanceUntilIdle()

    assertTrue(events.any { it is LoginScreenEvent.Success })
    job.cancel()
}
```

---

## 4 Key Patterns

### Pattern 1: ScreenRoot + Screen

```kotlin
// Root - Stateful (ViewModel, events)
@Composable
fun LoginScreenRoot(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigate: (NavigationEvent) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LoginScreen(onAction = viewModel::onAction, state = state)
}

// Screen - Stateless (pure UI)
@Composable
fun LoginScreen(
    onAction: (LoginScreenAction) -> Unit,
    state: LoginScreenState
) {
    // Pure UI - can preview!
}
```

### Pattern 2: ObserveAsEvents

```kotlin
ObserveAsEvents(viewModel.event) { event ->
    when(event) {
        is LoginScreenEvent.Success -> {
            onNavigate(NavigationEvent.NavigateToAgenda)
        }
    }
}
```

### Pattern 3: .onStart{}.stateIn()

```kotlin
val state = _state
    .onStart {
        loadInitialData()
        observePasswordText()
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = _state.value
    )
```

### Pattern 4: Centralized Navigation

```kotlin
// MainActivity
private fun handleNavigationEvent(
    event: NavigationEvent,
    navController: NavHostController
) {
    when(event) {
        is NavigationEvent.NavigateToAgenda -> {
            navController.navigate(AgendaRoute) {
                popUpTo(AuthGraph) { inclusive = true }
            }
        }
    }
}
```

---

## Tech Stack

| Category          | Technology                    |
| ----------------- | ----------------------------- |
| **Language**      | 100% Kotlin                   |
| **UI**            | Jetpack Compose + Material 3  |
| **Architecture**  | Clean (MVVM + MVI)            |
| **DI**            | Dagger Hilt                   |
| **Database**      | Room (5 migrations)           |
| **Network**       | Retrofit + OkHttp + Moshi     |
| **Async**         | Coroutines + Flow + StateFlow |
| **Background**    | WorkManager                   |
| **Notifications** | AlarmManager (exact)          |
| **Storage**       | Encrypted DataStore           |
| **Images**        | Coil 3                        |
| **Logging**       | Timber                        |
| **Navigation**    | Navigation Compose            |
| **Testing**       | JUnit + Coroutines Test       |

---

## Questions for Neo

1. **GraphQL implementation** on Android?
2. **Testing strategy** and coverage goals?
3. **Code review process**?
4. **Team collaboration** (Android/iOS/Backend)?
5. **CI/CD pipeline**?
6. **GitHub Copilot usage** guidelines?
7. **Technical challenges** currently facing?
8. **Onboarding process**?

---

## Code to Demo

### If Asked About Architecture

**Show:** `AgendaViewModel.kt` (state management)
**Explain:** Clean Architecture, feature modules, layer separation

### If Asked About Testing

**Show:** `LoginViewModelTest.kt` or `InputValidatorImplTest.kt`
**Explain:** 90+ tests, fakes, edge cases

### If Asked About Navigation

**Show:** `MainActivity.kt` (handleNavigationEvent)
**Explain:** Centralized pattern, testability

### If Asked About Offline-First

**Show:** `OfflineFirstAgendaRepository.kt`
**Explain:** Local-first, background sync, WorkManager

---

## Day-Of Checklist

### 30 Minutes Before

- [ ] Review this document
- [ ] Practice 30-second pitch
- [ ] Open Tasky in Android Studio
- [ ] Breathe, stay calm

### During Interview

- [ ] Be specific with examples
- [ ] Reference actual code
- [ ] Mention testing early
- [ ] Show enthusiasm
- [ ] Ask questions

---

## Success Indicators

**After interview, you'll feel good if you:**

- Explained one complex feature clearly
- Mentioned your 90+ tests
- Showed actual Tasky code
- Asked thoughtful questions
- Connected with interviewer

---

## Final Thoughts

### You Are Ready Because:

1. **Portfolio**: Production-ready Tasky app
2. **Stack**: 100% match with Neo
3. **Testing**: 90+ comprehensive tests
4. **Patterns**: Modern best practices
5. **Documentation**: 8 comprehensive guides
6. **Preparation**: Thoroughly prepared

### You Will Succeed Because:

1. **Technical skills**: Strong and demonstrated
2. **Problem-solving**: Real challenges solved
3. **Growth mindset**: Ready to learn GraphQL
4. **Passion**: Genuine interest in Android
5. **Team fit**: Collaborative, quality-focused

---

## Emergency Mantras

**If nervous:**

> "I've built something real. I can show it. I can explain it. I'm prepared."

**If stuck on a question:**

> "Let me think about that for a moment..."  
> (Reference Tasky for examples)

**If they ask about weaknesses:**

> "I haven't worked with GraphQL yet, but I'm excited to learn. My Retrofit experience and understanding of reactive patterns will transfer well."

---

## Post-Interview

### Immediate Follow-Up

- Send thank you email within 24 hours
- Reference something specific from conversation
- Reiterate enthusiasm

### While Waiting

- Don't stress
- You did your best
- Keep learning

---

## 🎉 YOU'VE GOT THIS!

**Preparation Score: 100/100**

- ✅ Portfolio
- ✅ Documentation
- ✅ Testing
- ✅ Practice
- ✅ Confidence

**Stack Match: 100%**

- ✅ Kotlin
- ✅ Compose
- ✅ Hilt
- ✅ Coroutines
- ✅ Modern patterns

**Readiness: READY!** 🚀

---

## Quick Stats

- **165 Kotlin files** in Tasky
- **90+ unit tests** with edge coverage
- **6 screens** with centralized navigation
- **5 Room migrations** (db evolution)
- **4 background workers** (sync operations)
- **9 fake implementations** (testing)
- **100% Jetpack Compose** UI

---

## Remember This

_"Neo isn't just hiring for skills—they're hiring for people who care about quality, collaboration, and continuous improvement. Tasky demonstrates all three. You've got this!"_

---

**Now close this document, take a deep breath, and go ace that interview!** 🎯🚀

_You're not just a candidate—you're a perfect fit._
