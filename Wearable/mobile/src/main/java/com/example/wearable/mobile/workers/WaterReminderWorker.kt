package com.example.wearable.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.wearable.notifications.NotificationHelper

/**
 * Worker para mostrar recordatorios de hidratación
 */
class WaterReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            val notificationHelper = NotificationHelper(applicationContext)
            notificationHelper.showWaterReminder()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
