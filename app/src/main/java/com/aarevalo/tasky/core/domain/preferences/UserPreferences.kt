package com.aarevalo.tasky.core.domain.preferences

interface UserPreferences {
    fun saveUserId(userId: String)
    fun loadUserId(): String?
    fun saveFirstName(firstName: String)
    fun loadFirstName(): String?
    fun saveLastName(lastName: String)
    fun loadLastName(): String?
    fun saveEmail(email: String)
    fun loadEmail(): String?
    fun clearAll()

    companion object {
        const val KEY_USER_ID = "user_id"
        const val KEY_FIRST_NAME = "first_name"
        const val KEY_LAST_NAME = "last_name"
        const val KEY_EMAIL = "email"
    }
}