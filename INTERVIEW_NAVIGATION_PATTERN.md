# Interview Guide: Centralized Navigation Architecture

## Quick Summary

You've implemented **centralized navigation** in Tasky—a modern Android best practice where navigation logic lives at the app level, not in individual screens. This makes your code more testable, maintainable, and follows the principle of separation of concerns.

---

## 1. The Pattern Explained (30 seconds)

> "In Tasky, I use a **centralized navigation pattern**. Instead of screens directly calling `navController.navigate()`, they emit **NavigationEvents** through a callback. The MainActivity's NavHost handles all actual navigation in one place. This makes screens testable without mocking NavController, provides clear separation of concerns, and makes it easy to add cross-cutting features like analytics."

---

## 2. Show the Code

### Navigation Event Definition

```kotlin
// NavigationEvent.kt
sealed interface NavigationEvent {
    // Auth Navigation
    data object NavigateToLogin : NavigationEvent
    data object NavigateToRegister : NavigationEvent
    data object NavigateToAgenda : NavigationEvent

    // Agenda Navigation
    data class NavigateToAgendaDetail(
        val agendaItemId: String?,
        val isEditable: Boolean,
        val type: String
    ) : NavigationEvent

    data object NavigateBack : NavigationEvent
}
```

### Centralized Handler (MainActivity)

```kotlin
// All navigation logic in ONE place
private fun handleNavigationEvent(
    event: NavigationEvent,
    navController: NavHostController
) {
    when(event) {
        is NavigationEvent.NavigateToLogin -> {
            navController.navigate(Destination.Route.LoginRoute) {
                // Clear back stack when navigating to login
                popUpTo(Destination.Graph.AuthGraph) {
                    inclusive = true
                }
            }
        }
        is NavigationEvent.NavigateToAgenda -> {
            navController.navigate(Destination.Route.AgendaRoute) {
                // Clear auth graph when navigating to agenda
                popUpTo(Destination.Graph.AuthGraph) {
                    inclusive = true
                }
            }
        }
        is NavigationEvent.NavigateToAgendaDetail -> {
            navController.navigate(
                Destination.Route.AgendaDetailRoute(
                    agendaItemId = event.agendaItemId,
                    isEditable = event.isEditable,
                    type = event.type
                )
            )
        }
        // ... all other navigation
    }
}
```

### Screen Emits Navigation Intent

```kotlin
@Composable
fun LoginScreenRoot(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigate: (NavigationEvent) -> Unit  // ✅ Callback, not NavController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) { event ->
        when(event) {
            is LoginScreenEvent.Success -> {
                // ✅ Emit navigation intent
                onNavigate(NavigationEvent.NavigateToAgenda)
            }
        }
    }

    LoginScreen(
        onAction = { action ->
            when(action) {
                is LoginScreenAction.OnGoToRegister -> {
                    // ✅ Emit navigation intent
                    onNavigate(NavigationEvent.NavigateToRegister)
                }
                else -> Unit
            }
            viewModel.onAction(action)
        },
        state = state
    )
}
```

### NavHost Wiring

```kotlin
@Composable
fun TaskyRoot(navController: NavHostController) {
    TaskyTheme {
        NavHost(navController = navController, ...) {
            navigation<AuthGraph>(startDestination = LoginRoute) {
                composable<LoginRoute> {
                    LoginScreenRoot(
                        onNavigate = { event ->
                            handleNavigationEvent(event, navController)
                        }
                    )
                }

                composable<RegisterRoute> {
                    RegistrationScreenRoot(
                        onNavigate = { event ->
                            handleNavigationEvent(event, navController)
                        }
                    )
                }
            }

            navigation<AgendaGraph>(startDestination = AgendaRoute) {
                composable<AgendaRoute> {
                    AgendaScreenRoute(
                        onNavigate = { event ->
                            handleNavigationEvent(event, navController)
                        }
                    )
                }
            }
        }
    }
}
```

---

## 3. Why This Pattern?

### Problem with Direct NavController Access

```kotlin
// ❌ BAD: Screen has NavController dependency
@Composable
fun LoginScreenRoot(
    viewModel: LoginViewModel,
    navController: NavController  // ❌ Tight coupling
) {
    ObserveAsEvents(viewModel.event) { event ->
        when(event) {
            is LoginEvent.Success -> {
                navController.navigate(AgendaRoute)  // ❌ Navigation logic in screen
            }
        }
    }
}
```

**Problems:**

- ❌ **Hard to test**: Need to mock NavController
- ❌ **Tight coupling**: Screen knows about navigation structure
- ❌ **No preview support**: Can't preview screen with NavController
- ❌ **Scattered logic**: Navigation logic spread across multiple files
- ❌ **Hard to add analytics**: Would need to add logging in every screen

### Solution with Centralized Pattern

```kotlin
// ✅ GOOD: Screen emits intent
@Composable
fun LoginScreenRoot(
    viewModel: LoginViewModel,
    onNavigate: (NavigationEvent) -> Unit  // ✅ Simple callback
) {
    ObserveAsEvents(viewModel.event) { event ->
        when(event) {
            is LoginEvent.Success -> {
                onNavigate(NavigationEvent.NavigateToAgenda)  // ✅ Emit intent
            }
        }
    }
}
```

**Benefits:**

- ✅ **Easy to test**: Just check emitted events
- ✅ **Loose coupling**: Screen doesn't know navigation structure
- ✅ **Preview support**: Easy to mock callback
- ✅ **Centralized logic**: All navigation in one place
- ✅ **Easy to add analytics**: One place to add logging

---

## 4. Testing Example

