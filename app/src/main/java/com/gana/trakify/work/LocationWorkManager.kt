// work/LocationWorkManager.kt
package com.gana.trakify.work

import android.content.Context
import androidx.work.*
import com.gana.trakify.service.LocationService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LocationWorkManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun scheduleLocationWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val locationWork = PeriodicWorkRequestBuilder<LocationWorker>(
            5, TimeUnit.MINUTES  // Every 5 minutes
        )
            .setConstraints(constraints)
            .setInitialDelay(1, TimeUnit.MINUTES)  // Start after 1 minute
            .addTag("location_tracking")
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "location_tracking_work",
            ExistingPeriodicWorkPolicy.KEEP,  // Keep existing work
            locationWork
        )
    }

    fun cancelLocationWork() {
        WorkManager.getInstance(context).cancelUniqueWork("location_tracking_work")
    }
}

class LocationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Trigger location service
            LocationService.startService(applicationContext)

            // Wait for location to be captured
            withContext(Dispatchers.IO) {
                kotlinx.coroutines.delay(30000) // Wait 30 seconds for location
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}