package com.aarevalo.tasky.auth.domain.model

import com.aarevalo.tasky.core.util.UiText

data class PasswordValidationResult(
    val isValid: Boolean,
    val errorMessage: List<UiText>?
)
