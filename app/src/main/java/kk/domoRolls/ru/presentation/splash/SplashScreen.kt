package kk.domoRolls.ru.presentation.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kk.domoRolls.ru.R
import kk.domoRolls.ru.presentation.components.BaseButton
import kk.domoRolls.ru.presentation.navigation.Screen
import kk.domoRolls.ru.presentation.theme.DomoBlue

@Composable
fun SplashScreen(
    navController: NavHostController,
    viewModel: SplashViewModel = hiltViewModel(),
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
    val isAppAvailable by viewModel.isAppAvailable.collectAsState()

    LaunchedEffect(preloaderProgress, userId,isAppAvailable) {
        if (preloaderProgress == 1.0F) {
            userId?.let {
                val screen = if (it.isNotBlank()) Screen.NotifyPermissionScreen.route
                else Screen.RegistrationScreen.route
                if (isAppAvailable) {
                    navController.navigate(screen) {
                        popUpTo(navController.graph.id) {
                            inclusive = false
                        }
                    }
                }
            }
        }
    }


    Box(
        modifier = Modifier
            .background(color = Color.White)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        LottieAnimation(
            modifier = Modifier.align(Alignment.Center),
            composition = preloaderLottieComposition,
            progress = preloaderProgress,
        )

        if (isAppAvailable.not()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
                    .align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    style = MaterialTheme.typography.titleMedium,
                    text = "Извините, приложение в данный момент недоступно.",
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                )
                BaseButton(
                    onClick = { viewModel.initApp() },
                    buttonTitle = "Повторить",
                    backgroundColor = DomoBlue,
                    modifier = Modifier.padding(top = 16.dp, bottom = 64.dp)
                )
            }
        }
    }
}

