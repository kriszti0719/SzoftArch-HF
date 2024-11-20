package hu.bme.aut.citysee.domain.model

data class City (
    val id: String,
    val name: String,
    val sights: List<Sight>
    )