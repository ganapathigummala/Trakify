package com.gana.trakify.navhost

sealed class Screen(val route: String) {
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")
    object Main : Screen("main_screen")
    object Weather : Screen("weather_screen")
    object Settings : Screen("settings_screen")
}