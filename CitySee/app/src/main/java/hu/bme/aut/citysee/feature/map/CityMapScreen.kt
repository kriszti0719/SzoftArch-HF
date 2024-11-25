package hu.bme.aut.citysee.feature.map

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import getDirectionsAndUpdateState
import hu.bme.aut.citysee.R
import hu.bme.aut.citysee.ui.common.CitySeeAppBar
import hu.bme.aut.citysee.ui.model.SightUi
import hu.bme.aut.citysee.BuildConfig
import kotlin.math.*

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityMapScreen(
    onMarkerClick: (String) -> Unit,
    onProfileClick: () -> Unit,
    onFabClick: () -> Unit,
    viewModel: CityMapViewModel = viewModel(factory = CityMapViewModel.Factory)
) {
    val earthRadiusKm = 6371.0
    val isMarkerClicked = remember { mutableStateOf(false) }
    val markerClicked = remember { mutableStateOf<LatLng?>(null) }
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val userLocation = remember { mutableStateOf<Location?>(null) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    var isMapLoaded by remember { mutableStateOf(!state.isLoading) }
    val polylinePoints : MutableState<List<LatLng>> = remember { mutableStateOf(emptyList()) }
    val isDataLoaded : MutableState<Boolean> = remember { mutableStateOf(false) }
    val apiKey = BuildConfig.MAPS_API_KEY
    var origin: LatLng? = null
    var destination: LatLng? = null
    val coroutineScope = rememberCoroutineScope()

    fun isWithinOneKilometer(destination: LatLng, userLocation: Location): Boolean {
        val latDistance = Math.toRadians(destination.latitude - userLocation.latitude)
        val lonDistance = Math.toRadians(destination.longitude - userLocation.longitude)

        val a = sin(latDistance / 2) * sin(latDistance / 2) +
                cos(Math.toRadians(userLocation.latitude)) * cos(Math.toRadians(destination.latitude)) *
                sin(lonDistance / 2) * sin(lonDistance / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distance = earthRadiusKm * c

        return distance <= 1.0
    }

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
                    FloatingActionButton(
                        onClick = onFabClick,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .padding(16.dp) // Padding from edges
                            .align(Alignment.BottomStart) // Align to bottom-left corner
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    }
                    if(isMarkerClicked.value){
                        FloatingActionButton(
                            onClick = {
                                origin = userLocation.value?.let { LatLng(it.latitude, it.longitude) }
                                destination =  markerClicked.value
                                // Call the function that handles directions and state updates
                                getDirectionsAndUpdateState(
                                    origin,
                                    destination,
                                    apiKey,
                                    coroutineScope,
                                    polylinePoints,
                                    isDataLoaded
                                )

                            },
                            contentColor = MaterialTheme.colorScheme.primary,
                            containerColor = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.BottomStart)
                                .offset(y = -60.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ArrowOutward, contentDescription = null)
                        }
                    }
                }
            },
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                LaunchedEffect(Unit) {
                    try {
                        fusedLocationClient.getCurrentLocation(
                            LocationRequest.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token
                        ).addOnSuccessListener { loc ->
                            userLocation.value = loc
                        }.addOnFailureListener { e ->
                            Log.e("LocationError", "Failed to get current location", e)
                        }
                    } catch (e: Exception) {
                        Log.e("LocationError", "Error getting location", e)
                    }
                }
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPos,
                    onMapLoaded = { isMapLoaded = true },
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        compassEnabled = true,
                        myLocationButtonEnabled = true,
                    ),
                    onMapClick = { latLng ->
                        if (isMarkerClicked.value) {
                            isMarkerClicked.value = false
                        }
                        if(isDataLoaded.value){
                            isDataLoaded.value = false
                        }
                        origin = null
                        destination = null
                        polylinePoints.value = emptyList()
                    }
                )
                 {
                    MapMarkerContent(sights = sights, isMarkerClicked = isMarkerClicked, markerClicked = markerClicked, onMarkerClick = onMarkerClick)
                     if (userLocation.value != null) {
                         Marker(
                             state = rememberMarkerState(position = LatLng(userLocation.value!!.latitude, userLocation.value!!.longitude)),
                             title = "Your Location",
                             icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                         )

                         if (isWithinOneKilometer(destination!!, userLocation.value!!)) {
                             //add 100 to the user's score

                         }
                     }

                     Polyline(
                         points = polylinePoints.value,    // List of LatLng points
                         color = Color.Blue,         // Polyline color
                         width = 8f,                 // Polyline width
                         geodesic = true             // Optional: curve the polyline to follow Earth's curvature
                     )
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

