// viewmodel/LocationViewModel.kt
package com.gana.trakify.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gana.trakify.model.LocationData
import com.gana.trakify.repository.LocationRepository
import com.gana.trakify.service.LocationService
import com.gana.trakify.utils.LocationPermissionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val locationPermissionManager: LocationPermissionManager
) : ViewModel() {

    private val _locationHistory = MutableStateFlow<List<LocationData>>(emptyList())
    val locationHistory: StateFlow<List<LocationData>> = _locationHistory.asStateFlow()

    private val _latestLocation = MutableStateFlow<LocationData?>(null)
    val latestLocation: StateFlow<LocationData?> = _latestLocation.asStateFlow()

    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    private val _permissionState = MutableStateFlow<LocationPermissionManager.PermissionStatus>(
        LocationPermissionManager.PermissionStatus.DENIED
    )
    val permissionState: StateFlow<LocationPermissionManager.PermissionStatus> = _permissionState.asStateFlow()

    private val _permissionRationale = MutableStateFlow(false)
    val permissionRationale: StateFlow<Boolean> = _permissionRationale.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        checkPermissions()
        loadLocationHistory()
        loadLatestLocation()
    }

    fun loadLocationHistory() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                locationRepository.getLocationHistory().onSuccess { locations ->
                    _locationHistory.value = locations
                    Log.d("LocationViewModel", "Loaded ${locations.size} locations")
                }.onFailure { e ->
                    Log.e("LocationViewModel", "Failed to load location history: ${e.message}")
                }
            } catch (e: Exception) {
                Log.e("LocationViewModel", "Exception loading history: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadLatestLocation() {
        viewModelScope.launch {
            try {
                locationRepository.getLatestLocation().onSuccess { location ->
                    _latestLocation.value = location
                    Log.d("LocationViewModel", "Latest location: ${location?.latitude}, ${location?.longitude}")
                }.onFailure { e ->
                    Log.e("LocationViewModel", "Failed to load latest location: ${e.message}")
                }
            } catch (e: Exception) {
                Log.e("LocationViewModel", "Exception loading latest location: ${e.message}")
            }
        }
    }

    fun startLocationTracking(context: Context) {
        if (locationPermissionManager.hasAllPermissions()) {
            Log.d("LocationViewModel", "Starting location tracking")
            LocationService.startService(context)
            _isTracking.value = true

            // Load fresh data after starting
            loadLatestLocation()
            loadLocationHistory()
        } else {
            Log.d("LocationViewModel", "Cannot start tracking - permissions missing")
            checkPermissions()
        }
    }

    fun stopLocationTracking(context: Context) {
        Log.d("LocationViewModel", "Stopping location tracking")
        LocationService.stopService(context)
        _isTracking.value = false
    }

    fun captureLocationNow(context: Context) {
        if (locationPermissionManager.hasAllPermissions()) {
            Log.d("LocationViewModel", "Capturing location immediately")
            LocationService.captureLocationNow(context)

            // Wait a moment and refresh
            viewModelScope.launch {
                kotlinx.coroutines.delay(2000) // Wait 2 seconds
                loadLatestLocation()
                loadLocationHistory()
            }
        }
    }

    fun checkPermissions() {
        _permissionState.value = locationPermissionManager.getPermissionStatus()
        Log.d("LocationViewModel", "Permission state: ${_permissionState.value}")
    }

    fun deleteLocation(locationId: String) {
        viewModelScope.launch {
            try {
                locationRepository.deleteLocation(locationId).onSuccess {
                    Log.d("LocationViewModel", "Location deleted: $locationId")
                    loadLocationHistory()
                }.onFailure { e ->
                    Log.e("LocationViewModel", "Failed to delete location: ${e.message}")
                }
            } catch (e: Exception) {
                Log.e("LocationViewModel", "Exception deleting location: ${e.message}")
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            try {
                locationRepository.clearLocationHistory().onSuccess {
                    Log.d("LocationViewModel", "Location history cleared")
                    _locationHistory.value = emptyList()
                }.onFailure { e ->
                    Log.e("LocationViewModel", "Failed to clear history: ${e.message}")
                }
            } catch (e: Exception) {
                Log.e("LocationViewModel", "Exception clearing history: ${e.message}")
            }
        }
    }

    fun showPermissionRationale(show: Boolean) {
        _permissionRationale.value = show
    }

    fun refreshData() {
        loadLatestLocation()
        loadLocationHistory()
    }
}