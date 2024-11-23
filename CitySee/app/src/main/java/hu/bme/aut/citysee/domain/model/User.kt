package hu.bme.aut.citysee.domain.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val profileImageUrl: String? = null,
    val visitedSights: List<String> = emptyList()
)
