package kk.domoRolls.ru.presentation.story

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
        onBackClick = { navController.popBackStack() }
    )
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StoryScreenUI(
    promoState: StateFlow<List<Promo>?> = MutableStateFlow(emptyList()),
    currentIndex: Int = 0,
    onBackClick:()->Unit = {},
) {
    val promo by promoState.collectAsState()

    val pagerState = rememberPagerState {
        promo?.size ?: 0
    }

    val pagerScope = rememberCoroutineScope()

    pagerScope.launch {
        pagerState.animateScrollToPage(currentIndex)
        delay(1500L)
        repeat(promo?.size ?: (1 - currentIndex)){
            if(pagerState.currentPage == 3){
                onBackClick()
            }
            delay(1500L)
            pagerState.animateScrollToPage(
                page = (pagerState.currentPage + 1),
            )
        }
    }

    HorizontalPager(state = pagerState) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = rememberAsyncImagePainter(model = promo?.get(pagerState.currentPage)?.storyImage),
                contentDescription = ""
            )
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