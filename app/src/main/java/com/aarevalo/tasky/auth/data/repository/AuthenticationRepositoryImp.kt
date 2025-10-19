package com.aarevalo.tasky.auth.data.repository

import com.aarevalo.tasky.auth.data.remote.api.TaskyAuthApi
import com.aarevalo.tasky.auth.data.remote.dto.LoginRequest
import com.aarevalo.tasky.auth.data.remote.dto.toRegisterRequest
import com.aarevalo.tasky.auth.domain.model.User
import com.aarevalo.tasky.auth.domain.repository.AuthenticationRepository
import com.aarevalo.tasky.core.data.networking.responseToResult
import com.aarevalo.tasky.core.domain.preferences.SessionStorage
import com.aarevalo.tasky.core.domain.user.AuthenticatedUser
import com.aarevalo.tasky.core.domain.util.DataError
import com.aarevalo.tasky.core.domain.util.Result
import com.aarevalo.tasky.core.domain.util.EmptyResult
import com.aarevalo.tasky.core.domain.util.asEmptyDataResult
import javax.inject.Inject


class AuthenticationRepositoryImpl @Inject constructor(
    private val api: TaskyAuthApi,
    private val sessionStorage: SessionStorage
): AuthenticationRepository {

    override suspend fun register(user: User): EmptyResult<DataError.Network> {
        val request = user.toRegisterRequest()
        val response = api.register(request)
        return responseToResult(response)
    }

    override suspend fun login(email: String, password: String): EmptyResult<DataError.Network> {
        val request = LoginRequest(email, password)
        val response = responseToResult(api.login(request))
        if (response is Result.Success) {
            sessionStorage.setSession(
                AuthenticatedUser(
                    accessToken = response.data.accessToken,
                    refreshToken = response.data.refreshToken,
                    userId = response.data.userId,
                    fullName = response.data.fullName,
                    accessTokenExpirationTimestamp = response.data.accessTokenExpirationTimestamp
                )
            )
            println("Session set!")
            println(sessionStorage.getSession())
        }
        return response.asEmptyDataResult()
    }
}