package kk.domoRolls.ru.presentation.story

import android.annotation.SuppressLint
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.ProgressIndicatorDefaults.linearTrackColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import kk.domoRolls.ru.domain.model.Promo
import kk.domoRolls.ru.presentation.theme.DomoBlue
import kk.domoRolls.ru.presentation.theme.DomoSub
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

    val pagerState = rememberPagerState {
        promo?.size ?: 0
    }

    val pagerScope = rememberCoroutineScope()

    var enabled by remember { mutableStateOf(false) }
    val progress: Float by animateFloatAsState(
        if (enabled) 1f else 0.0f,
        animationSpec = tween(
            durationMillis = 1500,
            delayMillis = 40,
            easing = LinearOutSlowInEasing
        ), label = ""
    )


    pagerScope.launch {
        enabled = true
        pagerState.scrollToPage(currentIndex)
        delay(1500L)
//        repeat(promo?.size ?: (1 - currentIndex)) {
//            if (pagerState.currentPage == 3) {
//                onBackClick()
//            }
//            delay(1500L)
//            pagerState.animateScrollToPage(
//                page = (pagerState.currentPage + 1),
//            )
//        }
    }

    HorizontalPager(state = pagerState) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            Image(
                modifier = Modifier.fillMaxSize(),
                painter = rememberAsyncImagePainter(model = promo?.get(pagerState.currentPage)?.storyImage),
                contentDescription = ""
            )
        }
    }
    LinearProgressIndicator(
        progress = progress,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 64.dp),
        color = ProgressIndicatorDefaults.linearColor,
        trackColor = linearTrackColor,
        strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
    )
}

@Preview(showBackground = true)
@Composable
fun OTPScreenPreview() {
    DomoTheme {
        StoryScreenUI()

    }
}