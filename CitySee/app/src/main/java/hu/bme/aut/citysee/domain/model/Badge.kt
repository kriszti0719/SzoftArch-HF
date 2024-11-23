package hu.bme.aut.citysee.domain.model

data class Badge(
    val name: String,
    val description: String,
    var isUnlocked: Boolean = false
)

{
    fun unlock(){
        isUnlocked = true
    }
}
