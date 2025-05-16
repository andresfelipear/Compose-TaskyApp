package com.aarevalo.tasky.auth.data.remote.api

import com.aarevalo.tasky.BuildConfig
import com.aarevalo.tasky.auth.data.remote.dto.LoginRequest
import com.aarevalo.tasky.auth.data.remote.dto.LoginResponse
import com.aarevalo.tasky.auth.data.remote.dto.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface TaskyAuthenticationApi {

    @Headers("x-api-key: ${BuildConfig.API_KEY}")
    @POST("register")
    suspend fun register(
        @Body request: RegisterRequest
    )

    @Headers("x-api-key: ${BuildConfig.API_KEY}")
    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @Headers("x-api-key: ${BuildConfig.API_KEY}")
    @GET("authenticate")
    suspend fun checkIfUserIsAuthenticated()

    @Headers("x-api-key: ${BuildConfig.API_KEY}")
    @GET("logout")
    suspend fun logout()
}