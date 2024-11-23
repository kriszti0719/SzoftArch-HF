package hu.bme.aut.citysee.feature.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hu.bme.aut.citysee.ui.common.CitySeeAppBar
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import hu.bme.aut.citysee.domain.model.User


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onSignOut: () -> Unit,
    viewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var username by remember { mutableStateOf(TextFieldValue(state.name)) }
    var isUpdated by remember { mutableStateOf(false) }

    // focus requester for the username input field
    val focusManager = LocalFocusManager.current

    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    viewModel.getCurrentUser()

    // ImagePicker launcher to open the gallery and choose a profile picture
    val imagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            profileImageUri = uri // Set selected image URI

            // Upload to Firebase Storage
            uri?.let {
                    viewModel.updateProfileImage(profileImageUri) { success ->}
            }
        }

    Scaffold(
        topBar = {
            CitySeeAppBar(
                title = "Profile",
                actions = {},
                onNavigateBack = onNavigateBack,
            )
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center

                    ) {
                        // If the user has set a profile picture, show it; otherwise, show a placeholder
                        Image(
                            painter = rememberAsyncImagePainter(
                                state.profileImageUrl ?: "https://www.gravatar.com/avatar/00000000000000000000000000000000?d=mp&f=y"
                            ),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        // Button to select a new profile picture
                        IconButton(
                            onClick = { imagePickerLauncher.launch("image/*") }, // Launch image picker
                            modifier = Modifier
                                .size(32.dp)
                                .align(Alignment.BottomEnd)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Edit Profile Picture",
                                tint = Color.Black
                            )
                        }
                    }

                    // Display registered e-mail
                    Text(
                        text = state.email,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    // Edit username
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .wrapContentWidth(align = Alignment.CenterHorizontally)
                    ) {

                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Username") },
                            modifier = Modifier
                                .fillMaxWidth()
                        )

                        // Green checkmark inside the text field, aligned to the right
                        if (isUpdated) {
                            Icon(
                                imageVector = Icons.Default.Check, // Green checkmark icon
                                contentDescription = "Username updated",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 8.dp) // Add padding to the right edge
                                    .size(24.dp) // Adjust the size of the checkmark if needed
                            )
                        }
                    }

                    // save the new username
                    Button(onClick = {
                        // Trigger the action to update the username
                        viewModel.updateUsername(username.text) { success ->
                            isUpdated = success
                        }
                        focusManager.clearFocus()
                    }) {
                        Text("Save changes")
                    }

                    Text("Points:${state.points}")

                    // logout button at the bottom of the page
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = {
                            viewModel.signOut()
                            onSignOut()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Logout")
                    }
                }
            }
        }
    )
}