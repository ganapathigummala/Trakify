// di/WorkManagerModule.kt
package com.gana.trakify.di

import android.content.Context
import androidx.work.WorkManager
import com.gana.trakify.work.LocationWorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkManagerModule {

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideLocationWorkManager(@ApplicationContext context: Context): LocationWorkManager {
        return LocationWorkManager(context)
    }
}