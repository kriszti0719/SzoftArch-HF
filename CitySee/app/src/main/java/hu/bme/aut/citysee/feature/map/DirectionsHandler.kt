import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.IOException
import java.net.URLEncoder
import com.google.gson.Gson
import hu.bme.aut.citysee.ui.model.DirectionsResponse

suspend fun getDirections(
    origin: LatLng,
    destination: LatLng,
    apiKey: String,
    travelMode: String = "driving"  // Default mode is driving
): MutableList<LatLng> {
    val client = OkHttpClient()
    val polylineList = mutableListOf<LatLng>()

    // Convert LatLng to String format
    val originString = "${origin.latitude},${origin.longitude}"
    val destinationString = "${destination.latitude},${destination.longitude}"

    // URL encode the origin and destination
    val encodedOrigin = URLEncoder.encode(originString, "UTF-8")
    val encodedDestination = URLEncoder.encode(destinationString, "UTF-8")

    // Build the API URL with the dynamic travelMode
    val url = "https://maps.googleapis.com/maps/api/directions/json?" +
            "origin=$encodedOrigin&destination=$encodedDestination&mode=$travelMode&key=$apiKey"

    val request = Request.Builder().url(url).build()

    // Perform the network request on a background thread
    try {
        val response = withContext(Dispatchers.IO) {
            client.newCall(request).execute()
        }

        if (response.isSuccessful) {
            val responseBody = response.body?.string()

            val directionsResponse = Gson().fromJson(responseBody, DirectionsResponse::class.java)

            // Check if the response contains valid routes
            if (directionsResponse?.routes != null && directionsResponse.routes.isNotEmpty()) {
                val route = directionsResponse.routes[0]

                // Loop through legs and steps to collect polyline points
                for (leg in route.legs) {
                    for (step in leg.steps) {
                        val stepPolyline = step.polyline?.points
                        if (stepPolyline != null) {
                            val decodedPolyline = decodePolyline(stepPolyline)
                            polylineList.addAll(decodedPolyline)
                        }
                    }
                }
            } else {
                Log.e("Directions API", "No routes found in the response")
            }
        } else {
            Log.e("Directions API", "Request failed with status code: ${response.code}")
        }
    } catch (e: IOException) {
        Log.e("Directions API", "Error making request: ${e.message}")
    }

    // Return the list of LatLng points
    return polylineList
}


fun decodePolyline(encoded: String): List<LatLng> {
    val polyline = mutableListOf<LatLng>()
    var index = 0
    val len = encoded.length
    var lat = 0
    var lng = 0

    try {
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            // Add LatLng to the list
            polyline.add(LatLng(lat / 1E5, lng / 1E5))
        }
    } catch (e: Exception) {
        Log.e("Polyline Decode", "Error decoding polyline: ${e.message}")
    }

    return polyline
}
