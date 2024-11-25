package hu.bme.aut.citysee.ui.model

import com.google.android.gms.maps.model.LatLng

data class DirectionsResponse(
    val routes: List<Route>
)

data class Route(
    val bounds: Bounds,
    val copyrights: String,
    val legs: List<Leg>
)

data class Bounds(
    val northeast: LatLng,
    val southwest: LatLng
)

data class Leg(
    val start_address: String,
    val end_address: String,
    val start_location: LatLng,
    val end_location: LatLng,
    val distance: Distance,
    val duration: Duration,
    val steps: List<Step>
)

data class Step(
    val html_instructions: String,
    val distance: Distance,
    val duration: Duration,
    val end_location: LatLng,
    val polyline: Polyline?,
    val start_location: LatLng,
    val travel_mode: String
)

data class Polyline(
    val points: String
)

data class Distance(
    val text: String,
    val value: Int
)

data class Duration(
    val text: String,
    val value: Int
)