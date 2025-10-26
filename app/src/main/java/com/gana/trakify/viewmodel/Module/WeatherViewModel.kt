package com.gana.trakify.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gana.trakify.ResourceState.ResourceState
import com.gana.trakify.model.WeatherResponse
import com.gana.trakify.repository.WeatherRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepo: WeatherRepo
) : ViewModel() {

    private val _weatherState = MutableStateFlow<ResourceState<WeatherResponse>?>(null)

    val weatherState: StateFlow<ResourceState<WeatherResponse>?> = _weatherState.asStateFlow()


    fun getWeather(cityName: String) {
        _weatherState.value = ResourceState.Loading()

        viewModelScope.launch {
            try {
                weatherRepo.getWeather(cityName).collect { state ->
                    _weatherState.value = state
                }
            } catch (e: Exception) {
                _weatherState.value = ResourceState.Error(e.message ?: "Unknown error")
            }
        }
    }
}