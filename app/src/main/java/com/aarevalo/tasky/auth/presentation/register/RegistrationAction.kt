package com.aarevalo.tasky.auth.presentation.register

sealed interface RegistrationAction {
    data class OnNameChanged(val name: String): RegistrationAction
    data class OnEmailChanged(val email: String): RegistrationAction
    data class OnPasswordVisibilityChanged(val isPasswordVisible: Boolean): RegistrationAction
    data object OnRegister: RegistrationAction
    data object OnGoToLogin: RegistrationAction
    data class OnPasswordChanged(val password: String): RegistrationAction
}
