package com.aarevalo.tasky.core.data.remote.api

import com.aarevalo.tasky.core.data.remote.dto.AccessTokenRequest
import com.aarevalo.tasky.core.data.remote.dto.AccessTokenResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface TaskyRefreshTokenApi {

    @POST("accessToken")
    suspend fun getAccessToken(
        @Body request: AccessTokenRequest
    ): AccessTokenResponse
}