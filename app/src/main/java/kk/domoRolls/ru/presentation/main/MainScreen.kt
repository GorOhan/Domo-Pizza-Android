package kk.domoRolls.ru.presentation.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.AsyncImage
import kk.domoRolls.ru.R
import kk.domoRolls.ru.data.model.order.MenuItem
import kk.domoRolls.ru.domain.model.Promo
import kk.domoRolls.ru.domain.model.User
import kk.domoRolls.ru.presentation.components.DomoLoading
import kk.domoRolls.ru.presentation.components.MenuItemComponent
import kk.domoRolls.ru.presentation.navigation.Screen
import kk.domoRolls.ru.presentation.theme.DomoTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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
        userState = mainViewModel.user
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
    userState: StateFlow<User> = MutableStateFlow(User())
) {
    val menu by menuState.collectAsState()
    val showLoading by showLoadingState.collectAsState()
    val user by userState.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(start = 22.dp, end = 22.dp, top = 64.dp)
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
                painter = painterResource(id = R.drawable.ic_domo),
                contentDescription = ""
            )
        }
        StorySection(
            promo = promoList,
            onNavigationClick = onNavigationClick
        )

        MenuSection(
            menu = menu,
            onPlusClick = onAddToCart,
            onMinusClick = onRemoveFromCart,
        )
    }

    if (showLoading){
        DomoLoading()
    }

}


@Composable
fun MenuSection(
    menu: List<MenuItem>,
    onMinusClick: (MenuItem) -> Unit = {},
    onPlusClick: (MenuItem) -> Unit = {},
) {
    LazyVerticalGrid(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .fillMaxWidth(),
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.Center,
        userScrollEnabled = true,
    ) {
        itemsIndexed(items = menu) { inex, item ->
            MenuItemComponent(
                menuItem = item,
                onMinusClick = { onMinusClick(item) },
                onPlusClick = { onPlusClick(item) }
                )
        }
    }
}

@Composable
fun StorySection(
    promo: List<Promo>,
    onNavigationClick: (String) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items = promo, itemContent = { item ->
            AsyncImage(
                modifier = Modifier
                    .clickable {
                        onNavigationClick("${Screen.StoryScreen.route}/${promo.indexOf(item)}")
                    }
                    .height(160.dp)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(20.dp)),
                model = item.bannerImage,
                contentDescription = "", imageLoader = ImageLoader(LocalContext.current) )
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