package com.aarevalo.tasky.auth.presentation.login

sealed interface LoginScreenAction {
    data class OnEmailChanged(val email: String) : LoginScreenAction
    data class OnPasswordVisibilityChanged(val isPasswordVisible: Boolean) : LoginScreenAction
    data object OnLogin : LoginScreenAction
    data object OnGoToRegister : LoginScreenAction
}