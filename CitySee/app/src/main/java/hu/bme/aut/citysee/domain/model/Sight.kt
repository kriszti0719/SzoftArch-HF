package hu.bme.aut.citysee.domain.model

data class Sight (
    val id: String = "",
    val name: String = "",
    val city: String = "",
    val type: Type = Type.NONE,
    val description: String = "",
    val bonusInfo: String = ""
)