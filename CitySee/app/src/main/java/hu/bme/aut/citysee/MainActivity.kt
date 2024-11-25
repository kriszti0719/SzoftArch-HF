package hu.bme.aut.citysee

import androidx.activity.compose.setContent
import hu.bme.aut.citysee.navigation.NavGraph
import hu.bme.aut.citysee.ui.theme.CitySeeTheme

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.ActivityResultLauncher

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog

import androidx.compose.runtime.Composable


class MainActivity : ComponentActivity() {

    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the permission launcher
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                // If permission is granted, show the NavGraph
                setContent {
                    CitySeeTheme {
                        NavGraph() // Show main content after permission is granted
                    }
                }
            } else {
                // If permission is denied, show the permission denied dialog
                setContent {
                    CitySeeTheme {
                        PermissionDeniedDialog { finish() } // Close the app if permission is denied
                    }
                }
            }
        }

        // Check if permission is granted at app launch
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // If permission is already granted, directly show NavGraph
            setContent {
                CitySeeTheme {
                    NavGraph() // Proceed to main content if permission is granted
                }
            }
        } else {
            // If permission is not granted, request permission
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }
}

// Composable to show the permission denied dialog
@Composable
fun PermissionDeniedDialog(onCancel: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onCancel() },
        title = { Text("Location Permission Denied") },
        text = { Text("We need location permission to continue using the app.") },
        confirmButton = {
            Button(onClick = { /* Handle confirmation (e.g., redirect to settings) */ }) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(onClick = { onCancel() }) { // Close the app when the user clicks "Cancel"
                Text("Cancel")
            }
        }
    )
}
