// activity/MainContent.kt
package com.gana.trakify.activity

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gana.trakify.components.FeatureCard
import com.gana.trakify.components.NavBarHome
import com.gana.trakify.components.ProfileDialog
import com.gana.trakify.model.FeatureItemData
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

@Composable
fun MainContent(
    onLogout: () -> Unit = {},
    onWeatherClick: () -> Unit = {},
    onLocationClick: () -> Unit = {},  // Add this parameter
    onSettingsClick: () -> Unit = {},
    firebaseAuth: FirebaseAuth = Firebase.auth
) {
    val currentUser = firebaseAuth.currentUser
    var showProfileDialog by remember { mutableStateOf(false) }

    val features = listOf(
        FeatureItemData("Location Tracking", "Track and view your location history"),
        FeatureItemData("Weather", "Check current weather conditions"),
        FeatureItemData("Activity Log", "View your activity history"),
        FeatureItemData("Notifications", "Stay updated with alerts"),
        FeatureItemData("Settings", "Customize your preferences")
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NavBarHome(
            onMenuClick = { /* TODO: open drawer */ },
            onProfileClick = { showProfileDialog = true }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(features) { feature ->
                FeatureCard(
                    title = feature.title,
                    description = feature.description,
                    onClick = {
                        when (feature.title) {
                            "Location Tracking" -> onLocationClick()
                            "Weather" -> onWeatherClick()
                            "Settings" -> onSettingsClick()
                            else -> { /* handle other features later */ }
                        }
                    }
                )
            }
        }

        if (showProfileDialog) {
            ProfileDialog(
                firebaseAuth = firebaseAuth,
                onLogout = {
                    firebaseAuth.signOut()
                    onLogout()
                    showProfileDialog = false
                },
                onDismiss = { showProfileDialog = false }
            )
        }
    }
}