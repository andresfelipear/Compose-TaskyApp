package com.aarevalo.tasky.core.data.remote.api

import com.aarevalo.tasky.core.data.remote.dto.RefreshTokenRequest
import com.aarevalo.tasky.core.data.remote.dto.RefreshTokenResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface TaskyRefreshTokenApi {

    @POST("auth/refresh")
    suspend fun refresh(
        @Body request: RefreshTokenRequest
    ): RefreshTokenResponse
}