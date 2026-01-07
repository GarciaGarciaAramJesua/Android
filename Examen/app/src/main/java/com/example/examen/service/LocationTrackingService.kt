package com.example.examen.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.examen.MainActivity
import com.example.examen.R
import com.example.examen.data.LocationDatabase
import com.example.examen.data.LocationEntity
import com.example.examen.data.LocationRepository
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class LocationTrackingService : Service() {
    
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var repository: LocationRepository
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    private var updateInterval: Long = 10000L // Por defecto 10 segundos
    private var showNotification: Boolean = true
    
    companion object {
        const val CHANNEL_ID = "LocationTrackingChannel"
        const val NOTIFICATION_ID = 1
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val EXTRA_UPDATE_INTERVAL = "EXTRA_UPDATE_INTERVAL"
        const val EXTRA_SHOW_NOTIFICATION = "EXTRA_SHOW_NOTIFICATION"
        
        var isServiceRunning = false
            private set
    }
    
    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        
        val database = LocationDatabase.getDatabase(applicationContext)
        repository = LocationRepository(database.locationDao())
        
        createNotificationChannel()
        
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    saveLocation(location)
                    if (showNotification) {
                        updateNotification(location)
                    }
                }
            }
        }
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                updateInterval = intent.getLongExtra(EXTRA_UPDATE_INTERVAL, 10000L)
                showNotification = intent.getBooleanExtra(EXTRA_SHOW_NOTIFICATION, true)
                startLocationUpdates()
            }
            ACTION_STOP -> {
                stopLocationUpdates()
            }
        }
        return START_STICKY
    }
    
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            updateInterval
        ).apply {
            setMinUpdateIntervalMillis(updateInterval / 2)
            setWaitForAccurateLocation(false)
        }.build()
        
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        
        val notification = createNotification(null)
        startForeground(NOTIFICATION_ID, notification)
        isServiceRunning = true
    }
    
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        isServiceRunning = false
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }
    
    private fun saveLocation(location: Location) {
        val locationEntity = LocationEntity(
            latitude = location.latitude,
            longitude = location.longitude,
            timestamp = System.currentTimeMillis(),
            accuracy = location.accuracy
        )
        
        serviceScope.launch {
            repository.insert(locationEntity)
        }
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Rastreo de Ubicacion",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Canal para notificaciones de rastreo de ubicacion"
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(location: Location?): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        
        val contentText = if (location != null) {
            "Lat: %.6f, Lon: %.6f\nPrecision: %.2fm".format(
                location.latitude,
                location.longitude,
                location.accuracy
            )
        } else {
            "Rastreando ubicacion..."
        }
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Rastreo Activo")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_location)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
    
    private fun updateNotification(location: Location) {
        val notification = createNotification(location)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        isServiceRunning = false
    }
}
