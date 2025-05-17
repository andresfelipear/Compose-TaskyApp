package com.aarevalo.tasky.auth.data.remote.api

import com.aarevalo.tasky.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Headers

interface TaskyAuthenticatedApi {

    @Headers("x-api-key: ${BuildConfig.API_KEY}")
    @GET("authenticate")
    suspend fun checkIfUserIsAuthenticated()

    @Headers("x-api-key: ${BuildConfig.API_KEY}")
    @GET("logout")
    suspend fun logout()
}