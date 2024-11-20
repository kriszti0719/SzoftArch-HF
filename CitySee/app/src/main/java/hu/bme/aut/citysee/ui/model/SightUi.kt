package hu.bme.aut.citysee.ui.model

import com.google.firebase.firestore.GeoPoint
import hu.bme.aut.citysee.domain.model.Sight
import hu.bme.aut.citysee.domain.model.Type
import java.time.LocalDate
import java.time.LocalDateTime

data class SightUi(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val type: TypeUi = TypeUi.None,
    val description: String = "",
    val bonusInfo: String = "",
    val photos: List<String> = emptyList(),
)

fun Sight.asSightUi(): SightUi = SightUi(
    id = id,
    name = name,
    address = address,
    type = type.asTypeUi(),
    description = description,
    bonusInfo = bonusInfo,
    photos = photos,
    )

fun SightUi.asSight(): Sight = Sight(
    id = id,
    name = name,
    address = address,
    type = type.asType(),
    description = description,
    bonusInfo = bonusInfo,
    photos = photos,
)