package com.aarevalo.tasky.agenda.data.remote.api

import retrofit2.Response
import retrofit2.http.GET

interface TaskyAgendaApi {

    @GET("authenticate")
    suspend fun checkIfUserIsAuthenticated(): Response<Unit>

    @GET("logout")
    suspend fun logout(): Response<Unit>
}