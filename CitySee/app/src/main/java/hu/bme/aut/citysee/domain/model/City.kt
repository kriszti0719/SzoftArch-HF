package hu.bme.aut.citysee.domain.model

data class City (
    val id: String = "",
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val sights: List<String> = emptyList()
    )
