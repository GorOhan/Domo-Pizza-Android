import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import kk.domoRolls.ru.data.model.order.Order
import kk.domoRolls.ru.domain.model.address.Address
import kk.domoRolls.ru.presentation.components.BaseButton
import kk.domoRolls.ru.presentation.components.BaseScreen
import kk.domoRolls.ru.presentation.components.DomoToolbar
import kk.domoRolls.ru.presentation.myorders.MyOrdersViewModel
import kk.domoRolls.ru.presentation.theme.DomoBlue
import kk.domoRolls.ru.presentation.theme.DomoBorder
import kk.domoRolls.ru.presentation.theme.DomoGray
import kk.domoRolls.ru.util.formatToOrderType


@Composable
fun MyOrdersScreen(
    navController: NavHostController,
    viewModel: MyOrdersViewModel = hiltViewModel()
) {
    val orders = viewModel.myOrders.collectAsState()

    BaseScreen(
        onBackClick = { navController.popBackStack() },
        baseViewModel = viewModel,
    ) {
        MyOrdersScreenUI(
            orders = orders.value,
            onEvent = {
                when (it) {
                    MyOrdersEvent.BackClick -> {
                        navController.popBackStack()
                    }
                    else -> {}
                }
            },
            findImages = { ids ->
                viewModel.getImagesUrls(ids)
            }
        )
    }
}

@Composable
fun MyOrdersScreenUI(
    orders: List<Order>,
    onEvent: (MyOrdersEvent) -> Unit = {},
    findImages: (List<String?>) -> List<String> = { _ -> emptyList() }
) {

    Scaffold(
        containerColor = Color.White,
        topBar = {
            DomoToolbar(
                title = "Мои заказы",
                onBackClick = {
                    onEvent(MyOrdersEvent.BackClick)
                }
            )
        },
        bottomBar = {},
        floatingActionButton = {}

    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            orders.forEach {
                val ids = it.orderItem?.items?.map { it.product?.id }
                OrderItem(
                    order = it,
                    images = findImages(ids ?: emptyList())
                )
            }
        }

    }
}

@Composable
fun OrderItem(
    order: Order,
    images: List<String?> = emptyList()
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .clip(RectangleShape)
            .border(
                width = 2.dp,
                shape = RoundedCornerShape(24.dp),
                color = DomoBorder
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "№1",
                    style = MaterialTheme.typography.bodySmall,
                    color = DomoGray
                )

                Text(
                    text = order.orderItem?.whenCreated?.formatToOrderType() ?: "",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Black
                )
            }

            Column {
                Text(
                    text = "Доставка",
                    style = MaterialTheme.typography.bodySmall,
                    color = DomoGray
                )

                Text(
                    text = order.orderItem?.deliveryPoint?.deliveryAddress?.street?.name.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Black
                )
            }
        }

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp),
            color = DomoBorder
        )

        LazyRow(
            modifier = Modifier
                .padding(22.dp)
                .fillMaxWidth()
        ) {
            itemsIndexed(images) { index, item ->
                Image(
                    contentScale = ContentScale.Crop,
                    painter = rememberAsyncImagePainter(item),
                    contentDescription = null,
                    modifier = Modifier
                        .width(80.dp)
                        .height(40.dp)
                )
            }


        }

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp),
            color = DomoBorder
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Сумма заказа",
                    style = MaterialTheme.typography.bodySmall,
                    color = DomoGray
                )

                Text(
                    text = "${order.orderItem?.sum.toString()} ₽",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Black
                )
            }
            BaseButton(
                buttonTitle = "Повторить заказ",
                backgroundColor = DomoBlue,
                modifier = Modifier
            )
        }

    }

}

sealed class MyOrdersEvent {
    data class UpdateAddress(val address: Address) : MyOrdersEvent()
    data class NavigateClick(val route: String) : MyOrdersEvent()
    data object BackClick : MyOrdersEvent()
    data object Nothing : MyOrdersEvent()
}