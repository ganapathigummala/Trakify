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
    firebaseAuth: FirebaseAuth = Firebase.auth
) {
    val currentUser = firebaseAuth.currentUser
    var showProfileDialog by remember { mutableStateOf(false) }

    val features = listOf(
        FeatureItemData("Location", "Track your location in real-time"),
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
        // 🔹 Keep NavBarHome untouched
        NavBarHome(
            onMenuClick = { /* TODO: open drawer */ },
            onProfileClick = { showProfileDialog = true }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Grid layout for features
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
                    onClick = { /* TODO: Navigate to feature */ }
                )
            }
        }

        // 🔹 Profile dialog
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
