package com.aarevalo.tasky.auth.domain.model

data class RegisterUser(
    val name: String,
    val email: String ? = null,
    val password: String
)
