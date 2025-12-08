// utils/LocationPermissionManager.kt
package com.gana.trakify.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class LocationPermissionManager(private val context: Context) {

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasBackgroundLocationPermission(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun hasAllPermissions(): Boolean {
        return hasLocationPermission() && hasBackgroundLocationPermission()
    }

    fun requestPermissions(
        activity: Activity,
        launcher: ActivityResultLauncher<Array<String>>
    ) {
        val permissions = mutableListOf<String>()

        // Add location permissions
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)

        // Add background location permission for Android 10+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }

        launcher.launch(permissions.toTypedArray())
    }

    fun shouldShowPermissionRationale(activity: Activity): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) || (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q &&
                ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ))
    }

    fun openAppSettings(activity: Activity) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", activity.packageName, null)
        }
        activity.startActivity(intent)
    }

    fun getPermissionStatus(): PermissionStatus {
        return when {
            hasAllPermissions() -> PermissionStatus.GRANTED
            hasLocationPermission() && !hasBackgroundLocationPermission() &&
                    android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q ->
                PermissionStatus.NEEDS_BACKGROUND
            else -> PermissionStatus.DENIED
        }
    }

    enum class PermissionStatus {
        GRANTED,
        NEEDS_BACKGROUND,
        DENIED
    }
}