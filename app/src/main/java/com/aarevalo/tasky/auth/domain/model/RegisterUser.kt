package com.aarevalo.tasky.auth.domain.model

data class RegisterUser(
    val fullName: String ? = "",
    val email: String,
    val password: String
)
