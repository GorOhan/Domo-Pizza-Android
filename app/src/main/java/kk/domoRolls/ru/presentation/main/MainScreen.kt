package kk.domoRolls.ru.presentation.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.AsyncImage
import kk.domoRolls.ru.R
import kk.domoRolls.ru.data.model.order.ItemCategory
import kk.domoRolls.ru.data.model.order.MenuItem
import kk.domoRolls.ru.domain.model.PromoStory
import kk.domoRolls.ru.domain.model.User
import kk.domoRolls.ru.domain.model.address.Address
import kk.domoRolls.ru.presentation.components.CartButton
import kk.domoRolls.ru.presentation.components.DomoLoading
import kk.domoRolls.ru.presentation.components.MenuItemComponent
import kk.domoRolls.ru.presentation.components.ProductBottomSheet
import kk.domoRolls.ru.presentation.navigation.Screen
import kk.domoRolls.ru.presentation.registration.gridItems
import kk.domoRolls.ru.presentation.theme.DomoBlue
import kk.domoRolls.ru.presentation.theme.DomoTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    val promo by mainViewModel.promoList.collectAsState()
    val menu by mainViewModel.menu.collectAsState()
    val showLoading by mainViewModel.showLoading.collectAsState()
    val user by mainViewModel.user.collectAsState()
    val categories by mainViewModel.categories.collectAsState()
    val defaultAddress by mainViewModel.defaultAddress.collectAsState()
    val toProfile by mainViewModel.toProfile.collectAsState(initial = false)
    val toAuth by mainViewModel.toAuth.collectAsState(initial = false)

    LaunchedEffect(toProfile) {
        if (toProfile) navController.navigate(Screen.MyProfileScreen.route)
    }

    LaunchedEffect(toAuth) {
        if (toAuth) navController.popBackStack()
    }

    MainScreenUI(
        showLoading = showLoading,
        promoStoryList = promo,
        menu = menu,
        defaultAddress = defaultAddress,
        categories = categories,
        user = user,
        onEvent = {
            when (it) {
                MainScreenEvent.BackClick -> {
                    navController.popBackStack()
                }

                is MainScreenEvent.NavigateClick -> {
                    navController.navigate(it.route)
                }

                MainScreenEvent.Nothing -> {}
                is MainScreenEvent.AddToCart -> {
                    mainViewModel.addToCart(it.menuItem)
                }

                is MainScreenEvent.RemoveFromCart -> {
                    mainViewModel.removeFromCart(it.menuItem)
                }

                is MainScreenEvent.CategoryCheck -> {
                    mainViewModel.categoryCheck(it.category)
                }

                MainScreenEvent.ProfileClick -> {
                    mainViewModel.handleProfileClick()
                }
            }
        }
    )
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreenUI(
    showLoading: Boolean = false,
    promoStoryList: List<PromoStory> = emptyList(),
    menu: List<MenuItem> = emptyList(),
    categories: List<ItemCategory> = emptyList(),
    user: User = User(),
    defaultAddress: Address = Address(),
    onEvent: (MainScreenEvent) -> Unit = {},
) {
    val insets = WindowInsets.statusBars
    val statusBarHeight = insets.asPaddingValues().calculateTopPadding()

    val menuState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val isCategoryClicked = remember {
        mutableStateOf(false)
    }

    var showBottomSheet by remember { mutableStateOf(false) }
    var currentItem by remember { mutableStateOf(MenuItem()) }

    val sheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (showBottomSheet) {
        ModalBottomSheet(
            containerColor = Color.White,
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState
        ) {
            ProductBottomSheet(
                menuItem = currentItem,
                menu = menu,
                onMinusClick = { onEvent(MainScreenEvent.RemoveFromCart(currentItem)) },
                onPlusClick = { onEvent(MainScreenEvent.AddToCart(currentItem)) }
            )
        }
    }


    LaunchedEffect(key1 = remember { derivedStateOf { menuState.firstVisibleItemIndex } }) {
        var accumulatedSize = 0

        categories.forEachIndexed { index, category ->
            val categorySize = menu.filter { it.categoryId == category.id }.size
            val thresholdIndex = index + (accumulatedSize + categorySize) / 2

            if (menuState.firstVisibleItemIndex == thresholdIndex && !isCategoryClicked.value) {
                onEvent(MainScreenEvent.CategoryCheck(category))
            }

            accumulatedSize += categorySize
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center

    ) {
        if (menu.sumOf { it.countInCart } > 0) {
            CartButton(
                menu = menu,
                modifier = Modifier
                    .padding(horizontal = 22.dp, vertical = 64.dp)
                    .zIndex(1f)
                    .align(Alignment.BottomCenter),
                backgroundColor = DomoBlue,
                onClick = {
                    if (user.id.isNotEmpty()) onEvent(MainScreenEvent.NavigateClick(Screen.CartScreen.route))
                    else onEvent(MainScreenEvent.BackClick)
                }
            )
        }

        LazyColumn(
            modifier = Modifier
                .padding(top = statusBarHeight)
                .fillMaxSize(),
            state = menuState

        ) {

            item {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 22.dp)
                        .fillMaxWidth()
                        .padding(top = 22.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Привет ${user.name}")
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier
                                    .clickable {
                                        onEvent(MainScreenEvent.NavigateClick("${Screen.AddressMapScreen.route}/${defaultAddress.id.ifEmpty { null }}"))
                                    },
                                text = defaultAddress.street,
                                overflow = TextOverflow.Ellipsis,

                                )
                            if (user.id.isNotEmpty()) {
                                Icon(
                                    modifier = Modifier
                                        .padding(start = 4.dp)
                                        .size(8.dp),
                                    painter = painterResource(id = R.drawable.ic_nav),
                                    contentDescription = "",
                                    tint = DomoBlue
                                )
                            }
                        }
                    }
                    Image(
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { onEvent(MainScreenEvent.ProfileClick) },
                        painter = painterResource(id = R.drawable.ic_profile),
                        contentDescription = ""
                    )
                }
            }

            item {
                StorySection(
                    promoStory = promoStoryList,
                    onNavigationClick = { onEvent(MainScreenEvent.NavigateClick(it)) }
                )
            }

            stickyHeader {
                val rowState = rememberLazyListState()

                LaunchedEffect(key1 = categories) {
                    if (categories.any { it.isChecked }) {
                        val indexOfItem = categories.indexOfFirst { it.isChecked }
                        rowState.animateScrollToItem(indexOfItem)
                    }
                }

                LazyRow(
                    state = rowState,
                    modifier = Modifier
                        .background(Color.White)
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .zIndex(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    itemsIndexed(items = categories, itemContent = { index, item ->
                        Box(
                            modifier = Modifier
                                .padding(
                                    start = if (index == 0) 16.dp else 0.dp,
                                    end = if (index == categories.size - 1) 16.dp else 0.dp
                                )
                                .clickable {
                                    isCategoryClicked.value = true
                                    onEvent(MainScreenEvent.CategoryCheck(item))
                                    coroutineScope.launch {
                                        menuState.animateScrollToItem(
                                            index = (menu.indexOfFirst {
                                                it.categoryId == item.id
                                            } / 2) + index + 2,
                                        )
                                        delay(500L)
                                        isCategoryClicked.value = false
                                    }
                                }
                                .border(
                                    width = 1.dp,
                                    shape = RoundedCornerShape(24.dp),
                                    color = DomoBlue
                                )
                                .background(
                                    if (item.isChecked) MaterialTheme.colorScheme.secondary else Color.White,
                                    RoundedCornerShape(24.dp)
                                ),

                            ) {
                            Text(
                                modifier = Modifier.padding(8.dp),
                                color = if (!item.isChecked) MaterialTheme.colorScheme.secondary
                                else Color.White,
                                text = item.name ?: "",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    })
                }
            }


            categories.forEach { category ->
                item {
                    Text(
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(all = 16.dp),
                        text = category.name ?: ""
                    )
                }
                gridItems(menu.filter { it.categoryId == category.id }, nColumns = 2) { item ->
                    MenuItemComponent(
                        menuItem = item,
                        onMinusClick = { onEvent(MainScreenEvent.RemoveFromCart(item)) },
                        onPlusClick = { onEvent(MainScreenEvent.AddToCart(item)) },
                        onProductClick = {
                            showBottomSheet = true
                            currentItem = item
                        }
                    )
                }

            }

        }

        if (showLoading) {
            DomoLoading()
        }

    }
}

