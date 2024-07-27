package kk.domoRolls.ru.presentation.story

import android.annotation.SuppressLint
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import kk.domoRolls.ru.domain.model.Promo
import kk.domoRolls.ru.presentation.theme.DomoTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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
    val backClicked = remember { mutableStateOf(false) }
    val pagerState = rememberPagerState { promo?.size ?: 0 }
    val pagerScope = rememberCoroutineScope()

    pagerScope.launch {
        withContext(Dispatchers.Main) {
            pagerState.scrollToPage(currentIndex)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "")
    val animationSpec = infiniteRepeatable<Float>(
        animation = tween(
            delayMillis = 11,
            durationMillis = 3500,
            easing = LinearEasing
        ),
        repeatMode = RepeatMode.Restart,
    )
    val animatedValue by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = animationSpec, label = ""
    )

    LaunchedEffect(Unit) {
        repeat(pagerState.pageCount - currentIndex + 1) {
            delay(3500)
            val nextPage = (pagerState.currentPage + 1) % 5 // Assuming you have 5 pages
            pagerScope.launch {
                if (pagerState.pageCount == pagerState.currentPage + 1) {
                    if (!backClicked.value) onBackClick()
                    backClicked.value = true
                } else {
                    pagerState.animateScrollToPage(nextPage)
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false
        ) {

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize(),
                    painter = rememberAsyncImagePainter(model = promo?.get(pagerState.currentPage)?.storyImage),
                    contentDescription = ""
                )

                LinearProgressIndicator(
                    progress = animatedValue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .width(1.dp)
                        .padding(top = 52.dp),
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