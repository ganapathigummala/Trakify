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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gana.trakify.components.TextView
import com.gana.trakify.ui.theme.TrakifyTheme
import com.gana.trakify.ui.theme.TrakifyRed
import com.gana.trakify.ui.theme.White
import androidx.core.view.WindowCompat
import com.gana.trakify.model.WeatherResponse
import com.gana.trakify.ui.theme.TrakifyBlue
import com.gana.trakify.ui.theme.TrakifyGreen

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
                ) {
//                    // Simple centered text
//                    TextView(
//                        text = "Trakify",
//                        color = TrakifyRed
//                    )

                    // Or using Box for more control:

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        TextView(
                            text = "Trakify",
                            color = TrakifyRed
                        )
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
                    color = TrakifyRed
                )
                TempCard()
            }
        }
    }
}@Composable
fun TempCard() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TrakifyRed)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Center Image
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = stringResource(id = R.string.app_name),
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Title
        TextView(
            text = "Trakify",
            color = White
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Subtitle
        TextView(
            text = "Track Smarter, Move Faster",
            color = White
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Bottom Row with evenly spaced text items
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TrakifyBlue)
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextView(text = "Live", color = White)
            TextView(text = "History", color = White)
//            TextView(text = "Settings", color = White)
        }
    }
}
