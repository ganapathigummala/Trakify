// navhost/NavigationHost.kt
package com.gana.trakify.navhost

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gana.trakify.activity.LoginScreen
import com.gana.trakify.screens.WeatherScreen
import com.gana.trakify.screens.LocationScreen
import com.gana.trakify.activity.MainContent
import com.gana.trakify.activity.RegistrationScreen
import com.gana.trakify.components.SettingsScreen

@Composable
fun NavigationHost(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegistrationScreen(
                onRegistrationSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // THIS IS CRITICAL - Make sure Main route is defined
        composable(Screen.Main.route) {
            MainContent(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                },
                onWeatherClick = {
                    navController.navigate(Screen.Weather.route)
                },
                onLocationClick = {
                    navController.navigate(Screen.Location.route)
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.Weather.route) {
            WeatherScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Location.route) {
            LocationScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}