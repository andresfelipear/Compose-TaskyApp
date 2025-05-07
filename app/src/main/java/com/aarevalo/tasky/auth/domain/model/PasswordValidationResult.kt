package com.aarevalo.tasky.auth.domain.model

import com.aarevalo.tasky.core.common.ui.UiText

data class PasswordValidationResult(
    val isValid: Boolean,
    val errorMessage: List<UiText>?
)
