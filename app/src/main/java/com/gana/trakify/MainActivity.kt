package com.gana.trakify

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.gana.trakify.ResourceState.ResourceState
import com.gana.trakify.model.WeatherResponse
import com.gana.trakify.ui.theme.Green100
import com.gana.trakify.ui.theme.TrakifyTheme
import com.gana.trakify.ui.theme.White
import com.gana.trakify.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.text.toDouble

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val TAG = "MainActivity"
    private val weatherViewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate Called")

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            TrakifyTheme {
                val weatherState = weatherViewModel.weatherState.collectAsState()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = White
                ) {
                    WeatherAppContent(
                        weatherViewModel = weatherViewModel,
                        weatherState = weatherState.value
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume Called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause Called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop Called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy Called")
    }
}

@Composable
fun WeatherAppContent(
    weatherViewModel: WeatherViewModel,
    weatherState: ResourceState<WeatherResponse>?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InputField(weatherViewModel)

        Spacer(modifier = Modifier.height(16.dp))

        when (val state = weatherState) {
            is ResourceState.Loading -> CircularProgressIndicator()
            is ResourceState.Success -> GridTempCards(state.data)
            is ResourceState.Error -> Text("Error: ${state.error}", color = MaterialTheme.colorScheme.error)
            null -> Text("Search a city...")
        }
    }
}
@Composable
fun InputField(weatherViewModel: WeatherViewModel) {
    var textState by remember { mutableStateOf("Bangalore") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        OutlinedTextField(
            value = textState,
            onValueChange = { textState = it },
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val city = textState.trim()
                if (city.isNotEmpty()) {
                    weatherViewModel.getWeather(city)
                }
            }
        ) {
            Text("Search")
        }
    }
}



// Rest of your composable functions remain the same...
@Composable
fun GridTempCards(response: WeatherResponse) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TempCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            response
        )
        Spacer(modifier = Modifier.height(16.dp))

        ChildTempCards(response.main.humidity.toDouble(), "Humidity", response.wind.speed,"Wind")

        ChildTempCards(response.visibility.toDouble(), "Visibility", response.main.pressure,"Visibility")
    }
}

@Composable
fun TempCard(modifier: Modifier = Modifier, response: WeatherResponse) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Green100)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.snow),
            contentDescription = null,
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "${response.main.temp}°", color = White, fontStyle = FontStyle.Italic, fontSize = 32.sp)
        Text("${response.weather.get(0).main}", color = White, fontStyle = FontStyle.Italic, fontSize = 20.sp)

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
fun ChildTempCards(valueHumidity: Double,stringHumidity: String,valueWind: Double,stringWind: String) {
    Row(
        modifier = Modifier.height(150.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SubTempCard(valueHumidity,stringHumidity)
        SubTempCard(valueWind,stringWind)

    }
}

@Composable
fun SubTempCard(value: Double, string: String) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .width(150.dp)
            .height( 100.dp)
            .background(Green100),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${value}°",
            color = Color.White,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = string,
            color = Color.White,
            fontSize = 14.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TrakifyTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = White
        ) {
//            WeatherAppContent(
//                weatherViewModel = WeatherViewModel(),
//                weatherState = null
//            )
        }
    }
}