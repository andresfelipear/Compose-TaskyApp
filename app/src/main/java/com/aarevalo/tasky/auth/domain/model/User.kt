package com.aarevalo.tasky.auth.domain.model

data class User(
    val fullName: String ? = null,
    val email: String,
    val password: String
)
