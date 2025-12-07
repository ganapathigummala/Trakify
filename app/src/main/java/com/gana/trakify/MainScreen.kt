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
import com.gana.trakify.navhost.Screen
import com.gana.trakify.ui.theme.TrakifyTheme
import com.gana.trakify.ui.theme.White
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            TrakifyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = White
                ) {
                    val navController = rememberNavController()

                    val startDestination = if (auth.currentUser != null) {
                        Screen.Main.route
                    } else {
                        Screen.Login.route
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