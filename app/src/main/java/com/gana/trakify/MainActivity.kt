package com.gana.trakify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gana.trakify.components.TextView
import com.gana.trakify.ui.theme.TrakifyTheme
import com.gana.trakify.ui.theme.Red
import com.gana.trakify.ui.theme.White
import androidx.core.view.WindowCompat
import com.gana.trakify.model.WeatherResponse
import com.gana.trakify.ui.theme.Blue
import com.gana.trakify.ui.theme.Blue
import com.gana.trakify.ui.theme.Blue100
import com.gana.trakify.ui.theme.ChartPink
import com.gana.trakify.ui.theme.Green
import com.gana.trakify.ui.theme.Green100
import com.gana.trakify.ui.theme.Orange

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            TrakifyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = White
                ) { Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center


                ) {
                    TextView(
                        text = "Trakify",
                        color = Red
                    )
                    TempCard()
                }

                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        TextView(
            text = "Welcome to $name!",
            color = MaterialTheme.colorScheme.onBackground
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
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center


            ) {
                TextView(
                    text = "Trakify",
                    color = Red
                )
                TempCard()
            }
        }
    }
}





@Composable
fun TempCard() {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .size(250.dp,300.dp)
            .background(Green100)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.snow),
            contentDescription = stringResource(id = R.string.app_name),
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "1",
            color = White,
            fontStyle = FontStyle.Italic,
            fontSize = 30.sp

        )
        Text(
            text = "Snow",
            color = White,
            fontStyle = FontStyle.Italic,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "1", color = White,
            fontStyle = FontStyle.Italic,
            fontSize = 20.sp)
                Text(text = "Feels like", color = White,
            fontStyle = FontStyle.Italic,
            fontSize = 20.sp)

            }
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "5", color = White,
            fontStyle = FontStyle.Italic,
            fontSize = 20.sp)
                Text(text = "Low", color = White,
            fontStyle = FontStyle.Italic,
            fontSize = 20.sp)

            }
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "8", color = White,
            fontStyle = FontStyle.Italic,
            fontSize = 20.sp)
                Text(text = "High", color = White,
            fontStyle = FontStyle.Italic,
            fontSize = 20.sp)

            }
        }
    }
}
