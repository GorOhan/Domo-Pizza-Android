package kk.domoRolls.ru.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kk.domoRolls.ru.presentation.cart.CartScreen
import kk.domoRolls.ru.presentation.html.HTMLScreenType
import kk.domoRolls.ru.presentation.html.HtmlScreen
import kk.domoRolls.ru.presentation.main.MainScreen
import kk.domoRolls.ru.presentation.myprofile.MyProfileScreen
import kk.domoRolls.ru.presentation.personaldata.PersonalDataScreen
import kk.domoRolls.ru.presentation.personaldata.PersonalDataUI
import kk.domoRolls.ru.presentation.registration.OTPScreen
import kk.domoRolls.ru.presentation.registration.RegistrationScreen
import kk.domoRolls.ru.presentation.registration.RegistrationViewModel
import kk.domoRolls.ru.presentation.splash.SplashScreen
import kk.domoRolls.ru.presentation.story.StoryScreen

sealed class Screen(val route: String) {
    data object SplashScreen : Screen("splashScreen")
    data object RegistrationScreen : Screen("registrationScreen")
    data object OTPScreen : Screen("otpScreen")
    data object NotifyPermissionScreen : Screen("notifyPermissionScreen")
    data object HTMLScreen : Screen("htmlScreen")
    data object MainScreen : Screen("mainScreen")
    data object StoryScreen : Screen("storyScreen")
    data object CartScreen : Screen("cartScreen")
    data object MyProfileScreen : Screen("myProfileScreen")
    data object PersonalDataScreen: Screen("personalDataScreen")

}

@Composable
internal fun NavMain(
) {
    val navController = rememberNavController()
    val registrationViewModel: RegistrationViewModel = hiltViewModel()

    NavHost(navController, startDestination = Screen.SplashScreen.route) {
        composable(route = Screen.SplashScreen.route) {
            SplashScreen(navController)
        }

        composable(route = Screen.RegistrationScreen.route) {
            RegistrationScreen(navController, registrationViewModel)
        }

        composable(route = Screen.OTPScreen.route) {
            OTPScreen(navController, registrationViewModel)
        }

        composable(route = Screen.NotifyPermissionScreen.route) {
            MainScreen(
                navController = navController
            )
        }

        composable(route = "${Screen.HTMLScreen.route}/{screenType}",
            arguments = listOf(
                navArgument("screenType") { type = NavType.StringType}),
            ) {backStackEntry ->
            val enumArgument = backStackEntry.arguments?.getString("screenType")

            HtmlScreen(
                htmlScreenType = enumArgument?.let { HTMLScreenType.valueOf(it) }?:run { HTMLScreenType.TERMS }
            )
        }

        composable(route = Screen.MainScreen.route) {
            MainScreen(
                navController = navController
            )
        }

        composable(
            route = "${Screen.StoryScreen.route}/{currentIndex}",
            arguments = listOf(
                navArgument("currentIndex") { type = NavType.IntType }),
        ) { backStackEntry ->
            StoryScreen(
                currentIndex = backStackEntry.arguments?.getInt("currentIndex") ?: 0,
                navController = navController,
            )
        }

        composable(route = Screen.CartScreen.route) {
            CartScreen(
                navController = navController
            )
        }

        composable(route = Screen.MyProfileScreen.route) {
            MyProfileScreen(
                navController = navController
            )
        }

        composable(route = Screen.PersonalDataScreen.route) {
            PersonalDataScreen(
                navController = navController
            )
        }
    }
}
