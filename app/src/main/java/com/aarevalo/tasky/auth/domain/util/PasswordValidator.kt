package com.aarevalo.tasky.auth.domain.util

import com.aarevalo.tasky.auth.domain.model.AuthConstants

object PasswordValidator {
    fun validate(password: String): PasswordValidationResult {
        val isValidLength = password.length >= AuthConstants.MIN_PASSWORD_LENGTH
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }

        val isValid = isValidLength && hasUpperCase && hasLowerCase && hasDigit
        val errorMessage = if (isValid) {
            null
        } else {
            buildString {
                if (!isValidLength) appendLine("Must be at least ${AuthConstants.MIN_PASSWORD_LENGTH} characters long.")
                if (!hasUpperCase) appendLine("Must contain at least one uppercase letter.")
                if (!hasLowerCase) appendLine("Must contain at least one lowercase letter.")
                if (!hasDigit) appendLine("Must contain at least one digit.")
            }
        }
        return PasswordValidationResult(isValid, errorMessage)
    }
}

data class PasswordValidationResult(val isValid: Boolean, val errorMessage: String?)