# Navigation Refactoring - Centralized Navigation Pattern

## Overview

We've implemented **centralized navigation** following Philipp Lackner's best practices. All navigation logic is now handled at the **MainActivity/NavHost level**, not in individual screens.

## What Changed

### ✅ **Completed**

#### 1. Created NavigationEvent System

```kotlin
sealed interface NavigationEvent {
    data object NavigateToLogin : NavigationEvent
    data object NavigateToRegister : NavigationEvent
    data object NavigateToAgenda : NavigationEvent
    data class NavigateToAgendaDetail(...) : NavigationEvent
    data object NavigateBack : NavigationEvent
    // ... etc
}
```

#### 2. Centralized Navigation in MainActivity

```kotlin
// MainActivity now handles ALL navigation
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
        // ... all other navigation
    }
}
```

#### 3. Refactored Screens (NavController Removed)

**LoginScreenRoot:**

```kotlin
// Before ❌
fun LoginScreenRoot(navController: NavController)

// After ✅
fun LoginScreenRoot(onNavigate: (NavigationEvent) -> Unit)
```

**Screens Updated:**

- ✅ LoginScreen
- ✅ RegistrationScreen
- ✅ AgendaScreen

**Navigation now bubbles up:**

```kotlin
// In screen
ObserveAsEvents(viewModel.event) { event ->
    when(event) {
        is LoginScreenEvent.Success -> {
            onNavigate(NavigationEvent.NavigateToAgenda)  // ✅ Emit event
        }
    }
}
```

---

## Benefits of This Architecture

### 1. **Testability**

```kotlin
// Screens are now testable without NavController
@Test
fun `when login succeeds, emits navigate to agenda event`() {
    val navigationEvents = mutableListOf<NavigationEvent>()

    composeTestRule.setContent {
        LoginScreenRoot(
            onNavigate = { navigationEvents.add(it) }
        )
    }

    // Trigger login...
    assertTrue(navigationEvents.contains(NavigationEvent.NavigateToAgenda))
}
```

### 2. **Separation of Concerns**

- **Screens**: Only care about UI and emitting navigation intent
- **MainActivity**: Handles actual navigation logic
- **ViewModels**: Business logic, no navigation knowledge

### 3. **Maintainability**

- All navigation logic in one place
- Easy to add navigation analytics
- Easy to change navigation behavior globally
- No circular dependencies

### 4. **Preview Support**

```kotlin
@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreenRoot(
        onNavigate = {}  // ✅ Easy to mock
    )
}
```

---

## Remaining Screens (Special Cases)

### AgendaDetailScreen, EditTextScreen, PhotoPreviewScreen

These screens use **navigation-for-result** pattern with `savedStateHandle`:

```kotlin
// AgendaDetailScreen observes results from EditTextScreen
val editedTitle: String? = backStackEntry
    ?.savedStateHandle
    ?.get(EditTextFieldType.TITLE.key)

LaunchedEffect(editedTitle) {
    if(editedTitle != null) {
        viewModel.onAction(OnEditTitle(editedTitle))
    }
}
```

**Options for these screens:**

1. **Keep Minimal NavController** (Pragmatic)

   - Use NavController only for `savedStateHandle` and `popBackStack`
   - Still use `onNavigate` callback for forward navigation
   - Document as legitimate exception

2. **Refactor to Shared ViewModel** (Clean but complex)

   - Use shared ViewModel for passing data between screens
   - Completely remove Nav Controller

3. **Use Navigation Arguments** (Alternative)
   - Pass data through navigation arguments instead of savedStateHandle
   - Refactor screens to accept data as navigation args

**Recommendation:** Go with Option 1 for now. It's pragmatic and the savedStateHandle pattern is a legitimate Android Navigation pattern.

---

## For Your Interview

### When asked about navigation:

> "I've implemented **centralized navigation** in Tasky where all navigation logic lives at the MainActivity level. Screens emit **NavigationEvents** through a callback, and the navigation host handles the actual navigation.
>
> This makes screens **testable without NavController dependencies**, provides **clear separation of concerns**, and makes it easy to add cross-cutting concerns like analytics.
>
> For screens using **navigation-for-result** with savedStateHandle (a Jetpack Navigation pattern), I keep minimal NavController access specifically for that use case, while still using navigation events for forward navigation."

### Code Example to Reference:

```kotlin
// MainActivity - Centralized Navigation
@Composable
fun TaskyRoot(navController: NavHostController) {
    NavHost(navController = navController, ...) {
        composable<LoginRoute> {
            LoginScreenRoot(
                onNavigate = { event ->
                    handleNavigationEvent(event, navController)
                }
            )
        }
    }
}

private fun handleNavigationEvent(
    event: NavigationEvent,
    navController: NavHostController
) {
    when(event) {
        is NavigationEvent.NavigateToLogin -> {
            navController.navigate(LoginRoute) {
                popUpTo(AuthGraph) { inclusive = true }
            }
        }
        // All navigation logic here
    }
}

// Screen - Emits Navigation Intent
@Composable
fun LoginScreenRoot(onNavigate: (NavigationEvent) -> Unit) {
    ObserveAsEvents(viewModel.event) { event ->
        when(event) {
            is LoginEvent.Success -> {
                onNavigate(NavigationEvent.NavigateToAgenda)
            }
        }
    }
}
```

---

## Architecture Diagram

```
┌──────────────────────────────────────────┐
│           MainActivity                   │
│                                          │
│  ┌────────────────────────────────────┐ │
│  │   handleNavigationEvent()          │ │
│  │   - All navigation logic           │ │
│  │   - NavController operations       │ │
│  │   - popUpTo, launchSingleTop, etc  │ │
│  └────────────────────────────────────┘ │
│                 ▲                        │
│                 │ NavigationEvent        │
└─────────────────┼────────────────────────┘
                  │
    ┌─────────────┴──────────────┬──────────────┐
    │                            │              │
┌───┴──────┐              ┌──────┴───┐    ┌────┴─────┐
│  Login   │              │  Agenda  │    │ Register │
│  Screen  │              │  Screen  │    │  Screen  │
└──────────┘              └──────────┘    └──────────┘
    │                            │              │
    │ onNavigate callback        │              │
    └────────────────────────────┴──────────────┘
```

---

## Next Steps (If Time Permits)

1. ✅ Complete refactoring of remaining simple screens
2. ⏳ Refactor AgendaDetailScreen to use onNavigate for forward navigation
3. ⏳ Refactor EditTextScreen similarly
4. ⏳ Refactor PhotoPreviewScreen similarly
5. ⏳ Add navigation analytics at centralized point
6. ⏳ Add navigation testing examples

---

## Key Takeaways

1. **Navigation logic belongs at the navigation host level, not in screens**
2. **Screens should emit navigation intent, not perform navigation**
3. **This pattern makes code more testable and maintainable**
4. **savedStateHandle is a legitimate exception for navigation-for-result**
5. **This is a modern Android best practice recommended by experts**

---

This architecture demonstrates **production-ready** navigation patterns that scale well in team environments and align with what companies like Neo Financial look for.
