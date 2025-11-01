// MainActivity.kt
package com.gana.trakify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.gana.trakify.navhost.NavigationHost
import com.gana.trakify.ui.theme.TrakifyTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        auth = Firebase.auth

        setContent {
            TrakifyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val navController = rememberNavController()

                    // Check if user is already logged in
                    val startDestination = if (auth.currentUser != null) {
                        "main_screen"
                    } else {
                        "login_screen"
                    }

                    NavigationHost(
                        navController = navController,
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}