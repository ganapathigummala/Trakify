package com.gana.trakify.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gana.trakify.R
import com.gana.trakify.ResourceState.ResourceState
import com.gana.trakify.model.WeatherResponse
import com.gana.trakify.navhost.Screen
import com.gana.trakify.ui.theme.Black
import com.gana.trakify.ui.theme.Green100
import com.gana.trakify.ui.theme.White
import com.gana.trakify.viewmodel.WeatherViewModel

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

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = password, onValueChange = { password = it }, label = { Text("Password") })
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            // TODO: Call FirebaseAuth signIn
            navController.navigate(Screen.Main.route) // navigate on success
        }) {
            Text("Login")
        }
    }
}
