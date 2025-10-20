package com.aarevalo.tasky.auth.domain

import com.aarevalo.tasky.auth.domain.model.PasswordValidationResult
import com.aarevalo.tasky.auth.domain.util.InputValidator

/**
 * Fake implementation of InputValidator for testing.
 * Can be configured to return specific validation results.
 */
class FakeInputValidator : InputValidator {
    
    // Control flags
    var isEmailValid = true
    var passwordValidationResult = PasswordValidationResult(
        isValid = true,
        errorMessage = emptyList()
    )
    
    override fun isValidEmailPattern(email: String): Boolean {
        return isEmailValid
    }
    
    override fun isValidPassword(password: String): PasswordValidationResult {
        return passwordValidationResult
    }
    
    // Helper methods for test setup
    fun reset() {
        isEmailValid = true
        passwordValidationResult = PasswordValidationResult(
            isValid = true,
            errorMessage = emptyList()
        )
    }
    
    fun simulateInvalidEmail() {
        isEmailValid = false
    }
    
    fun simulateWeakPassword() {
        passwordValidationResult = PasswordValidationResult(
            isValid = false,
            errorMessage = emptyList()
        )
    }
}

