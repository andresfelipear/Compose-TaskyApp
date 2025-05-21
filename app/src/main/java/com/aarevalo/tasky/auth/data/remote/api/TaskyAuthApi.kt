package com.aarevalo.tasky.auth.data.remote.api

import com.aarevalo.tasky.auth.data.remote.dto.LoginRequest
import com.aarevalo.tasky.auth.data.remote.dto.LoginResponse
import com.aarevalo.tasky.auth.data.remote.dto.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TaskyAuthApi {
    @POST("register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<Unit>

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>
}