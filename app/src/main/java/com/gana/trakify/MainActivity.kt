package com.gana.trakify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gana.trakify.components.TextView
import com.gana.trakify.ui.theme.TrakifyTheme
import com.gana.trakify.ui.theme.TrakifyRed
import com.gana.trakify.ui.theme.White
import androidx.core.view.WindowCompat

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
            }
        }
    }
}