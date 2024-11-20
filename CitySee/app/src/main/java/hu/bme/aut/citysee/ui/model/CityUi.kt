package hu.bme.aut.citysee.ui.model

import hu.bme.aut.citysee.domain.model.City
import hu.bme.aut.citysee.domain.model.Sight

data class CityUi (
    val id: String = "",
    val name: String = "",
    val sights: List<Sight> = emptyList()
)

fun City.asCityUi(): CityUi = CityUi(
    id = id,
    name = name,
    sights = sights
)

fun CityUi.asCity(): City = City(
    id = id,
    name = name,
    sights = sights
)
