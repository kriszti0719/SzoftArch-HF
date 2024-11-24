package hu.bme.aut.citysee.feature.map

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import hu.bme.aut.citysee.R
import hu.bme.aut.citysee.ui.common.CitySeeAppBar
import hu.bme.aut.citysee.ui.model.SightUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityMapScreen(
    onMarkerClick: (String) -> Unit,
    onProfileClick: () -> Unit,
    onFabClick: () -> Unit,
    viewModel: CityMapViewModel = viewModel(factory = CityMapViewModel.Factory)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var isMapLoaded by remember { mutableStateOf(!state.isLoading) }

    LaunchedEffect(key1 = state.city) {
        state.city?.let {
            viewModel.loadSights() // Reload sights when the city changes
        }
    }

    if (!state.isLoading) {
        val pos = state.city?.let { LatLng(it.latitude, it.longitude) }!!
        val cameraPos = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(pos, 10f)
        }
        val sights: List<SightUi> = state.city!!.sights

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CitySeeAppBar(
                    title = stringResource(id = R.string.app_bar_title_map),
                    actions = {
                        IconButton(onClick = {
                            onProfileClick()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Person, // Replace with a profile-related icon if available
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                Box(
                    modifier = Modifier.fillMaxSize() // Ensure the Box takes the full screen
                ) {
                    LargeFloatingActionButton(
                        onClick = onFabClick,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .padding(16.dp) // Padding from edges
                            .align(Alignment.BottomStart) // Align to bottom-left corner
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    }
                }
            },
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPos,
                    onMapLoaded = { isMapLoaded = true }
                ) {
                    sights.forEach { sight ->
                        Marker(
                            state = MarkerState(position = LatLng(sight.latitude, sight.longitude)),
                            title = sight.name,
                            onClick = {
                                onMarkerClick(sight.id)
                                true
                            }
                        )
                    }
                }

                if (!isMapLoaded || state.isLoading) {
                    AnimatedVisibility(
                        modifier = Modifier.matchParentSize(),
                        visible = !isMapLoaded,
                        enter = EnterTransition.None,
                        exit = fadeOut()
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.background)
                                .wrapContentSize()
                        )
                    }
                }
            }
        }
    }
}
