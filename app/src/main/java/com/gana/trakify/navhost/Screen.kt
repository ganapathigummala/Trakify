package com.gana.trakify.navhost

// navhost/Screen.kt
// navhost/Screen.kt

sealed class Screen(val route: String) {
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")
    object Main : Screen("main_screen")
}
