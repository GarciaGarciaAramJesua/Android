package com.example.practica3

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.practica3.data.database.FileExplorerDatabase
import com.example.practica3.data.preferences.PreferencesManager
import com.example.practica3.data.repository.FileRepository
import com.example.practica3.utils.ThemeUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FileExplorerApplication : Application() {
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    // Database
    val database by lazy { FileExplorerDatabase.getDatabase(this) }
    
    // Preferences
    val preferencesManager by lazy { PreferencesManager(this) }
    
    // Repository
    val fileRepository by lazy { 
        FileRepository(
            context = this,
            recentFileDao = database.recentFileDao(),
            favoriteFileDao = database.favoriteFileDao()
        )
    }

    override fun onCreate() {
        super.onCreate()
        
        // Apply saved theme
        applicationScope.launch {
            try {
                val themeType = preferencesManager.themeType.first()
                ThemeUtils.setNightMode(themeType)
            } catch (e: Exception) {
                // Use default theme if preference loading fails
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }
}