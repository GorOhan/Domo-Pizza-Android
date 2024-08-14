package kk.domoRolls.ru.presentation.orderstatus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kk.domoRolls.ru.R
import kk.domoRolls.ru.data.model.order.Order
import kk.domoRolls.ru.data.model.order.OrderStatus
import kk.domoRolls.ru.data.model.order.OrderStepCount
import kk.domoRolls.ru.data.model.order.orderStatusTitle
import kk.domoRolls.ru.domain.model.User
import kk.domoRolls.ru.presentation.components.BaseButton
import kk.domoRolls.ru.presentation.components.BaseScreen
import kk.domoRolls.ru.presentation.navigation.Screen
import kk.domoRolls.ru.presentation.theme.DomoBlue
import kk.domoRolls.ru.presentation.theme.DomoBorder
import kk.domoRolls.ru.presentation.theme.DomoGray
import kk.domoRolls.ru.presentation.theme.DomoRed
import kk.domoRolls.ru.presentation.theme.DomoTheme
import kk.domoRolls.ru.presentation.theme.InterFont

@Composable
fun OrderStatusScreen(
    orderId: String,
    navController: NavHostController,
    viewModel: OrderStatusViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.getOrderById(orderId)
    }
    BaseScreen(
        onBackClick = { navController.popBackStack() },
        baseViewModel = viewModel,
    ) {
        OrderStatusScreenUI(
            deliveryTime = viewModel.deliveryTime.collectAsState(),
            order = viewModel.order.collectAsState(),
            user = viewModel.user.collectAsState(),
            onEvent = {
                when (it) {
                    OrderStatusEvent.BackClick -> {
                        navController.popBackStack()
                    }

                    OrderStatusEvent.ToMainPage -> {
                        navController.navigate(Screen.MainScreen.route) {
                            popUpTo(navController.graph.id) {
                                inclusive = false
                            }
                        }
                    }

                    OrderStatusEvent.Nothing -> {}
                }
            }
        )
    }
}

@Composable
fun OrderStatusScreenUI(
    deliveryTime: State<String> = mutableStateOf(""),
    order: State<Order> = mutableStateOf(Order()),
    user: State<User> = mutableStateOf(User()),
    onEvent: (OrderStatusEvent) -> Unit = {},

    ) {

    val annotatedString = buildAnnotatedString {
        append("ваш")
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.secondary
            )
        ) {
            append(" заказ принят")
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 22.dp),
            text = "${user.value.name},",
            fontSize = 36.sp,
            fontFamily = InterFont
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 22.dp),
            text = annotatedString,
            fontSize = 36.sp,
            fontFamily = InterFont,
            textAlign = TextAlign.Start
        )

        Divider(
            color = DomoBorder,
            modifier = Modifier
                .fillMaxWidth()
                .width(2.dp)
                .padding(vertical = 20.dp),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 22.dp, end = 22.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = OrderStatus.entries.find { it.value == order.value.orderItem?.status }?.orderStatusTitle?:"",
                fontSize = 20.sp,
                fontFamily = InterFont
            )

            Text(
                text = deliveryTime.value,
                fontSize = 20.sp,
                fontFamily = InterFont
            )
        }

        FourEqualPartsRow( OrderStatus.entries.find { it.value == order.value.orderItem?.status }?:OrderStatus.CANCELLED)

        Row(
            modifier = Modifier
                .padding(start = 22.dp, top = 6.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                tint = DomoRed,
                painter = painterResource(id = R.drawable.ic_text_attention),
                contentDescription = ""
            )
            Text(
                modifier = Modifier.padding(start = 6.dp),
                color = DomoRed,
                style = MaterialTheme.typography.bodySmall,
                text = "Обращаем ваше внимание, что заказ может приехать ранее" +
                        "обозначенного времени, либо задержаться в связи с высокой" +
                        "нагрузкой или ситуацией на дорогах."
            )
        }

        Divider(
            color = DomoBorder,
            modifier = Modifier
                .fillMaxWidth()
                .width(2.dp)
                .padding(top = 20.dp),
        )
        Column(
            modifier = Modifier
                .padding(22.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(DomoBorder),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(top = 20.dp),
                text = "Оставить отзыв",
                fontSize = 20.sp,
                fontFamily = InterFont
            )

            ReviewStars(OrderStatus.ON_WAY)

        }

        BaseButton(
            buttonTitle = "Связаться с поддержкой",
            titleColor = Color.Black,
            backgroundColor = DomoBorder,
            modifier = Modifier
                .padding(horizontal = 22.dp)
                .fillMaxWidth()
        )

        Divider(
            color = DomoBorder,
            modifier = Modifier
                .fillMaxWidth()
                .width(1.dp)
                .padding(top = 20.dp),
        )

        Spacer(Modifier.weight(1f))

        BaseButton(
            buttonTitle = "Вернуться на главную",
            backgroundColor = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .padding(horizontal = 22.dp, vertical = 10.dp)
                .fillMaxWidth(),
            onClick = { onEvent(OrderStatusEvent.ToMainPage)}
        )
    }

}

@Composable
fun FourEqualPartsRow(
    orderStatus: OrderStatus
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        repeat(4) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Divider(
                    color = if (index < orderStatus.OrderStepCount) DomoBlue else DomoGray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                )
            }

        }
    }
}

@Composable
fun ReviewStars(
    orderStatus: OrderStatus
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(22.dp)
    ) {
        repeat(5) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(38.dp),
                    painter = painterResource(id = R.drawable.ic_star),
                    tint = if (index < orderStatus.OrderStepCount) DomoBlue else DomoGray,
                    contentDescription = ""

                )
            }

        }
    }
}


sealed class OrderStatusEvent {
    data object ToMainPage : OrderStatusEvent()
    data object BackClick : OrderStatusEvent()
    data object Nothing : OrderStatusEvent()
}

@Preview(showBackground = true)
@Composable
fun OTPScreenPreview() {
    DomoTheme {
        OrderStatusScreenUI()
    }
}