@Composable
fun StorySection(
    promoStory: List<PromoStory>,
    onNavigationClick: (String) -> Unit,
) {
    LazyRow {
        itemsIndexed(items = promoStory, itemContent = { inex, item ->
            AsyncImage(
                modifier = Modifier
                    .clickable {
                        onNavigationClick("${Screen.StoryScreen.route}/${promoStory.indexOf(item)}")
                    }
                    .height(124.dp)
                    .padding(
                        start = 16.dp,
                        top = 8.dp,
                        bottom = 8.dp,
                        end = if (inex == promoStory.size - 1) 16.dp else 0.dp
                    )
                    .clip(RoundedCornerShape(20.dp)),
                model = item.bannerImage,
                contentDescription = "", imageLoader = ImageLoader(LocalContext.current))
        })

    }

}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    DomoTheme {
        MainScreenUI()
    }
}

sealed class MainScreenEvent {
    data class CategoryCheck(val category: ItemCategory) : MainScreenEvent()
    data class RemoveFromCart(val menuItem: MenuItem) : MainScreenEvent()
    data class AddToCart(val menuItem: MenuItem) : MainScreenEvent()
    data class NavigateClick(val route: String) : MainScreenEvent()
    data object ProfileClick : MainScreenEvent()
    data object BackClick : MainScreenEvent()
    data object Nothing : MainScreenEvent()
}