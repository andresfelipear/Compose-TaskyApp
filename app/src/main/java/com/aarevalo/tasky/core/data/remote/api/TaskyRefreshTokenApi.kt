package com.aarevalo.tasky.core.data.remote.api

import com.aarevalo.tasky.BuildConfig
import com.aarevalo.tasky.core.data.remote.dto.AccessTokenRequest
import com.aarevalo.tasky.core.data.remote.dto.AccessTokenResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface TaskyRefreshTokenApi {

    @Headers("x-api-key: ${BuildConfig.API_KEY}")
    @POST("accessToken")
    suspend fun getAccessToken(
        @Body request: AccessTokenRequest
    ): AccessTokenResponse
}