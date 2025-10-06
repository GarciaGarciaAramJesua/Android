package com.example.practica3.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.practica3.data.model.SortType
import com.example.practica3.data.model.ThemeType
import com.example.practica3.data.model.ViewType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "file_explorer_preferences")

@Singleton
class PreferencesManager @Inject constructor(
    private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        private val THEME_TYPE = stringPreferencesKey("theme_type")
        private val VIEW_TYPE = stringPreferencesKey("view_type")
        private val SORT_TYPE = stringPreferencesKey("sort_type")
        private val SHOW_HIDDEN_FILES = booleanPreferencesKey("show_hidden_files")
        private val LAST_DIRECTORY = stringPreferencesKey("last_directory")
        private val GRID_SPAN_COUNT = intPreferencesKey("grid_span_count")
    }

    val themeType: Flow<ThemeType> = dataStore.data.map { preferences ->
        try {
            ThemeType.valueOf(preferences[THEME_TYPE] ?: ThemeType.IPN.name)
        } catch (e: IllegalArgumentException) {
            ThemeType.IPN
        }
    }

    val viewType: Flow<ViewType> = dataStore.data.map { preferences ->
        try {
            ViewType.valueOf(preferences[VIEW_TYPE] ?: ViewType.LIST.name)
        } catch (e: IllegalArgumentException) {
            ViewType.LIST
        }
    }

    val sortType: Flow<SortType> = dataStore.data.map { preferences ->
        try {
            SortType.valueOf(preferences[SORT_TYPE] ?: SortType.NAME_ASC.name)
        } catch (e: IllegalArgumentException) {
            SortType.NAME_ASC
        }
    }

    val showHiddenFiles: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[SHOW_HIDDEN_FILES] ?: false
    }

    val lastDirectory: Flow<String?> = dataStore.data.map { preferences ->
        preferences[LAST_DIRECTORY]
    }

    val gridSpanCount: Flow<Int> = dataStore.data.map { preferences ->
        preferences[GRID_SPAN_COUNT] ?: 2
    }

    suspend fun setThemeType(themeType: ThemeType) {
        dataStore.edit { preferences ->
            preferences[THEME_TYPE] = themeType.name
        }
    }

    suspend fun setViewType(viewType: ViewType) {
        dataStore.edit { preferences ->
            preferences[VIEW_TYPE] = viewType.name
        }
    }

    suspend fun setSortType(sortType: SortType) {
        dataStore.edit { preferences ->
            preferences[SORT_TYPE] = sortType.name
        }
    }

    suspend fun setShowHiddenFiles(show: Boolean) {
        dataStore.edit { preferences ->
            preferences[SHOW_HIDDEN_FILES] = show
        }
    }

    suspend fun setLastDirectory(path: String) {
        dataStore.edit { preferences ->
            preferences[LAST_DIRECTORY] = path
        }
    }

    suspend fun setGridSpanCount(count: Int) {
        dataStore.edit { preferences ->
            preferences[GRID_SPAN_COUNT] = count
        }
    }
}