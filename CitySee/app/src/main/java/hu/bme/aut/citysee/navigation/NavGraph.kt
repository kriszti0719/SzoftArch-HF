package hu.bme.aut.citysee.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import hu.bme.aut.citysee.feature.auth.login.LoginScreen
import hu.bme.aut.citysee.feature.auth.register.RegisterScreen
import hu.bme.aut.citysee.feature.home.HomeScreen
import hu.bme.aut.citysee.feature.home_list.SightsScreen
import hu.bme.aut.citysee.feature.map.CityMapScreen
import hu.bme.aut.citysee.feature.profile.ProfileScreen
import hu.bme.aut.citysee.feature.sight_create.SightCreateScreen
import hu.bme.aut.citysee.feature.sight_details.SightDetailsScreen
import hu.bme.aut.citysee.feature.home_cities.CitiesScreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
        //startDestination = Screen.CityMap.passId("a8ILKBpSELkxln9MZUmy")
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onSuccess = {
                    navController.navigate(Screen.Cities.route)
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack(
                        route = Screen.Login.route,
                        inclusive = true
                    )
                    navController.navigate(Screen.Login.route)
                },
                onSuccess = {
                    navController.navigate(Screen.Sights.route)
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen (
                onSignOut = {
                    navController.popBackStack(
                        route = Screen.Login.route,
                        inclusive = true
                    )
                    navController.navigate(Screen.Login.route)
                }
            )
        }
        composable(Screen.Sights.route) {
            SightsScreen(
                onListItemClick = {
                    navController.navigate(Screen.SightDetails.passId(it))
                },
                onFabClick = {
                    navController.navigate(Screen.SightCreate.route)
                },
                onSignOut = {
                    navController.popBackStack(
                        route = Screen.Login.route,
                        inclusive = true
                    )
                    navController.navigate(Screen.Login.route)
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
                , onSignOut = {
                    navController.popBackStack(
                        route = Screen.Login.route,
                        inclusive = false
                    )
                    navController.navigate(Screen.Login.route)
                }
            )
        }
        composable(Screen.SightCreate.route) {
            SightCreateScreen(onNavigateBack = {
                navController.popBackStack()
/*                navController.popBackStack(
                    route = Screen.Sights.route,
                    inclusive = true
                )
                navController.navigate(Screen.CityMap.route)*/
            })
        }
        composable(
            route = Screen.SightDetails.route,
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                }
            )
        ) {
            SightDetailsScreen(
                onNavigateBack = {
                    navController.popBackStack(
                        route = Screen.CityMap.route,
                        inclusive = false
                    )
                    //navController.navigate(Screen.CityMap.route)
                }
            )
        }
        composable(route = Screen.CityMap.route,
            arguments = listOf(
            navArgument("id") {
                type = NavType.StringType
            }
        )){
            CityMapScreen(
                onMarkerClick = {
                    navController.navigate(Screen.SightDetails.passId(it))
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                }
                , onFabClick = {
                    navController.navigate(Screen.SightCreate.route)
                }
            )
        }
        composable(route = Screen.Cities.route) {
            CitiesScreen(
                onCityClick = {
                    navController.navigate(Screen.CityMap.passId(it))
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }
    }
}