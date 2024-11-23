package hu.bme.aut.citysee.feature.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun CityMapScreen(
    //paddingValues: PaddingValues,
    viewModel: CityMapViewModel = viewModel(factory = CityMapViewModel.Factory)
    //eventFlow: Flow<MountainsScreenEvent>,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var isMapLoaded by remember { mutableStateOf(!state.isLoading) }
    if(!state.isLoading){
        var pos = state.city?.let { com.google.android.gms.maps.model.LatLng(it.latitude, it.longitude) }!!

        var cameraPos = rememberCameraPositionState{
            position = CameraPosition.fromLatLngZoom(pos, 10f)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                //.padding(paddingValues)
        ) {
            // Add GoogleMap here
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPos,
                onMapLoaded = { isMapLoaded = true }
            ) {
                Marker(
                    state = MarkerState(position = pos),
                    title = state.city?.name,
                    snippet = "Marker in Null island"
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