package com.aarevalo.tasky.auth.domain.util

import com.aarevalo.tasky.auth.domain.model.PasswordValidationResult

interface InputValidator {
    fun isValidEmailPattern(email: String): Boolean
    fun isValidPassword(password: String): PasswordValidationResult
}