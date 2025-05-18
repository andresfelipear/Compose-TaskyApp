package com.aarevalo.tasky.auth.data.repository

import com.aarevalo.tasky.auth.data.remote.api.TaskyAuthApi
import com.aarevalo.tasky.auth.data.remote.dto.toRegisterRequest
import com.aarevalo.tasky.auth.domain.model.RegisterUser
import com.aarevalo.tasky.auth.domain.repository.AuthenticationRepository
import com.aarevalo.tasky.core.data.networking.responseToResult
import com.aarevalo.tasky.core.domain.preferences.SessionStorage
import com.aarevalo.tasky.core.domain.util.DataError
import com.aarevalo.tasky.core.domain.util.EmptyResult
import javax.inject.Inject


class AuthenticationRepositoryImpl @Inject constructor(
    private val api: TaskyAuthApi,
    private val sessionStorage: SessionStorage
): AuthenticationRepository {

    override suspend fun register(user: RegisterUser): EmptyResult<DataError.Network> {
        val request = user.toRegisterRequest()
        val response = api.register(request)
        return responseToResult(response)
    }

    override suspend fun login(user: RegisterUser) {
        TODO("Not yet implemented")
    }

}