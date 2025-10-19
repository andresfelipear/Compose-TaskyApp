package com.aarevalo.tasky.core.domain

import com.aarevalo.tasky.core.domain.preferences.SessionStorage
import com.aarevalo.tasky.core.domain.user.AuthenticatedUser

/**
 * Fake implementation of SessionStorage for testing.
 * Stores session in memory for easy testing.
 */
class FakeSessionStorage : SessionStorage {
    
    private var currentSession: AuthenticatedUser? = null
    
    // Track method calls
    val setSessionCalls = mutableListOf<AuthenticatedUser?>()
    val getSessionCalls = mutableListOf<Unit>()
    
    override suspend fun setSession(session: AuthenticatedUser?) {
        setSessionCalls.add(session)
        currentSession = session
    }
    
    override suspend fun getSession(): AuthenticatedUser? {
        getSessionCalls.add(Unit)
        return currentSession
    }
    
    // Helper methods for test setup
    fun reset() {
        currentSession = null
        setSessionCalls.clear()
        getSessionCalls.clear()
    }
    
    fun setSession(user: AuthenticatedUser) {
        currentSession = user
    }
    
    fun clearSession() {
        currentSession = null
    }
    
    fun hasSession(): Boolean {
        return currentSession != null
    }
}

