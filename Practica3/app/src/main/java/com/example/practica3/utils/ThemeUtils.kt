package com.example.practica3.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.practica3.R
import com.example.practica3.data.model.ThemeType

object ThemeUtils {
    
    fun applyTheme(activity: Activity, themeType: ThemeType) {
        val themeResId = when (themeType) {
            ThemeType.IPN -> R.style.Theme_FileExplorer_IPN
            ThemeType.ESCOM -> R.style.Theme_FileExplorer_ESCOM
            ThemeType.AUTO -> {
                // Use system theme with IPN colors as default
                R.style.Theme_FileExplorer_IPN
            }
        }
        
        activity.setTheme(themeResId)
    }

    fun isDarkModeEnabled(context: Context): Boolean {
        return when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            else -> false
        }
    }

    fun setNightMode(themeType: ThemeType) {
        when (themeType) {
            ThemeType.AUTO -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            else -> {
                // For specific themes, follow system
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    fun getThemeDisplayName(context: Context, themeType: ThemeType): String {
        return when (themeType) {
            ThemeType.IPN -> context.getString(R.string.theme_ipn)
            ThemeType.ESCOM -> context.getString(R.string.theme_escom)
            ThemeType.AUTO -> context.getString(R.string.theme_auto)
        }
    }
}