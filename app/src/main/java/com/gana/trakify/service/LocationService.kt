// service/LocationService.kt
package com.gana.trakify.service

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import com.gana.trakify.R
import com.gana.trakify.repository.LocationRepository
import com.gana.trakify.model.LocationData
import com.google.firebase.Timestamp
import kotlinx.coroutines.cancel

@AndroidEntryPoint
class LocationService : Service() {

    @Inject
    lateinit var locationRepository: LocationRepository

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var serviceScope = CoroutineScope(Dispatchers.IO + Job())

    private val notificationId = 12345
    private val channelId = "location_tracking_channel"

    private var isLocationUpdatesActive = false

    override fun onCreate() {
        super.onCreate()
        Log.d("LocationService", "Service onCreate()")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setupLocationRequest()
        setupLocationCallback()
        createNotificationChannel()
        startForegroundService()

        // Request immediate location on service start
        requestImmediateLocation()
    }

    private fun setupLocationRequest() {
        locationRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, TimeUnit.MINUTES.toMillis(5))
                .setMinUpdateIntervalMillis(TimeUnit.MINUTES.toMillis(5))
                .setMaxUpdateDelayMillis(TimeUnit.MINUTES.toMillis(10))
                .setWaitForAccurateLocation(true)
                .build()
        } else {
            LocationRequest.create().apply {
                interval = TimeUnit.MINUTES.toMillis(5) // Every 5 minutes
                fastestInterval = TimeUnit.MINUTES.toMillis(2)
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                maxWaitTime = TimeUnit.MINUTES.toMillis(10)
                smallestDisplacement = 50f // 50 meters minimum change
            }
        }
    }

    private fun setupLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.locations.forEach { location ->
                    Log.d("LocationService", "Location received: ${location.latitude}, ${location.longitude}")
                    saveLocationToFirebase(location)
                }
            }

            override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                super.onLocationAvailability(locationAvailability)
                Log.d("LocationService", "Location available: ${locationAvailability.isLocationAvailable}")
                if (!locationAvailability.isLocationAvailable) {
                    // Handle location not available
                    updateNotification(null)
                }
            }
        }
    }

    private fun requestImmediateLocation() {
        if (hasLocationPermission()) {
            try {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        location?.let {
                            Log.d("LocationService", "Immediate location: ${it.latitude}, ${it.longitude}")
                            saveLocationToFirebase(it)
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("LocationService", "Failed to get immediate location: ${e.message}")
                    }
            } catch (e: SecurityException) {
                Log.e("LocationService", "Security exception: ${e.message}")
            }
        }
    }

    private fun saveLocationToFirebase(location: Location) {
        serviceScope.launch {
            try {
                val locationData = LocationData(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    accuracy = location.accuracy,
                    altitude = location.altitude,
                    speed = location.speed,
                    bearing = location.bearing,
                    timestamp = Timestamp.now()
                )

                locationRepository.saveLocation(locationData).onSuccess {
                    Log.d("LocationService", "Location saved to Firebase: ${location.latitude}, ${location.longitude}")
                    // Update notification with latest location
                    updateNotification(location)
                }.onFailure { e ->
                    Log.e("LocationService", "Failed to save location: ${e.message}")
                }
            } catch (e: Exception) {
                Log.e("LocationService", "Exception saving location: ${e.message}")
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Location Tracking",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Tracks your location in the background"
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                setSound(null, null)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun startForegroundService() {
        val notification = createNotification()
        startForeground(notificationId, notification)
    }

    private fun createNotification(location: Location? = null): Notification {
        val intent = Intent(this, com.gana.trakify.MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val locationText = if (location != null) {
            String.format("%.6f, %.6f", location.latitude, location.longitude)
        } else {
            "Waiting for location..."
        }

        val lastUpdate = if (location != null) {
            "Last: ${location.latitude.toInt()}, ${location.longitude.toInt()}"
        } else {
            "Waiting..."
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Trakify Location Tracking")
            .setContentText("Tracking active - $lastUpdate")
            .setSmallIcon(R.drawable.logo)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setShowWhen(true)
            .build()
    }

    private fun updateNotification(location: Location?) {
        val notification = createNotification(location)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(notificationId, notification)
    }

    private fun startLocationUpdates() {
        if (isLocationUpdatesActive) {
            Log.d("LocationService", "Location updates already active")
            return
        }

        if (hasLocationPermission()) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e("LocationService", "Location permission not granted")
                return
            }

            try {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
                isLocationUpdatesActive = true
                Log.d("LocationService", "Location updates started successfully")
            } catch (e: SecurityException) {
                Log.e("LocationService", "Security exception starting location updates: ${e.message}")
            } catch (e: Exception) {
                Log.e("LocationService", "Exception starting location updates: ${e.message}")
            }
        } else {
            Log.e("LocationService", "Location permission check failed")
        }
    }

    private fun stopLocationUpdates() {
        if (isLocationUpdatesActive) {
            try {
                fusedLocationClient.removeLocationUpdates(locationCallback)
                isLocationUpdatesActive = false
                Log.d("LocationService", "Location updates stopped")
            } catch (e: Exception) {
                Log.e("LocationService", "Exception stopping location updates: ${e.message}")
            }
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("LocationService", "onStartCommand with action: ${intent?.action}")

        when (intent?.action) {
            ACTION_START -> {
                Log.d("LocationService", "Starting location updates")
                startLocationUpdates()
            }
            ACTION_STOP -> {
                Log.d("LocationService", "Stopping location updates")
                stopLocationUpdates()
                stopSelf()
            }
            ACTION_CAPTURE_NOW -> {
                Log.d("LocationService", "Capturing location immediately")
                requestImmediateLocation()
            }
            else -> {
                // If started without action, start updates
                startLocationUpdates()
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        Log.d("LocationService", "Service onDestroy()")
        stopLocationUpdates()
        serviceScope.coroutineContext.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_CAPTURE_NOW = "ACTION_CAPTURE_NOW"

        fun startService(context: Context) {
            val intent = Intent(context, LocationService::class.java).apply {
                action = ACTION_START
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, LocationService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }

        fun captureLocationNow(context: Context) {
            val intent = Intent(context, LocationService::class.java).apply {
                action = ACTION_CAPTURE_NOW
            }
            context.startService(intent)
        }
    }
}