package com.example.googlemapdirection.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LocationPermissionScreen(
    onPermissionGranted: () -> Unit
) {
    // State to manage whether to show the dialog or not
    var showDialog = remember { true }

    // Register the permission request launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Check if all permissions are granted
        val allPermissionsGranted = permissions.values.all { it }

        // If permissions are granted, call the onPermissionGranted callback
        if (allPermissionsGranted) {
            onPermissionGranted()
        } else {
            // Optionally, handle permission denial or show a message
        }
    }

    if (showDialog) {
        // Show the custom permission dialog
        AlertDialog(
            onDismissRequest = { /* Do nothing on dismiss */ },
            title = {
                Text(
                    text = "Location Permission Required",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("We need access to your location to provide directions. Please grant the required permissions.")
            },
            confirmButton = {
                TextButton(onClick = {
                    // When the user clicks "Grant Permission", show the system dialog
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                    showDialog = false  // Hide the custom dialog
                }) {
                    Text("Grant Permission")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false  // Hide the dialog if user chooses not to grant permission
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    // UI when the dialog is not showing (only shows after user dismisses dialog)
    if (!showDialog) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Location Permission Requested",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
