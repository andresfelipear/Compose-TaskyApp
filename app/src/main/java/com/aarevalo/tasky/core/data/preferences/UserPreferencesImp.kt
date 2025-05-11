package com.aarevalo.tasky.core.data.preferences

import android.content.SharedPreferences
import com.aarevalo.tasky.core.domain.preferences.UserPreferences

class UserPreferencesImp(
    private val sharedUserPreferences: SharedPreferences
): UserPreferences {

    override fun loadEmail(): String? {
        return sharedUserPreferences.getString(UserPreferences.KEY_EMAIL, null)
    }

    override fun loadLastName(): String? {
        return sharedUserPreferences.getString(UserPreferences.KEY_LAST_NAME, null)
    }

    override fun loadFirstName(): String? {
        return sharedUserPreferences.getString(UserPreferences.KEY_FIRST_NAME, null)
    }

    override fun loadUserId(): String? {
        return sharedUserPreferences.getString(UserPreferences.KEY_USER_ID, null)
    }

    override fun saveEmail(email: String) {
        sharedUserPreferences.edit()
            .putString(UserPreferences.KEY_EMAIL, email)
            .apply()
    }

    override fun saveLastName(lastName: String) {
        sharedUserPreferences.edit()
            .putString(UserPreferences.KEY_LAST_NAME, lastName)
            .apply()
    }

    override fun saveFirstName(firstName: String) {
        sharedUserPreferences.edit()
            .putString(UserPreferences.KEY_FIRST_NAME, firstName)
            .apply()
    }

    override fun saveUserId(userId: String) {
        sharedUserPreferences.edit()
            .putString(UserPreferences.KEY_USER_ID, userId)
            .apply()
    }

    override fun clearAll() {
        sharedUserPreferences.edit()
            .remove(UserPreferences.KEY_EMAIL)
            .remove(UserPreferences.KEY_LAST_NAME)
            .remove(UserPreferences.KEY_FIRST_NAME)
            .remove(UserPreferences.KEY_USER_ID)
            .apply()
    }
}