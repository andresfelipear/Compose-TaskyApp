package com.aarevalo.tasky.core.data.remote.api

import com.aarevalo.tasky.core.data.remote.dto.AccessTokenRequest
import com.aarevalo.tasky.core.data.remote.dto.AccessTokenResponse
import com.aarevalo.tasky.auth.data.remote.dto.LoginRequest
import com.aarevalo.tasky.auth.data.remote.dto.LoginResponse
import com.aarevalo.tasky.auth.data.remote.dto.RegisterRequest
import retrofit2.http.GET
import retrofit2.http.POST

interface RefreshTokenApi {
    @POST("/accessToken")
    suspend fun getAccessToken(request: AccessTokenRequest): AccessTokenResponse
}