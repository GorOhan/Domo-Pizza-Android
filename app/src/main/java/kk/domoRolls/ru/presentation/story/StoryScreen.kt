package kk.domoRolls.ru.presentation.story


import android.annotation.SuppressLint
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import kk.domoRolls.ru.domain.model.Promo
import kk.domoRolls.ru.presentation.theme.DomoTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


@Composable
fun StoryScreen(
    navController: NavHostController,
    currentIndex: Int = 0,
    storyViewModel: StoryViewModel = hiltViewModel()
) {
    StoryScreenUI(
        promoState = storyViewModel.promoList,
        currentIndex = currentIndex,
        onBackClick = { navController.navigateUp() }
    )
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StoryScreenUI(
    promoState: StateFlow<List<Promo>?> = MutableStateFlow(emptyList()),
    currentIndex: Int = 0,
    onBackClick: () -> Unit = {},
) {
    val promo by promoState.collectAsState()

    val backClicked = remember {
        mutableStateOf(false)
    }
    val pagerState = rememberPagerState {
        promo?.size ?: 0
    }

    val pagerScope = rememberCoroutineScope()
    pagerScope.launch {

        pagerState.scrollToPage(currentIndex)
        delay(2500)
        if (!backClicked.value) onBackClick()
        backClicked.value = true

    }

    // Remember the animation state
    val infiniteTransition = rememberInfiniteTransition(label = "")

    // Animate the float value from 0 to 1 and repeat it 4 times
    val animationSpec = infiniteRepeatable<Float>(
        animation = tween(durationMillis = 2500),
        repeatMode = RepeatMode.Restart,
    )


    val animatedValue by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = animationSpec, label = ""
    )

    Box {
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false
        ) {

            Box(
                modifier = Modifier.fillMaxSize()
            ) {


                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = rememberAsyncImagePainter(model = promo?.get(pagerState.currentPage)?.storyImage),
                    contentDescription = ""
                )

                LinearProgressIndicator(
                    progress = animatedValue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .width(1.dp)
                        .padding(top = 64.dp),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.2f),
                    strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,

                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun OTPScreenPreview() {
    DomoTheme {
        StoryScreenUI()

    }
}