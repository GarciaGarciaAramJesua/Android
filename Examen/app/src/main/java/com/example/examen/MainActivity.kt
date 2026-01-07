package com.example.examen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.examen.data.LocationDatabase
import com.example.examen.data.LocationRepository
import com.example.examen.databinding.ActivityMainBinding
import com.example.examen.service.LocationTrackingService
import com.example.examen.utils.ThemeManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var repository: LocationRepository
    
    private var isTracking = false
    private var currentUpdateInterval = 10000L
    
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
                requestBackgroundLocationPermission()
            }
            else -> {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private val backgroundLocationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(this, R.string.background_permission_rationale, Toast.LENGTH_LONG).show()
        }
    }
    
    private val notificationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(this, R.string.notification_permission_rationale, Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applyTheme(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(findViewById(R.id.toolbar))
        
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        
        val database = LocationDatabase.getDatabase(applicationContext)
        repository = LocationRepository(database.locationDao())
        
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        
        setupUI()
        checkPermissions()
        observeLocations()
        
        if (LocationTrackingService.isServiceRunning) {
            updateUIForTracking(true)
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_theme -> {
                showThemeDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun showThemeDialog() {
        val themes = arrayOf(
            getString(R.string.theme_ipn),
            getString(R.string.theme_escom)
        )
        
        val currentTheme = ThemeManager.getTheme(this)
        val selectedIndex = when (currentTheme) {
            ThemeManager.THEME_IPN -> 0
            ThemeManager.THEME_ESCOM -> 1
            else -> 0
        }
        
        AlertDialog.Builder(this)
            .setTitle(R.string.theme_selector)
            .setSingleChoiceItems(themes, selectedIndex) { dialog, which ->
                val newTheme = when (which) {
                    0 -> ThemeManager.THEME_IPN
                    1 -> ThemeManager.THEME_ESCOM
                    else -> ThemeManager.THEME_IPN
                }
                
                if (newTheme != currentTheme) {
                    ThemeManager.saveTheme(this, newTheme)
                    recreate()
                }
                dialog.dismiss()
            }
            .show()
    }
    
    private fun setupUI() {
        binding.btnStartStop.setOnClickListener {
            if (isTracking) {
                stopTracking()
            } else {
                startTracking()
            }
        }
        
        binding.btnViewHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
        
        binding.rgUpdateInterval.setOnCheckedChangeListener { _, checkedId ->
            currentUpdateInterval = when (checkedId) {
                R.id.rb10Seconds -> 10000L
                R.id.rb60Seconds -> 60000L
                R.id.rb5Minutes -> 300000L
                else -> 10000L
            }
        }
    }
    
    private fun checkPermissions() {
        if (!hasLocationPermission()) {
            requestLocationPermissions()
        } else if (!hasBackgroundLocationPermission()) {
            requestBackgroundLocationPermission()
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    
    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    private fun hasBackgroundLocationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
    
    private fun requestLocationPermissions() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
    
    private fun requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            AlertDialog.Builder(this)
                .setTitle(R.string.permission_required)
                .setMessage(R.string.background_permission_rationale)
                .setPositiveButton(R.string.grant_permission) { _, _ ->
                    backgroundLocationPermissionRequest.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                }
                .setNegativeButton(R.string.no, null)
                .show()
        }
    }
    
    private fun startTracking() {
        if (!hasLocationPermission()) {
            requestLocationPermissions()
            return
        }
        
        val intent = Intent(this, LocationTrackingService::class.java).apply {
            action = LocationTrackingService.ACTION_START
            putExtra(LocationTrackingService.EXTRA_UPDATE_INTERVAL, currentUpdateInterval)
            putExtra(LocationTrackingService.EXTRA_SHOW_NOTIFICATION, binding.switchNotification.isChecked)
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        
        updateUIForTracking(true)
    }
    
    private fun stopTracking() {
        val intent = Intent(this, LocationTrackingService::class.java).apply {
            action = LocationTrackingService.ACTION_STOP
        }
        startService(intent)
        
        updateUIForTracking(false)
    }
    
    private fun updateUIForTracking(tracking: Boolean) {
        isTracking = tracking
        binding.btnStartStop.text = if (tracking) {
            getString(R.string.stop_tracking)
        } else {
            getString(R.string.start_tracking)
        }
        
        binding.tvTrackingStatus.text = if (tracking) {
            getString(R.string.tracking_active)
        } else {
            getString(R.string.tracking_inactive)
        }
        
        binding.statusIndicator.setBackgroundColor(
            if (tracking) Color.GREEN else Color.RED
        )
        
        binding.rgUpdateInterval.isEnabled = !tracking
        binding.switchNotification.isEnabled = !tracking
    }
    
    private fun observeLocations() {
        lifecycleScope.launch {
            repository.allLocationsAscending.collectLatest { locations ->
                googleMap?.let { map ->
                    map.clear()
                    
                    if (locations.isNotEmpty()) {
                        val lastLocation = locations.last()
                        val latLng = LatLng(lastLocation.latitude, lastLocation.longitude)
                        
                        map.addMarker(MarkerOptions().position(latLng).title("Ubicacion Actual"))
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                        
                        binding.tvLatitude.text = getString(R.string.latitude, lastLocation.latitude.toString())
                        binding.tvLongitude.text = getString(R.string.longitude, lastLocation.longitude.toString())
                        binding.tvAccuracy.text = getString(R.string.accuracy, lastLocation.accuracy)
                        
                        if (locations.size > 1) {
                            val polylineOptions = PolylineOptions().apply {
                                color(Color.BLUE)
                                width(10f)
                                locations.forEach { location ->
                                    add(LatLng(location.latitude, location.longitude))
                                }
                            }
                            map.addPolyline(polylineOptions)
                        }
                    }
                }
            }
        }
    }
    
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        
        if (hasLocationPermission()) {
            try {
                googleMap?.isMyLocationEnabled = true
                googleMap?.uiSettings?.isMyLocationButtonEnabled = true
                
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        val latLng = LatLng(it.latitude, it.longitude)
                        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    }
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }
    
    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }
    
    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}
