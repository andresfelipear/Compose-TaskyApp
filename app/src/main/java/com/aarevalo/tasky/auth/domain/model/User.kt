package com.aarevalo.tasky.auth.domain.model

data class User(
    val name: String,
    val email: String ? = null,
    val password: String
)
