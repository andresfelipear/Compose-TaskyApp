package com.aarevalo.tasky.auth.presentation.register

sealed interface RegistrationAction {
    data class NameChanged(val name: String): RegistrationAction
    data class EmailChanged(val email: String): RegistrationAction
    data class PasswordVisibilityChanged(val isPasswordVisible: Boolean): RegistrationAction
    data object Register: RegistrationAction
    data object Login: RegistrationAction
}
