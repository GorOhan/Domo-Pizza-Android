package kk.domoRolls.ru.presentation.cart

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kk.domoRolls.ru.data.model.order.MenuItem
import kk.domoRolls.ru.presentation.components.CartMenuItem
import kk.domoRolls.ru.presentation.components.Devices
import kk.domoRolls.ru.presentation.components.DomoToolbar
import kk.domoRolls.ru.presentation.components.GiftProductItem
import kk.domoRolls.ru.presentation.components.PromoInput
import kk.domoRolls.ru.presentation.components.SpicesSection
import kk.domoRolls.ru.presentation.components.isKeyboardVisible
import kk.domoRolls.ru.presentation.theme.DomoTheme

@Composable
fun CartScreen(
    viewModel: CartViewModel = hiltViewModel()
) {
    CartScreenUI(
        currentCart = viewModel.currentCart.collectAsState(),
        giftProduct = viewModel.giftProduct.collectAsState()
    )
}

@Composable
fun CartScreenUI(
    currentCart: State<List<MenuItem>> = mutableStateOf(emptyList()),
    giftProduct: State<MenuItem> = mutableStateOf(MenuItem())

) {
    val keyboardVisible = isKeyboardVisible()

    val animatedIconDp by animateDpAsState(
        targetValue = if (keyboardVisible) 220.dp else 0.dp,
        animationSpec = tween(durationMillis = 100), label = ""
    )
    Scaffold(
        topBar = { DomoToolbar() },
        bottomBar = {
            PromoInput(
                modifier = Modifier.padding(bottom = animatedIconDp)
            )
        },
        floatingActionButton = {}

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {


            currentCart.value.forEach {
                CartMenuItem(menuItem = it)
            }

            if (giftProduct.value.itemId?.isNotBlank() == true) {
                GiftProductItem(menuItem = giftProduct.value)
            }


            Devices()

            SpicesSection(currentCart.value)

        }
    }
}

@Preview(showBackground = true)
@Composable
fun CartScreenPreview() {
    DomoTheme {
        CartScreenUI()
    }
}
