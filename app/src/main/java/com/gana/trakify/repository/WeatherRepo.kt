package com.gana.trakify.repository

import com.gana.trakify.ResourceState.ResourceState
import com.gana.trakify.api.WeatherApi
import com.gana.trakify.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WeatherRepo @Inject constructor(private val weatherApi: WeatherApi) {

    fun getWeather(cityName: String): Flow<ResourceState<WeatherResponse>> = flow {
        emit(ResourceState.Loading())
        try {
            val response = weatherApi.getWeather(
                cityName = cityName,
                units = "metric",
                apiKey = "2ed9eedfb7571cfc51d9f21c39c4dff1"
            )
            emit(ResourceState.Success(response))
        } catch (e: Exception) {
            emit(ResourceState.Error(e.message ?: "An unknown error occurred"))
        }
    }
}