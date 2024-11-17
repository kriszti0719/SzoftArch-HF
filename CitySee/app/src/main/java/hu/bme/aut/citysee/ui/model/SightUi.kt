package hu.bme.aut.citysee.ui.model

import hu.bme.aut.citysee.domain.model.Sight
import hu.bme.aut.citysee.domain.model.Type
import java.time.LocalDate
import java.time.LocalDateTime

data class SightUi(
    val id: String = "",
    val name: String = "",
    val city: String = "",
    val type: TypeUi = TypeUi.None,
    val description: String = "",
    val bonusInfo: String = ""
)

fun Sight.asSightUi(): SightUi = SightUi(
    id = id,
    name = name,
    city = city,
    type = type.asTypeUi(),
    description = description,
    bonusInfo = bonusInfo
    )

fun SightUi.asSight(): Sight = Sight(
    id = id,
    name = name,
    city = city,
    type = type.asType(),
    description = description,
    bonusInfo = bonusInfo
)