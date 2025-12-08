package com.example.action.util

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREF_NAME = "ActionAppSession"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_IS_ADMIN = "is_admin"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }
    
    fun saveLoginSession(userId: Int, username: String, isAdmin: Boolean) {
        prefs.edit().apply {
            putInt(KEY_USER_ID, userId)
            putString(KEY_USERNAME, username)
            putBoolean(KEY_IS_ADMIN, isAdmin)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }
    
    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, -1)
    
    fun getUsername(): String? = prefs.getString(KEY_USERNAME, null)
    
    fun isAdmin(): Boolean = prefs.getBoolean(KEY_IS_ADMIN, false)
    
    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    
    fun logout() {
        prefs.edit().clear().apply()
    }
}
