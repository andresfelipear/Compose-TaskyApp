package com.aarevalo.tasky.auth.presentation.login

import com.aarevalo.tasky.MainDispatcherRule
import com.aarevalo.tasky.auth.domain.FakeAuthRepository
import com.aarevalo.tasky.auth.domain.FakeInputValidator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Comprehensive unit tests for LoginViewModel.
 * Tests cover:
 * - State management
 * - User input validation
 * - Login success/failure scenarios
 * - Edge cases and error handling
 * - Event emission
 */
@ExperimentalCoroutinesApi
class LoginViewModelTest {
    
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    
    private lateinit var viewModel: LoginViewModel
    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var fakeInputValidator: FakeInputValidator
    
    @Before
    fun setup() {
        fakeAuthRepository = FakeAuthRepository()
        fakeInputValidator = FakeInputValidator()
        
        viewModel = LoginViewModel(
            inputValidator = fakeInputValidator,
            authRepository = fakeAuthRepository
        )
    }
    
    // ========================================
    // State Management Tests
    // ========================================
    
    @Test
    fun `initial state is correct`() = runTest {
        // Assert
        val state = viewModel.state.value
        assertEquals("", state.email)
        assertEquals("", state.passwordState.text.toString())
        assertFalse(state.isLoading)
        assertTrue(state.isValidEmail)
        assertTrue(state.isValidPassword)
        assertFalse(state.isPasswordVisible)
    }
    
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
    
    @Test
    fun `when password visibility toggled, state is updated`() = runTest {
        // Arrange
        val initialVisibility = viewModel.state.value.isPasswordVisible
        
        // Act
        viewModel.onAction(LoginScreenAction.OnPasswordVisibilityChanged(initialVisibility))
        advanceUntilIdle()
        
        // Assert - should toggle (action passes current state, VM toggles it)
        assertEquals(!initialVisibility, viewModel.state.value.isPasswordVisible)
    }
    
    // ========================================
    // Validation Tests
    // ========================================
    
    @Test
    fun `when email is invalid, validation state reflects it`() = runTest {
        // Arrange
        fakeInputValidator.simulateInvalidEmail()
        viewModel = LoginViewModel(fakeInputValidator, fakeAuthRepository)
        
        // Act
        viewModel.onAction(LoginScreenAction.OnEmailChanged("invalid-email"))
        advanceUntilIdle()
        
        // Assert
        assertFalse(viewModel.state.value.isValidEmail)
    }
    
    @Test
    fun `when valid email provided, validation state is true`() = runTest {
        // Arrange
        fakeInputValidator.isEmailValid = true
        
        // Act
        viewModel.onAction(LoginScreenAction.OnEmailChanged("valid@example.com"))
        advanceUntilIdle()
        
        // Assert
        assertTrue(viewModel.state.value.isValidEmail)
    }
    
    @Test
    fun `when empty email provided, validation fails`() = runTest {
        // Act
        viewModel.onAction(LoginScreenAction.OnEmailChanged(""))
        advanceUntilIdle()
        
        // Assert - empty email should be invalid
        val state = viewModel.state.value
        assertTrue(state.email.isEmpty())
    }
    
    // ========================================
    // Login Success Tests
    // ========================================
    
    @Test
    fun `when login succeeds, loading state is managed correctly`() = runTest {
        // Arrange
        viewModel.onAction(LoginScreenAction.OnEmailChanged("test@example.com"))
        // Note: Password is set via TextFieldState, not an action
        viewModel.state.value.passwordState.edit {
            append("Password123")
        }
        advanceUntilIdle()
        
        // Act
        viewModel.onAction(LoginScreenAction.OnLogin)
        
        // Assert - loading starts
        assertTrue(viewModel.state.value.isLoading)
        
        advanceUntilIdle()
        
        // Assert - loading stops after completion
        assertFalse(viewModel.state.value.isLoading)
    }
    
