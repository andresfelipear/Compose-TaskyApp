package com.aarevalo.tasky.core.domain.preferences

interface UserPreferences {
    fun saveUserId(userId: String)
    fun loadUserId(): String?
    fun saveFullName(fullName: String)
    fun loadFullName(): String?
    fun saveEmail(email: String)
    fun loadEmail(): String?
    fun clearAll()
}