package com.aarevalo.tasky.core.domain.preferences

import com.aarevalo.tasky.core.domain.user.AuthenticatedUser

interface SessionStorage {
    suspend fun getSession(): AuthenticatedUser?
    suspend fun setSession(session: AuthenticatedUser?)
}