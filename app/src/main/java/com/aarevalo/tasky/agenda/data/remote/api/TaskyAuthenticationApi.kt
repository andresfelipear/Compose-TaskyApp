package com.aarevalo.tasky.agenda.data.remote.api

import com.aarevalo.tasky.agenda.data.remote.dto.AccessTokenRequest
import com.aarevalo.tasky.agenda.data.remote.dto.AccessTokenResponse
import com.aarevalo.tasky.agenda.data.remote.dto.LoginRequest
import com.aarevalo.tasky.agenda.data.remote.dto.LoginResponse
import com.aarevalo.tasky.agenda.data.remote.dto.RegisterRequest
import retrofit2.http.GET
import retrofit2.http.POST

interface TaskyAuthenticationApi {

    @POST("/register")
    suspend fun register(request: RegisterRequest)

    @POST("/login")
    suspend fun login(request: LoginRequest): LoginResponse

    @POST("/accessToken")
    suspend fun getAccessToken(request: AccessTokenRequest): AccessTokenResponse

    @GET("/authenticate")
    suspend fun checkIfUserIsAuthenticated()

    @GET("/logout")
    suspend fun logout()
}