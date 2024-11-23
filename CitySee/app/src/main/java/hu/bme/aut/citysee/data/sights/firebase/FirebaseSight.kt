package hu.bme.aut.citysee.data.sights.firebase

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint
import com.google.type.LatLng
import hu.bme.aut.citysee.domain.model.Sight
import hu.bme.aut.citysee.domain.model.Type

data class FirebaseSight(
    @DocumentId val id: String = "",
    val name: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val type: Type = Type.NONE,
    val description: String = "",
    val bonusInfo: String = "",
    val photos: List<String> = emptyList(), )

fun FirebaseSight.asSight() = Sight(
    id = id,
    name = name,
    address = address,
    latitude = latitude,
    longitude = longitude,
    type = type,
    description = description,
    bonusInfo = bonusInfo,
    photos = photos,
)

fun Sight.asFirebaseSight() = FirebaseSight(
    id = id,
    name = name,
    address = address,
    latitude = latitude,
    longitude = longitude,
    type = type,
    description = description,
    bonusInfo = bonusInfo,
    photos = photos,
)