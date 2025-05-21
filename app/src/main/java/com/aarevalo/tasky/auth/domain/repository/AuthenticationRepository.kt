package com.aarevalo.tasky.auth.domain.repository

import com.aarevalo.tasky.auth.domain.model.User
import com.aarevalo.tasky.core.domain.util.DataError
import com.aarevalo.tasky.core.domain.util.EmptyResult

interface AuthenticationRepository {
    suspend fun register(user: User): EmptyResult<DataError.Network>
    suspend fun login(user: User): EmptyResult<DataError.Network>
}