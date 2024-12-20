package hu.bme.aut.citysee.ui.model

import hu.bme.aut.citysee.domain.model.City
import hu.bme.aut.citysee.domain.model.Sight

data class CityUi (
    val id: String = "",
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    var sights: List<SightUi> = emptyList()
)

fun City.asCityUi(): CityUi = CityUi(
    id = id,
    name = name,
    latitude = latitude,
    longitude = longitude,
    //sights = sights.map { it.asSightUi() }
)

fun CityUi.asCity(): City = City(
    id = id,
    name = name,
    latitude = latitude,
    longitude = longitude,
    //sights = sights.map { it.asSight() }
)
