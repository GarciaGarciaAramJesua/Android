package com.example.wearable.data.model

import java.time.LocalTime

/**
 * Configuración de recordatorios de hidratación
 */
data class WaterReminderSettings(
    val enabled: Boolean = true,
    val intervalMinutes: Int = 60, // cada 60 minutos
    val startTime: LocalTime = LocalTime.of(8, 0), // 8:00 AM
    val endTime: LocalTime = LocalTime.of(22, 0), // 10:00 PM
    val dailyGoal: Int = 2000, // ml por día
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true
)

/**
 * Configuración de recordatorios de pausas activas
 */
data class ActiveBreakReminderSettings(
    val enabled: Boolean = true,
    val intervalMinutes: Int = 120, // cada 2 horas
    val startTime: LocalTime = LocalTime.of(9, 0), // 9:00 AM
    val endTime: LocalTime = LocalTime.of(18, 0), // 6:00 PM
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val preferredBreakTypes: List<String> = PredefinedBreaks.getAll().map { it.id }
)

/**
 * Configuración general de la aplicación
 */
data class AppSettings(
    val waterReminder: WaterReminderSettings = WaterReminderSettings(),
    val activeBreakReminder: ActiveBreakReminderSettings = ActiveBreakReminderSettings(),
    val useMetricSystem: Boolean = true,
    val darkModeEnabled: Boolean = true
)
