package hu.bme.aut.citysee.feature.map

import androidx.compose.runtime.Composable
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.rememberMarkerState
import hu.bme.aut.citysee.ui.model.SightUi

@Composable
@GoogleMapComposable
fun MapMarkerContent(
    sights: List<SightUi>,
    onMountainClick: (Marker) -> Boolean = { false }
) {
    sights.forEach { sight ->
        Marker(
            state = rememberMarkerState(position = sight.),
            title = sight.name,
            snippet = sight.elevation.toElevationString(),
            tag = sight,
            onClick = { marker ->
                onMountainClick(marker)
                false
            },
            zIndex = if (mountain.is14er()) 5f else 2f
        )
    }
}