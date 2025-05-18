package com.aarevalo.tasky.auth.domain.model

data class User(
    val name: String ? = null,
    val email: String ,
    val password: String
)
