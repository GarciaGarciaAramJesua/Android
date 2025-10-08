package com.example.practica2

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity

object ThemeManager {
    
    private const val PREFS_NAME = "theme_preferences"
    private const val KEY_DARK_MODE = "isDarkModeEnabled"
    
    /**
     * Guarda la preferencia del tema oscuro
     */
    fun setDarkMode(context: Context, isDarkMode: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_DARK_MODE, isDarkMode).apply()
    }
    
    /**
     * Obtiene la preferencia del tema oscuro
     */
    fun isDarkMode(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_DARK_MODE, false) // Por defecto: modo claro
    }
    
    /**
     * Aplica el tema a una Activity
     * Debe llamarse ANTES de setContentView()
     */
    fun applyTheme(activity: AppCompatActivity) {
        val isDark = isDarkMode(activity)
        val themeRes = if (isDark) {
            R.style.Theme_Practica2_Dark
        } else {
            R.style.Theme_Practica2_Light
        }
        activity.setTheme(themeRes)
    }
    
    /**
     * Cambia el tema y reinicia la Activity
     */
    fun toggleTheme(activity: AppCompatActivity) {
        val currentMode = isDarkMode(activity)
        setDarkMode(activity, !currentMode)
        activity.recreate() // Reinicia la Activity para aplicar el nuevo tema
    }
    
    /**
     * Obtiene el ID del recurso del tema actual
     */
    fun getCurrentThemeRes(context: Context): Int {
        return if (isDarkMode(context)) {
            R.style.Theme_Practica2_Dark
        } else {
            R.style.Theme_Practica2_Light
        }
    }
}