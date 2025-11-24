package com.gana.trakify.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gana.trakify.R
import com.gana.trakify.ResourceState.ResourceState
import com.gana.trakify.model.WeatherResponse
import com.gana.trakify.ui.theme.Green100
import com.gana.trakify.ui.theme.White
import com.gana.trakify.viewmodel.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    onBackClick: () -> Unit,
    weatherViewModel: WeatherViewModel = hiltViewModel()
) {
    var location by remember { mutableStateOf("Bangalore") }
    val weatherState by weatherViewModel.weatherState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (weatherState == null) {
            weatherViewModel.getWeather(location)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weather") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val city = location.trim()
                        if (city.isNotEmpty()) {
                            weatherViewModel.getWeather(city)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Search Weather")
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (val state = weatherState) {
                    is ResourceState.Loading -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Loading weather data...")
                        }
                    }
                    is ResourceState.Success -> {
                        GridTempCards(state.data)
                    }
                    is ResourceState.Error -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Error: ${state.error}",
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    weatherViewModel.getWeather(location.trim())
                                }
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                    null -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Search for a city to see weather data")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GridTempCards(response: WeatherResponse) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TempCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            response = response
        )
        Spacer(modifier = Modifier.height(16.dp))
        ChildTempCards(response)
    }
}

@Composable
fun TempCard(
    modifier: Modifier = Modifier,
    response: WeatherResponse
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Green100)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = getWeatherIcon(response.weather[0].main)),
            contentDescription = null,
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "${response.main.temp}°",
            color = White,
            fontStyle = FontStyle.Italic,
            fontSize = 32.sp
        )
        Text(
            text = response.weather[0].main,
            color = White,
            fontStyle = FontStyle.Italic,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TempDetail("${response.main.feels_like}°", "Feels")
            TempDetail("${response.main.temp_min}°", "Low")
            TempDetail("${response.main.temp_max}°", "High")
        }
    }
}

@Composable
private fun TempDetail(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = White, fontSize = 20.sp)
        Text(label, color = White, fontSize = 20.sp)
    }
}

@Composable
fun ChildTempCards(response: WeatherResponse) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        SubTempCard(
            value = response.main.humidity.toDouble(),
            string = "Humidity",
            unit = "%"
        )
        SubTempCard(
            value = response.wind.speed,
            string = "Wind",
            unit = "m/s"
        )
        SubTempCard(
            value = response.main.pressure.toDouble(),
            string = "Pressure",
            unit = "hPa"
        )
    }
}

@Composable
fun SubTempCard(value: Double, string: String, unit: String = "") {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .size(width = 100.dp, height = 150.dp)
            .background(Green100)
            .padding(vertical = 10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("$value$unit", color = White, fontSize = 18.sp)
        Text(string, color = White, fontSize = 14.sp)
    }
}

private fun getWeatherIcon(weatherCondition: String): Int {
    return when (weatherCondition.toLowerCase()) {
        "clear" -> R.drawable.snow
        "clouds" -> R.drawable.snow
        "rain" -> R.drawable.snow
        "snow" -> R.drawable.snow
        "thunderstorm" -> R.drawable.snow
        else -> R.drawable.snow
    }
}