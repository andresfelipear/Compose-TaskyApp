package com.aarevalo.tasky.auth.data.remote.dto

import com.aarevalo.tasky.auth.domain.model.RegisterUser

fun RegisterRequest.toRegisterUser(): RegisterUser {
    return RegisterUser(
        name = fullName,
        email = email,
        password = password
    )
}

fun RegisterUser.toRegisterRequest(): RegisterRequest {
    return RegisterRequest(
        fullName = name,
        email = email ?: "",
        password = password
    )
}