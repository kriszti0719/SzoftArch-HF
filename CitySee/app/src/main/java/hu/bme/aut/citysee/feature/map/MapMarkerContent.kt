package hu.bme.aut.citysee.feature.map

import androidx.compose.runtime.Composable
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.rememberMarkerState
import hu.bme.aut.citysee.ui.model.SightUi

@Composable
@GoogleMapComposable
fun MapMarkerContent(
    sights: List<SightUi>,
    onLocationClick: (Marker) -> Boolean = { false },
    onMarkerClick: (String) -> Unit,
    ) {
    sights.forEach { sight ->

        Marker(
            state = rememberMarkerState(position = LatLng(sight.latitude, sight.longitude)),
            title = sight.name,
            snippet = sight.address,
            tag = sight,
            onClick = { marker ->
                onLocationClick(marker)
                false
            },
            onInfoWindowClick = { marker ->
                onMarkerClick(sight.id)
            }
        )
    }
}