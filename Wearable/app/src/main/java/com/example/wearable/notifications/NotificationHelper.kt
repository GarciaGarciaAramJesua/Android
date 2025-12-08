package com.example.wearable.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.wearable.R
import com.example.wearable.presentation.MainActivity

/**
 * Gestor de notificaciones para recordatorios
 */
class NotificationHelper(private val context: Context) {
    
    companion object {
        const val WATER_CHANNEL_ID = "water_reminders"
        const val ACTIVE_BREAK_CHANNEL_ID = "active_break_reminders"
        const val WATER_NOTIFICATION_ID = 1001
        const val ACTIVE_BREAK_NOTIFICATION_ID = 1002
    }
    
    init {
        createNotificationChannels()
    }
    
    private fun createNotificationChannels() {
        val waterChannel = NotificationChannel(
            WATER_CHANNEL_ID,
            "Recordatorios de Hidratación",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notificaciones para recordarte beber agua"
            enableVibration(true)
        }
        
        val activeBreakChannel = NotificationChannel(
            ACTIVE_BREAK_CHANNEL_ID,
            "Recordatorios de Pausas Activas",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notificaciones para recordarte tomar pausas activas"
            enableVibration(true)
        }
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(waterChannel)
        notificationManager.createNotificationChannel(activeBreakChannel)
    }
    
    fun showWaterReminder() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "water")
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(context, WATER_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setContentTitle("💧 Hora de hidratarte")
            .setContentText("Recuerda beber agua para mantenerte saludable")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 300, 100, 300))
            .build()
        
        try {
            NotificationManagerCompat.from(context).notify(WATER_NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            // Permisos no concedidos
        }
    }
    
    fun showActiveBreakReminder() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "active_break")
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(context, ACTIVE_BREAK_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setContentTitle("🏃 Hora de una pausa activa")
            .setContentText("Toma un descanso y estira tu cuerpo")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 300, 100, 300))
            .build()
        
        try {
            NotificationManagerCompat.from(context).notify(ACTIVE_BREAK_NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            // Permisos no concedidos
        }
    }
}