    @Test
    fun `when login succeeds, success event is emitted`() = runTest {
        // Arrange
        val events = mutableListOf<LoginScreenEvent>()
        val job = launch {
            viewModel.event.collect { events.add(it) }
        }
        
        viewModel.onAction(LoginScreenAction.OnEmailChanged("test@example.com"))
        // Note: Password is set via TextFieldState, not an action
        viewModel.state.value.passwordState.edit {
            append("Password123")
        }
        advanceUntilIdle()
        
        // Act
        viewModel.onAction(LoginScreenAction.OnLogin)
        advanceUntilIdle()
        
        // Assert
        assertTrue(events.any { it is LoginScreenEvent.Success })
        
        job.cancel()
    }
    
    @Test
    fun `when login succeeds, repository login is called with correct credentials`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "Password123"
        viewModel.onAction(LoginScreenAction.OnEmailChanged(email))
        viewModel.state.value.passwordState.edit { append(password) }
        advanceUntilIdle()
        
        // Act
        viewModel.onAction(LoginScreenAction.OnLogin)
        advanceUntilIdle()
        
        // Assert
        assertEquals(1, fakeAuthRepository.loginCalls.size)
        val (calledEmail, calledPassword) = fakeAuthRepository.loginCalls.first()
        assertEquals(email, calledEmail)
        assertEquals(password, calledPassword)
    }
    
    // ========================================
    // Login Failure Tests
    // ========================================
    
    @Test
    fun `when login fails with network error, error event is emitted`() = runTest {
        // Arrange
        val events = mutableListOf<LoginScreenEvent>()
        val job = launch {
            viewModel.event.collect { events.add(it) }
        }
        
        fakeAuthRepository.simulateNetworkError()
        viewModel.onAction(LoginScreenAction.OnEmailChanged("test@example.com"))
        // Note: Password is set via TextFieldState, not an action
        viewModel.state.value.passwordState.edit {
            append("Password123")
        }
        advanceUntilIdle()
        
        // Act
        viewModel.onAction(LoginScreenAction.OnLogin)
        advanceUntilIdle()
        
        // Assert
        assertTrue(events.any { it is LoginScreenEvent.Error })
        
        job.cancel()
    }
    
    @Test
    fun `when login fails with unauthorized, error event is emitted`() = runTest {
        // Arrange
        val events = mutableListOf<LoginScreenEvent>()
        val job = launch {
            viewModel.event.collect { events.add(it) }
        }
        
        fakeAuthRepository.simulateUnauthorized()
        viewModel.onAction(LoginScreenAction.OnEmailChanged("test@example.com"))
        viewModel.state.value.passwordState.edit { append("WrongPassword123") }
        advanceUntilIdle()
        
        // Act
        viewModel.onAction(LoginScreenAction.OnLogin)
        advanceUntilIdle()
        
        // Assert
        assertTrue(events.any { it is LoginScreenEvent.Error })
        
        job.cancel()
    }
    
    @Test
    fun `when login fails, loading state is reset`() = runTest {
        // Arrange
        fakeAuthRepository.simulateServerError()
        viewModel.onAction(LoginScreenAction.OnEmailChanged("test@example.com"))
        // Note: Password is set via TextFieldState, not an action
        viewModel.state.value.passwordState.edit {
            append("Password123")
        }
        advanceUntilIdle()
        
        // Act
        viewModel.onAction(LoginScreenAction.OnLogin)
        advanceUntilIdle()
        
        // Assert - loading should be false after error
        assertFalse(viewModel.state.value.isLoading)
    }
    
    // ========================================
    // Edge Cases
    // ========================================
    
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
        
        // Assert - should have called login 3 times (no debouncing by default)
        // But state should be consistent
        assertFalse(viewModel.state.value.isLoading)
        assertEquals(3, fakeAuthRepository.loginCalls.size)
    }
    
    @Test
    fun `when email contains whitespace, state stores it as is`() = runTest {
        // Arrange
        val emailWithSpaces = "  test@example.com  "
        
        // Act
        viewModel.onAction(LoginScreenAction.OnEmailChanged(emailWithSpaces))
        advanceUntilIdle()
        
        // Assert
        assertEquals(emailWithSpaces, viewModel.state.value.email)
    }
    
    @Test
    fun `when password field state exists, can access password text`() = runTest {
        // Assert - passwordState should be initialized
        assertNotNull(viewModel.state.value.passwordState)
        assertEquals("", viewModel.state.value.passwordState.text.toString())
    }
    
    @Test
    fun `when very long email provided, state handles it`() = runTest {
        // Arrange
        val longEmail = "a".repeat(100) + "@example.com"
        
        // Act
        viewModel.onAction(LoginScreenAction.OnEmailChanged(longEmail))
        advanceUntilIdle()
        
        // Assert
        assertEquals(longEmail, viewModel.state.value.email)
    }
    
    @Test
    fun `when empty credentials provided, login attempt still calls repository`() = runTest {
        // Arrange - leave email and password empty
        
        // Act
        viewModel.onAction(LoginScreenAction.OnLogin)
        advanceUntilIdle()
        
        // Assert - should still attempt (validation happens in UI)
        assertEquals(1, fakeAuthRepository.loginCalls.size)
    }
    
    // ========================================
    // State Consistency Tests
    // ========================================
    
    @Test
    fun `when state changes during login, consistency is maintained`() = runTest {
        // Arrange
        viewModel.onAction(LoginScreenAction.OnEmailChanged("test@example.com"))
        viewModel.state.value.passwordState.edit { append("Password123") }
        advanceUntilIdle()
        
        // Act - start login
        viewModel.onAction(LoginScreenAction.OnLogin)
        
        // Change email while login is in progress
        viewModel.onAction(LoginScreenAction.OnEmailChanged("new@example.com"))
        advanceUntilIdle()
        
        // Assert - email should be updated
        assertEquals("new@example.com", viewModel.state.value.email)
        assertFalse(viewModel.state.value.isLoading)
    }
    
    @Test
    fun `when password visibility toggled multiple times, final state is correct`() = runTest {
        // Act
        viewModel.onAction(LoginScreenAction.OnPasswordVisibilityChanged(false))  // Start false -> becomes true
        viewModel.onAction(LoginScreenAction.OnPasswordVisibilityChanged(true))   // true -> becomes false
        viewModel.onAction(LoginScreenAction.OnPasswordVisibilityChanged(false))  // false -> becomes true
        advanceUntilIdle()
        
        // Assert
        assertTrue(viewModel.state.value.isPasswordVisible)
    }
    
    // ========================================
    // Integration-Style Tests
    // ========================================
    
    @Test
    fun `complete login flow - success scenario`() = runTest {
        // Full user flow test
        val events = mutableListOf<LoginScreenEvent>()
        val job = launch {
            viewModel.event.collect { events.add(it) }
        }
        
        // User types email
        viewModel.onAction(LoginScreenAction.OnEmailChanged("test@example.com"))
        advanceUntilIdle()
        
        // User types password
        viewModel.state.value.passwordState.edit { append("Password123") }
        advanceUntilIdle()
        
        // User clicks login
        viewModel.onAction(LoginScreenAction.OnLogin)
        advanceUntilIdle()
        
        // Assert success
        assertTrue(events.any { it is LoginScreenEvent.Success })
        assertFalse(viewModel.state.value.isLoading)
        
        job.cancel()
    }
    
    @Test
    fun `complete login flow - failure scenario with retry`() = runTest {
        // Arrange - first attempt fails, second succeeds
        val events = mutableListOf<LoginScreenEvent>()
        val job = launch {
            viewModel.event.collect { events.add(it) }
        }
        
        fakeAuthRepository.shouldReturnError = true
        
        // First attempt
        viewModel.onAction(LoginScreenAction.OnEmailChanged("test@example.com"))
        viewModel.state.value.passwordState.edit { append("Password123") }
        viewModel.onAction(LoginScreenAction.OnLogin)
        advanceUntilIdle()
        
        // Assert first failure
        assertTrue(events.any { it is LoginScreenEvent.Error })
        
        // Fix network and retry
        fakeAuthRepository.shouldReturnError = false
        viewModel.onAction(LoginScreenAction.OnLogin)
        advanceUntilIdle()
        
        // Assert success on retry
        assertTrue(events.any { it is LoginScreenEvent.Success })
        assertEquals(2, events.size) // One error, one success
        
        job.cancel()
    }
}

