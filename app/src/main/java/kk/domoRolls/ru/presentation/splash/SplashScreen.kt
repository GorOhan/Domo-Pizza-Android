package kk.domoRolls.ru.presentation.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kk.domoRolls.ru.R
import kk.domoRolls.ru.presentation.navigation.Screen
import kk.domoRolls.ru.presentation.theme.DomoTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavHostController,
    viewModel: SplashViewModel = hiltViewModel()
) {

    val userId by viewModel.userId.collectAsState()
    val preloaderLottieComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(
            R.raw.domo3
        )
    )

    val preloaderProgress by animateLottieCompositionAsState(
        preloaderLottieComposition,
        isPlaying = true
    )

    LaunchedEffect(preloaderProgress, userId) {
        if (preloaderProgress == 1.0F) {
            userId?.let {
                val screen = if (it.isNotBlank()) Screen.NotifyPermissionScreen.route
                else Screen.RegistrationScreen.route
                navController.navigate(screen) {
                    popUpTo(navController.graph.id) {
                        inclusive = false
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .background(color = Color.White)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        LottieAnimation(
            composition = preloaderLottieComposition,
            progress = preloaderProgress,
            modifier = Modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    DomoTheme {
        // SplashScreen()
    }
}

