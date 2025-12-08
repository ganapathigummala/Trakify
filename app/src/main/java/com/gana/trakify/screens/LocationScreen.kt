// screens/LocationScreen.kt
package com.gana.trakify.screens

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.Timestamp
import com.gana.trakify.ui.theme.Green100
import com.gana.trakify.ui.theme.White
import com.gana.trakify.utils.LocationPermissionManager
import com.gana.trakify.viewmodel.LocationViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationScreen(
    onBackClick: () -> Unit,
    locationViewModel: LocationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val locationHistory by locationViewModel.locationHistory.collectAsState()
    val latestLocation by locationViewModel.latestLocation.collectAsState()
    val isTracking by locationViewModel.isTracking.collectAsState()
    val permissionState by locationViewModel.permissionState.collectAsState()
    val permissionRationale by locationViewModel.permissionRationale.collectAsState()

    var showClearAllDialog by remember { mutableStateOf(false) }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        locationViewModel.checkPermissions()

        if (allGranted) {
            // Start tracking if permissions granted
            locationViewModel.startLocationTracking(context)
        } else {
            // Show rationale if some permissions denied
            locationViewModel.showPermissionRationale(true)
        }
    }

    // Request permissions when screen opens if not granted
    LaunchedEffect(Unit) {
        locationViewModel.checkPermissions()
        locationViewModel.loadLocationHistory()
        locationViewModel.loadLatestLocation()

        // Auto-request permissions if not granted
        if (permissionState != LocationPermissionManager.PermissionStatus.GRANTED) {
            requestPermissions(context, permissionLauncher)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Location Tracking") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (locationHistory.isNotEmpty()) {
                        IconButton(
                            onClick = { showClearAllDialog = true }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Clear All")
                        }
                    }

                    if (isTracking) {
                        IconButton(onClick = { locationViewModel.stopLocationTracking(context) }) {
                            Icon(Icons.Default.Clear, contentDescription = "Stop Tracking")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (!isTracking && permissionState == LocationPermissionManager.PermissionStatus.GRANTED) {
                FloatingActionButton(
                    onClick = { locationViewModel.startLocationTracking(context) },
                    containerColor = Green100
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Start Tracking")
                }
            }
        }
    ) { paddingValues ->
        // Clear All Dialog
        if (showClearAllDialog) {
            AlertDialog(
                onDismissRequest = { showClearAllDialog = false },
                title = { Text("Clear All Locations", fontWeight = FontWeight.Bold) },
                text = { Text("Are you sure you want to delete all location history?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            locationViewModel.clearHistory()
                            showClearAllDialog = false
                        }
                    ) {
                        Text("Clear All", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearAllDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Permission Rationale Dialog
        if (permissionRationale) {
            AlertDialog(
                onDismissRequest = { locationViewModel.showPermissionRationale(false) },
                title = { Text("Location Permission Required") },
                text = {
                    Text("Trakify needs location permissions to track your location in the background. " +
                            "Please grant all permissions in the app settings.")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            openAppSettings(context)
                            locationViewModel.showPermissionRationale(false)
                        }
                    ) {
                        Text("Open Settings")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { locationViewModel.showPermissionRationale(false) }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Permission Status Card
                PermissionStatusCard(
                    permissionState = permissionState,
                    onRequestPermission = {
                        requestPermissions(context, permissionLauncher)
                    },
                    onOpenSettings = { openAppSettings(context) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Latest Location Card
                LatestLocationCard(
                    location = latestLocation,
                    isTracking = isTracking,
                    permissionState = permissionState
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Location History
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Location History",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (locationHistory.isNotEmpty()) {
                        Text(
                            text = "${locationHistory.size} locations",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (locationHistory.isEmpty()) {
                    EmptyLocationState(
                        permissionState = permissionState,
                        isTracking = isTracking
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(locationHistory) { location ->
                            LocationItem(
                                location = location,
                                onDelete = { locationViewModel.deleteLocation(location.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionStatusCard(
    permissionState: LocationPermissionManager.PermissionStatus,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val (title, description) = when (permissionState) {
        LocationPermissionManager.PermissionStatus.GRANTED -> Pair(
            "Permissions Granted ✓",
            "Location tracking is enabled"
        )
        LocationPermissionManager.PermissionStatus.NEEDS_BACKGROUND -> Pair(
            "Background Permission Needed",
            "Allow background location access for continuous tracking"
        )
        LocationPermissionManager.PermissionStatus.DENIED -> Pair(
            "Permissions Required",
            "Please grant location permissions to use this feature"
        )
    }

    val actionText = when (permissionState) {
        LocationPermissionManager.PermissionStatus.GRANTED -> null
        LocationPermissionManager.PermissionStatus.NEEDS_BACKGROUND -> "Grant Background"
        LocationPermissionManager.PermissionStatus.DENIED -> "Grant Permissions"
    }

    val action: (() -> Unit)? = when (permissionState) {
        LocationPermissionManager.PermissionStatus.GRANTED -> null
        else -> onRequestPermission
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (permissionState) {
                LocationPermissionManager.PermissionStatus.GRANTED ->
                    MaterialTheme.colorScheme.primaryContainer
                LocationPermissionManager.PermissionStatus.NEEDS_BACKGROUND ->
                    MaterialTheme.colorScheme.secondaryContainer
                LocationPermissionManager.PermissionStatus.DENIED ->
                    MaterialTheme.colorScheme.errorContainer
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold)
                Text(text = description, fontSize = 12.sp)

                // Add settings button for denied permissions
                if (permissionState == LocationPermissionManager.PermissionStatus.DENIED) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = onOpenSettings,
                        modifier = Modifier.padding(start = 0.dp)
                    ) {
                        Text("Open App Settings")
                    }
                }
            }

            action?.let {
                Button(onClick = it) {
                    Text(actionText ?: "Grant")
                }
            }
        }
    }
}

@Composable
fun LatestLocationCard(
    location: com.gana.trakify.model.LocationData?,
    isTracking: Boolean,
    permissionState: LocationPermissionManager.PermissionStatus
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Green100
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Current Status",
                    color = White,
                    fontWeight = FontWeight.Bold
                )

                Badge(
                    containerColor = if (isTracking) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                    contentColor = if (isTracking) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                ) {
                    Text(
                        text = if (isTracking) "ACTIVE"
                        else if (permissionState == LocationPermissionManager.PermissionStatus.GRANTED) "READY"
                        else "DISABLED",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (permissionState == LocationPermissionManager.PermissionStatus.GRANTED) {
                if (location != null) {
                    Text(
                        text = "${location.latitude}, ${location.longitude}",
                        color = White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Accuracy: ${String.format("%.1f", location.accuracy)}m",
                            color = White,
                            fontSize = 12.sp
                        )
                        Text(
                            text = formatTimestamp(location.timestamp),
                            color = White,
                            fontSize = 12.sp
                        )
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "No Location",
                            tint = White,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isTracking) "Waiting for location..."
                            else "Start tracking to get location",
                            color = White,
                            fontSize = 16.sp
                        )
                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "Permissions Required",
                        tint = White,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Permissions Required",
                        color = White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Grant location permissions to start tracking",
                        color = White,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyLocationState(
    permissionState: LocationPermissionManager.PermissionStatus,
    isTracking: Boolean
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (permissionState) {
            LocationPermissionManager.PermissionStatus.GRANTED -> {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = "No Locations",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (isTracking) "Waiting for first location..."
                    else "No location data yet",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (isTracking) "Location updates every 5 minutes"
                    else "Start tracking to see your location history",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            else -> {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = "Permissions Required",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Permissions Required",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Grant location permissions to see location history",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun LocationItem(location: com.gana.trakify.model.LocationData, onDelete: () -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${location.latitude}, ${location.longitude}",
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Accuracy", modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${String.format("%.1f", location.accuracy)}m", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Default.DateRange, contentDescription = "Time", modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = formatTimestamp(location.timestamp), fontSize = 12.sp)
                }
            }

            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Location") },
            text = { Text("Are you sure you want to delete this location entry?") },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteDialog = false }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// Helper functions
private fun requestPermissions(context: Context, launcher: androidx.activity.result.ActivityResultLauncher<Array<String>>) {
    val permissions = mutableListOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    }

    launcher.launch(permissions.toTypedArray())
}

private fun openAppSettings(context: Context) {
    val intent = android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = android.net.Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}

private fun formatTimestamp(timestamp: Timestamp): String {
    val date = timestamp.toDate()
    val sdf = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    return sdf.format(date)
}