```kotlin
@Test
fun `when login succeeds, emits navigate to agenda event`() {
    // Arrange
    val navigationEvents = mutableListOf<NavigationEvent>()
    val fakeViewModel = FakeLoginViewModel()

    composeTestRule.setContent {
        LoginScreenRoot(
            viewModel = fakeViewModel,
            onNavigate = { navigationEvents.add(it) }  // ✅ Capture events
        )
    }

    // Act
    fakeViewModel.emitSuccess()

    // Assert
    assertTrue(navigationEvents.contains(NavigationEvent.NavigateToAgenda))
}

// ❌ Compare to testing with NavController:
@Test
fun `when login succeeds, navigates to agenda - OLD WAY`() {
    val navController = mock<NavController>()  // ❌ Need to mock
    // ... complex setup
    verify(navController).navigate(AgendaRoute)  // ❌ Fragile
}
```

---

## 5. Advanced: Navigation Analytics

With centralized navigation, adding analytics is trivial:

```kotlin
private fun handleNavigationEvent(
    event: NavigationEvent,
    navController: NavHostController
) {
    // ✅ Log ALL navigation in one place
    analytics.logNavigation(event.javaClass.simpleName)

    when(event) {
        is NavigationEvent.NavigateToLogin -> {
            navController.navigate(LoginRoute) {
                popUpTo(AuthGraph) { inclusive = true }
            }
        }
        // ... rest
    }
}
```

---

## 6. Edge Case: Navigation for Result

Some screens use `savedStateHandle` for navigation-for-result:

```kotlin
// AgendaDetailScreen navigates to EditTextScreen
// EditTextScreen saves result to savedStateHandle
// AgendaDetailScreen observes the result

// This is a legitimate Jetpack Navigation pattern
val editedTitle: String? = backStackEntry
    ?.savedStateHandle
    ?.get(EditTextFieldType.TITLE.key)
```

**Interview Answer:**

> "For navigation-for-result using savedStateHandle, which is a Jetpack Navigation pattern, I keep minimal NavController access specifically for that use case. However, I still use navigation events for forward navigation to maintain the centralized pattern where possible."

---

## 7. Interview Questions & Answers

### Q: "How do you handle navigation in your app?"

> "I use a **centralized navigation pattern**. All navigation logic lives at the MainActivity level in a `handleNavigationEvent()` function. Screens emit **NavigationEvents** through a callback instead of directly accessing NavController.
>
> For example, when a user successfully logs in, the LoginScreen emits a `NavigationEvent.NavigateToAgenda` event. The MainActivity receives this event and performs the actual navigation, including clearing the auth back stack.
>
> This approach provides several benefits:
>
> 1. **Testability**: Screens can be tested without mocking NavController
> 2. **Separation of Concerns**: Screens focus on UI, MainActivity handles navigation
> 3. **Maintainability**: All navigation logic in one place
> 4. **Analytics**: Easy to add logging at a single point
> 5. **Preview Support**: Screens can be previewed without NavController dependencies"

### Q: "Why not just pass NavController to screens?"

> "Passing NavController directly creates tight coupling and makes screens hard to test. With centralized navigation:
>
> - Screens become **pure** - they only care about rendering UI and emitting user intent
> - Testing is **simple** - just verify the right events are emitted
> - Navigation logic is **centralized** - easy to modify without touching screens
> - We can add **cross-cutting concerns** like analytics or deep linking in one place
>
> This follows the **Single Responsibility Principle** - screens are responsible for UI, not navigation."

### Q: "What about back navigation?"

> "For back navigation, I include `NavigateBack` and `NavigateUp` events that the centralized handler translates to `popBackStack()` or `navigateUp()` calls. This keeps the pattern consistent.
>
> For complex cases like navigation-for-result with savedStateHandle, I document that as a legitimate exception where minimal NavController access is needed for the Jetpack Navigation pattern."

---

## 8. Key Talking Points

1. **This is a modern best practice** recommended by Android experts like Philipp Lackner
2. **It's not over-engineering** - it solves real problems (testing, maintainability)
3. **It's scalable** - works well in team environments
4. **It's production-ready** - you've implemented it in a real app
5. **It demonstrates architectural thinking** - not just coding, but designing systems

---

## 9. Comparison to Other Patterns

| Pattern                      | Pros                             | Cons                          |
| ---------------------------- | -------------------------------- | ----------------------------- |
| **NavController in Screens** | Simple, direct                   | Hard to test, tight coupling  |
| **Centralized Navigation**   | Testable, maintainable, scalable | Slightly more boilerplate     |
| **Navigator Interface**      | Abstraction, testable            | More complex, more interfaces |
| **Voyager/Decompose**        | Third-party, powerful            | External dependency           |

**Your choice (Centralized)** balances simplicity with best practices.

---

## 10. Code Locations in Tasky

For quick reference during interview:

- **NavigationEvent**: `app/src/main/java/com/aarevalo/tasky/core/navigation/NavigationEvent.kt`
- **Centralized Handler**: `app/src/main/java/com/aarevalo/tasky/core/presentation/MainActivity.kt` (line ~172)
- **Example Screen**: `app/src/main/java/com/aarevalo/tasky/auth/presentation/login/LoginScreen.kt` (line 46-89)
- **NavHost Wiring**: `app/src/main/java/com/aarevalo/tasky/core/presentation/MainActivity.kt` (line ~96-166)

---

## Final Tip

When showing this in an interview:

1. **Start with the problem**: "Screens with NavController are hard to test"
2. **Show the solution**: "Emit navigation events instead"
3. **Demonstrate the benefit**: "Now screens are pure and testable"
4. **Show the code**: Open LoginScreen and MainActivity side-by-side
5. **Mention scalability**: "Easy to add analytics, deep linking, etc."

This shows you don't just write code—you **architect systems** with testability and maintainability in mind. That's exactly what companies like Neo Financial are looking for! 🚀
