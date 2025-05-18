package com.aarevalo.tasky.auth.data.util

import android.util.Patterns
import com.aarevalo.tasky.R
import com.aarevalo.tasky.auth.domain.model.PasswordValidationResult
import com.aarevalo.tasky.auth.domain.util.InputValidator
import com.aarevalo.tasky.core.presentation.util.UiText

class InputValidatorImpl: InputValidator {
    override fun isValidEmailPattern(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    }

    override fun isValidPassword(password: String): PasswordValidationResult {
        val isValidLength = password.length >= MIN_PASSWORD_LENGTH
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }

        val isValid = isValidLength && hasUpperCase && hasLowerCase && hasDigit
        val errorMessage: List<UiText>? = if (isValid) {
            null
        } else {
            val errorMessages = mutableListOf<UiText>()
            if (!isValidLength) {
                errorMessages.add(UiText.StringResource(R.string.password_error_length, arrayOf(MIN_PASSWORD_LENGTH)))
            }
            if (!hasUpperCase) {
                errorMessages.add(UiText.StringResource(R.string.password_error_uppercase))
            }
            if (!hasLowerCase) {
                errorMessages.add(UiText.StringResource(R.string.password_error_lowercase))
            }
            if (!hasDigit) {
                errorMessages.add(UiText.StringResource(R.string.password_error_digit))
            }
            errorMessages
        }
        return PasswordValidationResult(isValid, errorMessage)
    }

    companion object{
        const val MIN_PASSWORD_LENGTH = 9
    }
}