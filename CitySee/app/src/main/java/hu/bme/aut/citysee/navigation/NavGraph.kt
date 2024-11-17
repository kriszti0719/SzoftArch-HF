package hu.bme.aut.citysee.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import hu.bme.aut.citysee.feature.auth.login.LoginScreen
import hu.bme.aut.citysee.feature.auth.register.RegisterScreen
import hu.bme.aut.citysee.feature.home.HomeScreen
import hu.bme.aut.citysee.feature.home_list.SightsScreen
import hu.bme.aut.citysee.feature.sight_create.CreateSightScreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onSuccess = {
                    navController.navigate(Screen.Sights.route)
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
//                    navController.navigate(Screen.SightDetails.passId(it))
                },
                onFabClick = {
                    navController.navigate(Screen.CreateSight.route)
                },
                onSignOut = {
                    navController.popBackStack(
                        route = Screen.Login.route,
                        inclusive = true
                    )
                    navController.navigate(Screen.Login.route)
                }
            )
        }
        composable(Screen.CreateSight.route) {
            CreateSightScreen(onNavigateBack = {
                navController.popBackStack(
                    route = Screen.Sights.route,
                    inclusive = true
                )
                navController.navigate(Screen.Sights.route)
            })
        }
    }
}