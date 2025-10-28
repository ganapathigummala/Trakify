package com.gana.trakify.model

data class WeatherResponse(
    var name: String,
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind
)

data class Main(
    val temp: Double,
    val humidity: Int,
    val pressure: Double,
    val feels_like: Double
)

data class Weather(
    val main: String,
    val description: String,
    val icon: String,
    val id: Int? = null // Changed from String to Int
)

data class Wind(
    val speed: Double
)