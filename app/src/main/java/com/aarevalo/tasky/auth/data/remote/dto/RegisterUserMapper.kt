package com.aarevalo.tasky.auth.data.remote.dto

import com.aarevalo.tasky.auth.domain.model.User

fun RegisterRequest.toRegisterUser(): User {
    return User(
        fullName = fullName,
        email = email,
        password = password
    )
}

fun User.toRegisterRequest(): RegisterRequest {
    return RegisterRequest(
        fullName = fullName,
        email = email,
        password = password
    )
}

fun User.toLoginRequest(): LoginRequest {
    return LoginRequest(
        email = email,
        password = password
    )
}