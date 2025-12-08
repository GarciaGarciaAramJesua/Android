package com.example.wearable.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.wearable.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "health_settings")

/**
 * Repositorio para gestionar datos de salud y configuración
 */
class HealthRepository(private val context: Context) {
    
    private val dataStore = context.dataStore
    
    companion object {
        private val WATER_GOAL_KEY = intPreferencesKey("water_goal")
        private val WATER_REMINDER_ENABLED_KEY = booleanPreferencesKey("water_reminder_enabled")
        private val WATER_REMINDER_INTERVAL_KEY = intPreferencesKey("water_reminder_interval")
        private val WATER_REMINDER_START_TIME_KEY = stringPreferencesKey("water_reminder_start_time")
        private val WATER_REMINDER_END_TIME_KEY = stringPreferencesKey("water_reminder_end_time")
        
        private val BREAK_REMINDER_ENABLED_KEY = booleanPreferencesKey("break_reminder_enabled")
        private val BREAK_REMINDER_INTERVAL_KEY = intPreferencesKey("break_reminder_interval")
        private val BREAK_REMINDER_START_TIME_KEY = stringPreferencesKey("break_reminder_start_time")
        private val BREAK_REMINDER_END_TIME_KEY = stringPreferencesKey("break_reminder_end_time")
        
        private val DAILY_WATER_INTAKES_KEY = stringPreferencesKey("daily_water_intakes")
        private val COMPLETED_BREAKS_KEY = stringPreferencesKey("completed_breaks")
    }
    
    // Water Reminder Settings
    suspend fun saveWaterReminderSettings(settings: WaterReminderSettings) {
        dataStore.edit { preferences ->
            preferences[WATER_GOAL_KEY] = settings.dailyGoal
            preferences[WATER_REMINDER_ENABLED_KEY] = settings.enabled
            preferences[WATER_REMINDER_INTERVAL_KEY] = settings.intervalMinutes
            preferences[WATER_REMINDER_START_TIME_KEY] = settings.startTime.toString()
            preferences[WATER_REMINDER_END_TIME_KEY] = settings.endTime.toString()
        }
    }
    
    fun getWaterReminderSettings(): Flow<WaterReminderSettings> = dataStore.data.map { preferences ->
        WaterReminderSettings(
            enabled = preferences[WATER_REMINDER_ENABLED_KEY] ?: true,
            intervalMinutes = preferences[WATER_REMINDER_INTERVAL_KEY] ?: 60,
            startTime = LocalTime.parse(preferences[WATER_REMINDER_START_TIME_KEY] ?: "08:00"),
            endTime = LocalTime.parse(preferences[WATER_REMINDER_END_TIME_KEY] ?: "22:00"),
            dailyGoal = preferences[WATER_GOAL_KEY] ?: 2000
        )
    }
    
    // Active Break Reminder Settings
    suspend fun saveActiveBreakReminderSettings(settings: ActiveBreakReminderSettings) {
        dataStore.edit { preferences ->
            preferences[BREAK_REMINDER_ENABLED_KEY] = settings.enabled
            preferences[BREAK_REMINDER_INTERVAL_KEY] = settings.intervalMinutes
            preferences[BREAK_REMINDER_START_TIME_KEY] = settings.startTime.toString()
            preferences[BREAK_REMINDER_END_TIME_KEY] = settings.endTime.toString()
        }
    }
    
    fun getActiveBreakReminderSettings(): Flow<ActiveBreakReminderSettings> = dataStore.data.map { preferences ->
        ActiveBreakReminderSettings(
            enabled = preferences[BREAK_REMINDER_ENABLED_KEY] ?: true,
            intervalMinutes = preferences[BREAK_REMINDER_INTERVAL_KEY] ?: 120,
            startTime = LocalTime.parse(preferences[BREAK_REMINDER_START_TIME_KEY] ?: "09:00"),
            endTime = LocalTime.parse(preferences[BREAK_REMINDER_END_TIME_KEY] ?: "18:00")
        )
    }
    
