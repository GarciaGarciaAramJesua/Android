package com.escom.gestordearchivos.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.escom.gestordearchivos.ui.theme.AppTheme

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object ThemePreferences {
    private val IS_LIGHT_MODE = booleanPreferencesKey("is_light_mode")
    private val SELECTED_THEME = stringPreferencesKey("selected_theme")

    suspend fun saveIsLightMode(context: Context, isLight: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_LIGHT_MODE] = isLight
        }
    }

    fun isLightModeFlow(context: Context): Flow<Boolean> =
        context.dataStore.data.map { prefs -> prefs[IS_LIGHT_MODE] ?: true }

    suspend fun saveSelectedTheme(context: Context, theme: AppTheme) {
        context.dataStore.edit { prefs ->
            prefs[SELECTED_THEME] = theme.name
        }
    }

    fun selectedThemeFlow(context: Context): Flow<AppTheme> =
        context.dataStore.data.map { prefs ->
            prefs[SELECTED_THEME]?.let { name ->
                try {
                    AppTheme.valueOf(name)
                } catch (e: Exception) {
                    AppTheme.GUINDA_IPN
                }
            } ?: AppTheme.GUINDA_IPN
        }
}
