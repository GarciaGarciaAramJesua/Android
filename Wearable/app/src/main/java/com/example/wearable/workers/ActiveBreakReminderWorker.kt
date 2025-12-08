package com.example.wearable.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.wearable.notifications.NotificationHelper

/**
 * Worker para mostrar recordatorios de pausas activas
 */
class ActiveBreakReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            val notificationHelper = NotificationHelper(applicationContext)
            notificationHelper.showActiveBreakReminder()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
