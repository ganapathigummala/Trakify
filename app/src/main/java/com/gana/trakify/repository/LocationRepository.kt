// repository/LocationRepository.kt
package com.gana.trakify.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.gana.trakify.model.LocationData
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    suspend fun saveLocation(locationData: LocationData): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))

            val locationWithId = locationData.copy(
                id = "${userId}_${System.currentTimeMillis()}",
                userId = userId
            )

            firestore.collection("user_locations")
                .document(userId)
                .collection("locations")
                .document(locationWithId.id)
                .set(locationWithId)
                .await()

            // Also update the latest location in user document
            firestore.collection("users")
                .document(userId)
                .update(
                    "lastLocation", GeoPoint(locationData.latitude, locationData.longitude),
                    "lastLocationUpdate", Timestamp.now()
                )
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLocationHistory(limit: Int = 50): Result<List<LocationData>> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))

            val snapshot = firestore.collection("user_locations")
                .document(userId)
                .collection("locations")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()

            val locations = snapshot.documents.mapNotNull { document ->
                document.toObject(LocationData::class.java)
            }

            Result.success(locations)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLatestLocation(): Result<LocationData?> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))

            val snapshot = firestore.collection("user_locations")
                .document(userId)
                .collection("locations")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()

            val location = snapshot.documents.firstOrNull()?.toObject(LocationData::class.java)
            Result.success(location)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteLocation(locationId: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))

            firestore.collection("user_locations")
                .document(userId)
                .collection("locations")
                .document(locationId)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearLocationHistory(): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))

            val snapshot = firestore.collection("user_locations")
                .document(userId)
                .collection("locations")
                .get()
                .await()

            val batch = firestore.batch()
            snapshot.documents.forEach { document ->
                batch.delete(document.reference)
            }
            batch.commit().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}