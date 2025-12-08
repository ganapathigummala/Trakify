package com.gana.trakify.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class LocationData(
    val id: String = "",
    val userId: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val accuracy: Float = 0f,
    val altitude: Double? = null,
    val speed: Float? = null,
    val bearing: Float? = null,
    val timestamp: Timestamp = Timestamp.now(),
    val address: String? = null,
    val batteryLevel: Int? = null,
    val isCharging: Boolean? = null
) {
    fun toGeoPoint(): GeoPoint = GeoPoint(latitude, longitude)
}

data class LocationHistory(
    val locations: List<LocationData> = emptyList(),
    val lastUpdated: Timestamp = Timestamp.now()
)