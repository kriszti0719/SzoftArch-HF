package hu.bme.aut.citysee.domain.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val profileImageUrl: String? = null,
    val visitedSights: List<String> = emptyList(),
    var level: Int = 1,
    var points: Int = 100,
    private val badges: List<Badge> = emptyList()
)
