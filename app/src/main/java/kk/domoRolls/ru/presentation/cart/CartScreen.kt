package kk.domoRolls.ru.presentation.cart

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.navigation.NavHostController
import kk.domoRolls.ru.data.model.order.MenuItem
import kk.domoRolls.ru.domain.model.GiftProduct
import kk.domoRolls.ru.presentation.components.CartMenuItem
import kk.domoRolls.ru.presentation.components.Devices
import kk.domoRolls.ru.presentation.components.DomoToolbar
import kk.domoRolls.ru.presentation.components.GiftProductItem
import kk.domoRolls.ru.presentation.components.PromoInput
import kk.domoRolls.ru.presentation.components.SleepView
import kk.domoRolls.ru.presentation.components.SpicesSection
import kk.domoRolls.ru.presentation.components.isKeyboardVisible
import kk.domoRolls.ru.presentation.theme.DomoTheme
import kk.domoRolls.ru.util.isWorkingTime

@Composable
fun CartScreen(
    navController: NavHostController,
    viewModel: CartViewModel = hiltViewModel()
) {
    val isWorkingTime = viewModel.isWorkingTime.collectAsState()
    val onEvent = viewModel.onEvent.collectAsState()

    LaunchedEffect(onEvent.value) {
        when (val event = onEvent.value) {
            Event.ConfirmPromo -> {
                viewModel.confirmPromo()
            }

            Event.BackClick -> {
                navController.popBackStack()
            }

            is Event.AddToCart -> {
                viewModel.addToCart(event.item)
            }

            is Event.RemoveFromCart -> {
                viewModel.removeFromCart(event.item)
            }

            is Event.InputPromo -> {
                viewModel.inputPromo(event.input)
            }

            Event.ConfirmOrder -> {}
            Event.Nothing -> {}
            Event.LogOut -> {}
            is Event.NavigateClick -> {}
        }
    }

    CartScreenUI(
        isPromoSuccess = viewModel.isPromoSuccess.collectAsState(),
        inputPromo = viewModel.inputPromo.collectAsState(),
        gift = viewModel.gift.collectAsState(),
        currentCart = viewModel.currentCart.collectAsState(),
        giftProduct = viewModel.giftProduct.collectAsState(),
        spices = viewModel.spices.collectAsState(),
        onClick = { viewModel.setEvent(it)}
    )

    if (onEvent.value is Event.ConfirmOrder && isWorkingTime.value.not()){
        SleepView()
    }
}

@Composable
fun CartScreenUI(
    isWorkingTime: State<Boolean> = mutableStateOf(true),
    isPromoSuccess: State<Boolean?> = mutableStateOf(null),
    inputPromo: State<String> = mutableStateOf(""),
    gift: State<GiftProduct> = mutableStateOf(GiftProduct()),
    currentCart: State<List<MenuItem>> = mutableStateOf(emptyList()),
    giftProduct: State<MenuItem> = mutableStateOf(MenuItem()),
    spices: State<List<MenuItem>> = mutableStateOf(emptyList()),
    onClick: (type: Event) -> Unit = { _ -> },
) {
    val keyboardVisible = isKeyboardVisible()

    val animatedIconDp by animateDpAsState(
        targetValue = if (keyboardVisible) 220.dp else 0.dp,
        animationSpec = tween(durationMillis = 200), label = ""
    )
    Scaffold(
        topBar = {
            DomoToolbar(
                onBackClick = { onClick(Event.BackClick) }
            )
        },
        bottomBar = {
            PromoInput(
                modifier = Modifier.padding(bottom = animatedIconDp),
                currentCart = currentCart.value,
                inputPromo = inputPromo.value,
                isPromoSuccess = isPromoSuccess.value,
                onInputPromo = { onClick(Event.InputPromo(it)) },
                confirmPromo = { onClick(Event.ConfirmPromo) },
                onConfirmOrder = { onClick(Event.ConfirmOrder)}
            )
        },
        floatingActionButton = {}

    ) { innerPadding ->

        val cartPrice = currentCart.value.filter { menuItem -> menuItem.countInCart > 0 }
            .map { Pair(it.countInCart, it.itemSizes?.first()?.prices?.first()?.price ?: 0.0) }
            .sumOf { it.second * it.first }
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {


            currentCart.value.filter { !spices.value.contains(it) }.forEach {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(),  // You can customize the enter and exit animations
                    exit = fadeOut()
                ) {
                    CartMenuItem(
                        menuItem = it,
                        onMinusClick = { onClick(Event.RemoveFromCart(it)) },
                        onPlusClick = { onClick(Event.AddToCart(it)) }
                    )
                }
            }

            AnimatedVisibility (giftProduct.value.itemId?.isNotBlank() == true && (cartPrice > gift.value.sum)) {
                GiftProductItem(menuItem = giftProduct.value)
            }


            Devices(
                currentCount = currentCart.value.sumOf { it.countInCart },
            )

            SpicesSection(
                spices.value,
                onMinusClick = { onClick(Event.RemoveFromCart(it)) },
                onPlusClick = { onClick(Event.AddToCart(it)) }
            )

        }
    }
}

sealed class Event {
    data object Nothing : Event()
    data object BackClick : Event()
    data object ConfirmPromo : Event()
    data class InputPromo(val input: String) : Event()
    data class AddToCart(val item: MenuItem) : Event()
    data class RemoveFromCart(val item: MenuItem) : Event()
    data class NavigateClick(val route: String) : Event()
    data object LogOut : Event()
    data object ConfirmOrder : Event()

}

@Preview(showBackground = true)
@Composable
fun CartScreenPreview() {
    DomoTheme {
        CartScreenUI()
    }
}
