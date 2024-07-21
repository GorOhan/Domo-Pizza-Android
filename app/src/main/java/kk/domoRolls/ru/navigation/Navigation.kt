package kk.domoRolls.ru.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kk.domoRolls.ru.onboarding.NotifyPermissionScreen
import kk.domoRolls.ru.registration.OTPScreen
import kk.domoRolls.ru.registration.RegistrationScreen
import kk.domoRolls.ru.splash.SplashScreen

sealed class Screen(val route: String) {
    data object SplashScreen : Screen("splashScreen")
    data object RegistrationScreen : Screen("registrationScreen")
    data object OTPScreen : Screen("otpScreen")
    data object NotifyPermissionScreen : Screen("notifyPermissionScreen")
}

@Composable
internal fun NavMain(
) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = Screen.SplashScreen.route) {

        composable(route = Screen.SplashScreen.route) {
            SplashScreen(navController)
        }

        composable(route = Screen.RegistrationScreen.route) {
            RegistrationScreen(navController)
        }

        composable(route = Screen.OTPScreen.route) {
            OTPScreen(navController)
        }

        composable(route = Screen.NotifyPermissionScreen.route) {
            NotifyPermissionScreen()
        }

//
//        composable(
//            route = "${Screen.OtpVerificationCode.route}/{retrySeconds}/{phone}",
//            arguments = listOf(
//                navArgument("retrySeconds") { type = NavType.IntType },
//                navArgument("phone") { type = NavType.StringType })
//        ) { backStackEntry ->
//
//            OtpVerificationCode(
//                navController = navController,
//                phoneNumber = backStackEntry.arguments?.getString("phone") ?: "",
//                retrySecondsArg = backStackEntry.arguments?.getInt("retrySeconds") ?: 10
//            )
//        }

//        composable(
//            route = "${Screen.OnBoardingScreen.route}/{fromMainScreen}",
//            arguments = listOf(navArgument("fromMainScreen") { type = NavType.BoolType })
//        ) { backStackEntry ->
//            OnBoardingScreen(
//                navController = navController,
//                fromMainScreen = backStackEntry.arguments?.getBoolean("fromMainScreen") ?: true,
//            )
//        }
    }
}
