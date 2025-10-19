package com.aarevalo.tasky.auth.domain

import com.aarevalo.tasky.auth.domain.model.User
import com.aarevalo.tasky.auth.domain.repository.AuthenticationRepository
import com.aarevalo.tasky.core.domain.util.DataError
import com.aarevalo.tasky.core.domain.util.EmptyResult
import com.aarevalo.tasky.core.domain.util.Result

/**
 * Fake implementation of AuthenticationRepository for testing.
 * Allows tests to control the behavior and verify interactions.
 */
class FakeAuthRepository : AuthenticationRepository {
    
    // Control flags
    var shouldReturnError = false
    var errorToReturn: DataError.Network = DataError.Network.UNKNOWN
    
    // Track method calls
    val loginCalls = mutableListOf<Pair<String, String>>()
    val registerCalls = mutableListOf<User>()
    
    override suspend fun login(email: String, password: String): EmptyResult<DataError.Network> {
        loginCalls.add(email to password)
        
        return if (shouldReturnError) {
            Result.Error(errorToReturn)
        } else {
            Result.Success(Unit)
        }
    }
    
    override suspend fun register(user: User): EmptyResult<DataError.Network> {
        registerCalls.add(user)
        
        return if (shouldReturnError) {
            Result.Error(errorToReturn)
        } else {
            Result.Success(Unit)
        }
    }
    
    // Helper methods for test setup
    fun reset() {
        shouldReturnError = false
        errorToReturn = DataError.Network.UNKNOWN
        loginCalls.clear()
        registerCalls.clear()
    }
    
    fun simulateNetworkError() {
        shouldReturnError = true
        errorToReturn = DataError.Network.NO_INTERNET
    }
    
    fun simulateUnauthorized() {
        shouldReturnError = true
        errorToReturn = DataError.Network.UNAUTHORIZED
    }
    
    fun simulateServerError() {
        shouldReturnError = true
        errorToReturn = DataError.Network.SERVER_ERROR
    }
}

