package kk.domoRolls.ru.presentation.cart

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kk.domoRolls.ru.R
import kk.domoRolls.ru.data.model.order.MenuItem
import kk.domoRolls.ru.domain.model.GiftProduct
import kk.domoRolls.ru.domain.model.PromoCode
import kk.domoRolls.ru.presentation.components.BaseScreen
import kk.domoRolls.ru.presentation.components.CartMenuItem
import kk.domoRolls.ru.presentation.components.Devices
import kk.domoRolls.ru.presentation.components.DomoToolbar
import kk.domoRolls.ru.presentation.components.GiftProductItem
import kk.domoRolls.ru.presentation.components.PromoInput
import kk.domoRolls.ru.presentation.components.SleepView
import kk.domoRolls.ru.presentation.components.SpicesSection
import kk.domoRolls.ru.presentation.components.isKeyboardVisible
import kk.domoRolls.ru.presentation.navigation.Screen
import kk.domoRolls.ru.presentation.theme.DomoGreen
import kk.domoRolls.ru.presentation.theme.DomoRed
import kk.domoRolls.ru.presentation.theme.DomoTheme

@Composable
fun CartScreen(
    navController: NavHostController,
    viewModel: CartViewModel = hiltViewModel()
) {
    val isWorkingTime = viewModel.isWorkingTime.collectAsState()
    val onEvent = viewModel.onEvent.collectAsState(initial = Event.Nothing)

    LaunchedEffect(onEvent.value) {
        when (onEvent.value) {
            Event.BackClick -> {
                navController.popBackStack()
            }

            Event.ConfirmOrder -> {
                if (isWorkingTime.value) navController.navigate(Screen.PayOrderScreen.route)
            }

            Event.ConfirmPromo, is Event.AddToCart, is Event.RemoveFromCart,
            is Event.InputPromo, Event.Nothing, Event.LogOut,
            is Event.NavigateClick, is Event.SetDeviceCount -> {
            }
        }
    }

    BaseScreen(
        onBackClick = { navController.popBackStack() },
        baseViewModel = viewModel,
    ) {
        CartScreenUI(
            deviceCount = viewModel.deviceCount.collectAsState(),
            usedPromoCode = viewModel.usedPromoCode.collectAsState(),
            isPromoSuccess = viewModel.promoCodeUseState.collectAsState(),
            inputPromo = viewModel.inputPromo.collectAsState(),
            gift = viewModel.gift.collectAsState(),
            currentCart = viewModel.currentCart.collectAsState(),
            giftProduct = viewModel.giftProduct.collectAsState(),
            spices = viewModel.spices.collectAsState(),
            onClick = { viewModel.setEvent(it) }
        )
    }
    if (onEvent.value is Event.ConfirmOrder && isWorkingTime.value.not()) {
        SleepView(
            seeMenuClick = { viewModel.setEvent(Event.BackClick)}
        )
    }
}

@Composable
fun CartScreenUI(
    deviceCount: State<Int> = mutableStateOf(0),
    usedPromoCode: State<PromoCode?> = mutableStateOf(null),
    isPromoSuccess: State<PromoCodeUseState> = mutableStateOf(PromoCodeUseState.NOTHING),
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


    if (isPromoSuccess.value != PromoCodeUseState.NOTHING){
        isPromoSuccess.value.let {
            Dialog(
                onDismissRequest = { }) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        modifier = Modifier.fillMaxWidth(.5f),
                        tint = if (it == PromoCodeUseState.SUCCESS) DomoGreen else DomoRed,
                        painter = painterResource(id = if ((it == PromoCodeUseState.SUCCESS)) R.drawable.ic_success else R.drawable.ic_error),
                        contentDescription = ""
                    )
                    Text(
                        modifier = Modifier.padding(top = 16.dp),
                        text = it.dialogMessage,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if ((it == PromoCodeUseState.SUCCESS)) DomoGreen else DomoRed,
                    )
                }
            }
        }
    }


    Scaffold(
        topBar = {
            DomoToolbar(
                onBackClick = { onClick(Event.BackClick) }
            )
        },
        bottomBar = {
            PromoInput(
                usedPromoCode = usedPromoCode.value,
                modifier = Modifier.padding(bottom = animatedIconDp),
                currentCart = currentCart.value,
                inputPromo = inputPromo.value,
                isPromoSuccess = isPromoSuccess.value,
                onEvent = onClick
            )
        },
        floatingActionButton = {}

    ) { innerPadding ->

        var cartPrice = currentCart.value.filter { menuItem -> menuItem.countInCart > 0 }
            .map { Pair(it.countInCart, it.itemSizes?.first()?.prices?.first()?.price ?: 0.0) }
            .sumOf { it.second * it.first }

        usedPromoCode.value?.let {
            cartPrice *= (1 - it.discount)
        }


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
            AnimatedVisibility((cartPrice > gift.value.sum)) {
                GiftProductItem(menuItem = giftProduct.value)
            }


            Devices(
                deviceCount = deviceCount.value,
                onEvent = { onClick(Event.SetDeviceCount(it)) }
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
    data class SetDeviceCount(val count: Int) : Event()

}

@Preview(showBackground = true)
@Composable
fun CartScreenPreview() {
    DomoTheme {
        CartScreenUI()
    }
}
