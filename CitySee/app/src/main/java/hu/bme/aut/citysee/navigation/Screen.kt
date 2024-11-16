package hu.bme.aut.citysee.navigation

sealed class Screen(val route: String) {
    object Login: Screen("login")
    object Register: Screen("register")
}
