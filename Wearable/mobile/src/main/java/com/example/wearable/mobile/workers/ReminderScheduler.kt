package com.example.wearable.workers

import android.content.Context
import androidx.work.*
import com.example.wearable.data.model.ActiveBreakReminderSettings
import com.example.wearable.data.model.WaterReminderSettings
import java.time.Duration
import java.time.LocalTime
import java.util.concurrent.TimeUnit

/**
 * Gestor para programar recordatorios periódicos
 */
object ReminderScheduler {
    
    private const val WATER_REMINDER_WORK_NAME = "water_reminder_work"
    private const val ACTIVE_BREAK_REMINDER_WORK_NAME = "active_break_reminder_work"
    
    /**
     * Programa recordatorios de hidratación
     */
    fun scheduleWaterReminders(
        context: Context,
        settings: WaterReminderSettings
    ) {
        val workManager = WorkManager.getInstance(context)
        
        if (!settings.enabled) {
            cancelWaterReminders(context)
            return
        }
        
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()
        
        val waterReminderWork = PeriodicWorkRequestBuilder<WaterReminderWorker>(
            settings.intervalMinutes.toLong(),
            TimeUnit.MINUTES,
            15, // Flex interval
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .addTag(WATER_REMINDER_WORK_NAME)
            .setInitialDelay(calculateInitialDelay(settings.startTime), TimeUnit.MILLISECONDS)
            .build()
        
        workManager.enqueueUniquePeriodicWork(
            WATER_REMINDER_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            waterReminderWork
        )
    }
    
    /**
     * Programa recordatorios de pausas activas
     */
    fun scheduleActiveBreakReminders(
        context: Context,
        settings: ActiveBreakReminderSettings
    ) {
        val workManager = WorkManager.getInstance(context)
        
        if (!settings.enabled) {
            cancelActiveBreakReminders(context)
            return
        }
        
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()
        
        val activeBreakReminderWork = PeriodicWorkRequestBuilder<ActiveBreakReminderWorker>(
            settings.intervalMinutes.toLong(),
            TimeUnit.MINUTES,
            15, // Flex interval
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .addTag(ACTIVE_BREAK_REMINDER_WORK_NAME)
            .setInitialDelay(calculateInitialDelay(settings.startTime), TimeUnit.MILLISECONDS)
            .build()
        
        workManager.enqueueUniquePeriodicWork(
            ACTIVE_BREAK_REMINDER_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            activeBreakReminderWork
        )
    }
    
    /**
     * Cancela recordatorios de hidratación
     */
    fun cancelWaterReminders(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WATER_REMINDER_WORK_NAME)
    }
    
    /**
     * Cancela recordatorios de pausas activas
     */
    fun cancelActiveBreakReminders(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(ACTIVE_BREAK_REMINDER_WORK_NAME)
    }
    
    /**
     * Calcula el delay inicial para comenzar las notificaciones
     */
    private fun calculateInitialDelay(startTime: LocalTime): Long {
        val now = LocalTime.now()
        val duration = if (now.isBefore(startTime)) {
            Duration.between(now, startTime)
        } else {
            // Si ya pasó la hora de inicio hoy, programar para mañana
            Duration.between(now, startTime.plusHours(24))
        }
        return duration.toMillis()
    }
}
