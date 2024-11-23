package hu.bme.aut.citysee.navigation

sealed class Screen(val route: String) {
    object Login: Screen("login")
    object Register: Screen("register")

    object Home: Screen(route = "home")
    object Profile: Screen(route = "profile/{${Args.username}}") {
        fun passUsername(username: String) = "profile/$username"
        object Args {
            const val username = "username"
        }
    }
    object Settings: Screen(route = "settings")

    object Sights: Screen("sights")
    object SightCreate: Screen("create")
    object SightDetails: Screen("details/{id}") {
        fun passId(id: String) = "details/$id"
    }
    object CityMap: Screen("cityMap/{id}") {
        fun passId(id: String) = "cityMap/$id"
    }
    object Cities: Screen("cities")
}
