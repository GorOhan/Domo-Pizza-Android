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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
import kk.domoRolls.ru.domain.model.Promo
import kk.domoRolls.ru.domain.model.User
import kk.domoRolls.ru.presentation.components.CartButton
import kk.domoRolls.ru.presentation.components.DomoLoading
import kk.domoRolls.ru.presentation.components.MenuItemComponent
import kk.domoRolls.ru.presentation.navigation.Screen
import kk.domoRolls.ru.presentation.registration.gridItems
import kk.domoRolls.ru.presentation.theme.DomoBlue
import kk.domoRolls.ru.presentation.theme.DomoTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    val promo by mainViewModel.promoList.collectAsState()

    MainScreenUI(
        menuState = mainViewModel.menu,
        promoList = promo,
        onNavigationClick = {
            navController.navigate(it)
        },
        onAddToCart = { mainViewModel.addToCart(it) },
        onRemoveFromCart = { mainViewModel.removeFromCart(it) },
        showLoadingState = mainViewModel.showLoading,
        userState = mainViewModel.user,
        categoriesState = mainViewModel.categories,
        onCategoryCheck = {
            mainViewModel.categoryCheck(it)
        }
    )
}

@Composable
fun MainScreenUI(
    promoList: List<Promo> = emptyList(),
    menuState: StateFlow<List<MenuItem>> = MutableStateFlow(emptyList()),
    onNavigationClick: (String) -> Unit = {},
    onAddToCart: (MenuItem) -> Unit = {},
    onRemoveFromCart: (MenuItem) -> Unit = {},
    showLoadingState: StateFlow<Boolean> = MutableStateFlow(false),
    userState: StateFlow<User> = MutableStateFlow(User()),
    categoriesState: StateFlow<List<ItemCategory>> = MutableStateFlow(emptyList()),
    onCategoryCheck:(ItemCategory)->Unit = {}
) {
    val menu by menuState.collectAsState()
    val showLoading by showLoadingState.collectAsState()
    val user by userState.collectAsState()
    val categories by categoriesState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center

    ) {
        CartButton(
            modifier = Modifier
                .padding(horizontal = 22.dp, vertical = 64.dp)
                .zIndex(1f)
                .align(Alignment.BottomCenter),
            size = 4, price = "450", backgroundColor = DomoBlue,)

        ContentSection(
            promoList = promoList,
            menu = menu,
            onPlusClick = onAddToCart,
            onMinusClick = onRemoveFromCart,
            onNavigationClick = onNavigationClick,
            categories = categories,
            onCategoryCheck = onCategoryCheck,
            user = user
        )

    }

    if (showLoading) {
        DomoLoading()
    }


}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContentSection(
    promoList: List<Promo>,
    menu: List<MenuItem>,
    onMinusClick: (MenuItem) -> Unit = {},
    onPlusClick: (MenuItem) -> Unit = {},
    onNavigationClick: (String) -> Unit,
    categories: List<ItemCategory>,
    onCategoryCheck:(ItemCategory)->Unit = {},
    user:User,
) {
    val insets = WindowInsets.statusBars
    val statusBarHeight = insets.asPaddingValues().calculateTopPadding()

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier
            .padding(top = statusBarHeight)
            .fillMaxSize(),
        state = listState

    ) {

        item {
            Row(
                modifier = Modifier
                    .padding(horizontal = 22.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Привет ${user.name}")
                    Text(text = "address")
                }
                Image(
                    modifier = Modifier.size(32.dp),
                    painter = painterResource(id = R.drawable.ic_profile),
                    contentDescription = ""
                )
            }
        }

        item {
            StorySection(
                promo = promoList,
                onNavigationClick = onNavigationClick
            )
        }

        stickyHeader {
            LazyRow(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .zIndex(1f)
                    .background(Color.White)
                ,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                itemsIndexed(items = categories, itemContent = { index, item ->
                    Box(
                        modifier = Modifier
                            .clickable {
                                onCategoryCheck(item)
                                coroutineScope.launch {
                                    listState.animateScrollToItem(
                                        index = (menu.indexOfFirst {
                                            it.categoryId == item.id
                                        } / 2) + index + 2,
                                    )
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
                            )
                        ,

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
                    onMinusClick = { onMinusClick(item) },
                    onPlusClick = { onPlusClick(item) }
                )
            }

        }

    }
}

@Composable
fun StorySection(
    promo: List<Promo>,
    onNavigationClick: (String) -> Unit,
) {
    LazyRow() {
        items(items = promo, itemContent = { item ->
            AsyncImage(
                modifier = Modifier
                    .clickable {
                        onNavigationClick("${Screen.StoryScreen.route}/${promo.indexOf(item)}")
                    }
                    .height(124.dp)
                    .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
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