    // Water Intake Management
    suspend fun addWaterIntake(amount: Int) {
        val intake = WaterIntake(amount = amount)
        dataStore.edit { preferences ->
            val current = preferences[DAILY_WATER_INTAKES_KEY] ?: ""
            val intakes = if (current.isNotEmpty()) {
                current.split(";").toMutableList()
            } else {
                mutableListOf()
            }
            intakes.add("${intake.id},${intake.amount},${intake.timestamp},${intake.date}")
            preferences[DAILY_WATER_INTAKES_KEY] = intakes.joinToString(";")
        }
    }
    
    fun getDailyWaterStats(): Flow<DailyWaterStats> = dataStore.data.map { preferences ->
        val today = LocalDate.now()
        val goal = preferences[WATER_GOAL_KEY] ?: 2000
        val intakesString = preferences[DAILY_WATER_INTAKES_KEY] ?: ""
        
        val intakes = if (intakesString.isNotEmpty()) {
            intakesString.split(";").mapNotNull { entry ->
                try {
                    val parts = entry.split(",")
                    val date = LocalDate.parse(parts[3])
                    if (date == today) {
                        WaterIntake(
                            id = parts[0],
                            amount = parts[1].toInt(),
                            timestamp = LocalDateTime.parse(parts[2]),
                            date = date
                        )
                    } else null
                } catch (e: Exception) {
                    null
                }
            }
        } else {
            emptyList()
        }
        
        DailyWaterStats(
            date = today,
            totalAmount = intakes.sumOf { it.amount },
            goal = goal,
            intakes = intakes
        )
    }
    
    // Active Break Management
    suspend fun recordActiveBreak(breakType: ActiveBreak, completed: Boolean = true) {
        val record = ActiveBreakRecord(
            breakType = breakType,
            completed = completed,
            exercisesCompleted = if (completed) breakType.exercises.size else 0
        )
        
        dataStore.edit { preferences ->
            val current = preferences[COMPLETED_BREAKS_KEY] ?: ""
            val breaks = if (current.isNotEmpty()) {
                current.split(";").toMutableList()
            } else {
                mutableListOf()
            }
            breaks.add("${record.id},${record.breakType.id},${record.timestamp},${record.completed}")
            preferences[COMPLETED_BREAKS_KEY] = breaks.joinToString(";")
        }
    }
    
    fun getTodayActiveBreaks(): Flow<List<ActiveBreakRecord>> = dataStore.data.map { preferences ->
        val today = LocalDate.now()
        val breaksString = preferences[COMPLETED_BREAKS_KEY] ?: ""
        
        if (breaksString.isNotEmpty()) {
            breaksString.split(";").mapNotNull { entry ->
                try {
                    val parts = entry.split(",")
                    val timestamp = LocalDateTime.parse(parts[2])
                    if (timestamp.toLocalDate() == today) {
                        val breakType = PredefinedBreaks.getAll().find { it.id == parts[1] }
                            ?: PredefinedBreaks.quickStretch
                        ActiveBreakRecord(
                            id = parts[0],
                            breakType = breakType,
                            timestamp = timestamp,
                            completed = parts[3].toBoolean()
                        )
                    } else null
                } catch (e: Exception) {
                    null
                }
            }
        } else {
            emptyList()
        }
    }
    
    suspend fun clearOldData() {
        // Limpia datos antiguos (más de 7 días)
        dataStore.edit { preferences ->
            val cutoffDate = LocalDate.now().minusDays(7)
            
            // Limpiar water intakes
            val intakesString = preferences[DAILY_WATER_INTAKES_KEY] ?: ""
            if (intakesString.isNotEmpty()) {
                val validIntakes = intakesString.split(";").filter { entry ->
                    try {
                        val parts = entry.split(",")
                        LocalDate.parse(parts[3]).isAfter(cutoffDate)
                    } catch (e: Exception) {
                        false
                    }
                }
                preferences[DAILY_WATER_INTAKES_KEY] = validIntakes.joinToString(";")
            }
            
            // Limpiar active breaks
            val breaksString = preferences[COMPLETED_BREAKS_KEY] ?: ""
            if (breaksString.isNotEmpty()) {
                val validBreaks = breaksString.split(";").filter { entry ->
                    try {
                        val parts = entry.split(",")
                        LocalDateTime.parse(parts[2]).toLocalDate().isAfter(cutoffDate)
                    } catch (e: Exception) {
                        false
                    }
                }
                preferences[COMPLETED_BREAKS_KEY] = validBreaks.joinToString(";")
            }
        }
    }
}
