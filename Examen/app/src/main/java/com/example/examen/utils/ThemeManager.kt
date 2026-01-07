package com.example.examen.utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

object ThemeManager {
    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_THEME = "selected_theme"
    
    const val THEME_IPN = "ipn"
    const val THEME_ESCOM = "escom"
    
    fun saveTheme(context: Context, theme: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_THEME, theme).apply()
    }
    
    fun getTheme(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_THEME, THEME_IPN) ?: THEME_IPN
    }
    
    fun applyTheme(activity: AppCompatActivity) {
        val theme = getTheme(activity)
        when (theme) {
            THEME_IPN -> activity.setTheme(com.example.examen.R.style.Theme_Examen)
            THEME_ESCOM -> activity.setTheme(com.example.examen.R.style.Theme_Examen_ESCOM)
        }
    }
}
