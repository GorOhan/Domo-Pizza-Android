package kk.domoRolls.ru.presentation.payorder

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kk.domoRolls.ru.R
import kk.domoRolls.ru.presentation.components.BaseButton
import kk.domoRolls.ru.presentation.components.DomoToolbar
import kk.domoRolls.ru.presentation.navigation.Screen
import kk.domoRolls.ru.presentation.theme.DomoBlue
import kk.domoRolls.ru.presentation.theme.DomoBorder
import kk.domoRolls.ru.presentation.theme.DomoGray
import kk.domoRolls.ru.presentation.theme.DomoRed
import ru.tinkoff.acquiring.sdk.AcquiringSdk
import ru.tinkoff.acquiring.sdk.models.enums.CheckType
import ru.tinkoff.acquiring.sdk.models.options.screen.PaymentOptions
import ru.tinkoff.acquiring.sdk.redesign.mainform.MainFormLauncher
import ru.tinkoff.acquiring.sdk.utils.Money
import java.util.UUID


@Composable
fun PayOrderScreen(
    navController: NavHostController,
    payOrderViewModel: PayOrderViewModel = hiltViewModel()
) {

    val enableToPay = payOrderViewModel.enableToPay.collectAsState()

    AcquiringSdk.isDebug = true
    AcquiringSdk.logger = TinkoffLogger {
        payOrderViewModel.onLog(it)
    }

    val byMainFormPayment = rememberLauncherForActivityResult(MainFormLauncher.Contract,

        onResult = { result ->
            when (result) {
                is MainFormLauncher.Success -> {
                    payOrderViewModel.setPaymentAlreadyConfirmed()
                }

                is MainFormLauncher.Error -> {
                    //todo show error
                }

                MainFormLauncher.Canceled -> {

                }
            }

        })


    val paymentOptions =
        PaymentOptions().setOptions {
            setTerminalParams(
                "1713117993164",
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAv5yse9ka3ZQE0feuGtemYv3IqOlLck8zHUM7lTr0za6lXTszRSXfUO7jMb+L5C7e2QNFs+7sIX2OQJ6a+HG8kr+jwJ4tS3cVsWtd9NXpsU40PE4MeNr5RqiNXjcDxA+L4OsEm/BlyFOEOh2epGyYUd5/iO3OiQFRNicomT2saQYAeqIwuELPs1XpLk9HLx5qPbm8fRrQhjeUD5TLO8b+4yCnObe8vy/BMUwBfq+ieWADIjwWCMp2KTpMGLz48qnaD9kdrYJ0iyHqzb2mkDhdIzkim24A3lWoYitJCBrrB2xM05sm9+OdCI1f7nPNJbl5URHobSwR94IRGT7CJcUjvwIDAQAB"
            )

            orderOptions {                          // данные заказа
                orderId = UUID.randomUUID().toString()              // ID заказа в вашей системе
                amount = Money.ofRubles(1)       // сумма для оплаты
                // URL, куда будет переведен покупатель в случае неуспешной оплаты (см. полную документацию)
            }
            customerOptions {                       // данные покупателя
                checkType = CheckType.NO.toString() // тип привязки карты
                customerKey =
                    UUID.randomUUID()
                        .toString()      // уникальный ID пользователя для сохранения данных его карты
                email =
                    "ohangor@gmal.com"          // E-mail клиента для отправки уведомления об оплате
            }

        }

    val payment = MainFormLauncher.StartData(paymentOptions)

    val cartPrice = payOrderViewModel.cartPrice.collectAsState()
    val count = payOrderViewModel.cartCount.collectAsState()
    val discount = payOrderViewModel.discount.collectAsState()
    val defaultAddress = payOrderViewModel.defaultAddress.collectAsState()
    val deliveryTime = payOrderViewModel.deliveryTime.collectAsState()
    val comment = payOrderViewModel.comment.collectAsState()

    Scaffold(
        containerColor = Color.White,
        topBar = {
            DomoToolbar(
                title = "Доставка",
                onBackClick = { navController.popBackStack() }
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
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 22.dp, top = 12.dp),
                text = "Ваш адрес",
                style = MaterialTheme.typography.bodyLarge,
            )

            Row(
                modifier = Modifier

                    .padding(horizontal = 22.dp, vertical = 16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(DomoBorder)
                    .clickable(
                    ) {
                        navController.navigate(Screen.MyAddressesScreen.route)

                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically

            ) {
                Column {
                    Text(
                        modifier = Modifier
                            .padding(start = 20.dp, top = 20.dp),
                        text = defaultAddress.value.street,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Row(
                        modifier = Modifier.padding(start = 20.dp, bottom = 20.dp, top = 4.dp),
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
                            text = if (defaultAddress.value.type.lowercase() != "самовывоз") "Для самовывоза выберите адрес ресторана" else "самовывоз"
                        )
                    }
                }

                Icon(
                    modifier = Modifier
                        .padding(end = 20.dp)
                        .size(18.dp),
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = ""
                )
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 22.dp, top = 12.dp),
                text = "Время доставки",
                style = MaterialTheme.typography.bodyLarge,
            )

            Row(
                modifier = Modifier
                    .padding(horizontal = 22.dp)
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 20.dp, start = 4.dp, end = 4.dp)
                        .background(DomoBorder, RoundedCornerShape(20.dp))
                        .weight(0.5f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            modifier = Modifier.size(18.dp),
                            painter = painterResource(id = R.drawable.ic_delivery),
                            contentDescription = ""
                        )
                        Text(
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 10.dp),
                            text = "${deliveryTime.value} мин"
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .padding(top = 20.dp, start = 4.dp, end = 4.dp)
                        .background(DomoBorder, RoundedCornerShape(20.dp))
                        .weight(0.5f)
                ) {
                    Text(
                        color = DomoGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        text = "Ко времени"
                    )
                }

            }

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 22.dp, top = 12.dp),
                text = "Комментарий",
                style = MaterialTheme.typography.bodyLarge,
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                shape = RoundedCornerShape(24.dp),
                minLines = 5,
                value = comment.value,
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = DomoBlue,
                    unfocusedBorderColor = DomoGray,
                    focusedBorderColor = DomoBlue,
                    unfocusedTextColor = DomoGray,
                    focusedTextColor = Color.Black,
                    unfocusedPlaceholderColor = DomoGray,
                    focusedPlaceholderColor = DomoGray,
                ),
                onValueChange = { payOrderViewModel.inputComment(it) },
                placeholder = {
                    Text(
                        text = "Например: Не звоните в домофон," + "дома спит ребенок.",
                        style = MaterialTheme.typography.titleSmall,
                        lineHeight = 16.sp
                    )
                },
            )
            Spacer(modifier = Modifier.weight(1f))

            Divider(
                modifier = Modifier
                    .padding(start = 22.dp, end = 22.dp, top = 60.dp)
                    .fillMaxWidth()
                    .width(1.dp),
                color = DomoBorder
            )
            Column {
                Row(
                    modifier = Modifier
                        .padding(start = 22.dp, end = 22.dp, top = 24.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "${count.value} товаров")
                    Text(text = "${cartPrice.value.toInt()} ₽")
                }

                Row(
                    modifier = Modifier
                        .padding(start = 22.dp, end = 22.dp, top = 10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Доставка")
                    Text(text = "0 ₽")
                }

                Row(
                    modifier = Modifier
                        .padding(horizontal = 22.dp, vertical = 10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Скидка")
                    Text(text = "${discount.value} ₽")
                }
            }

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .width(1.dp),
                color = DomoBorder
            )
            BaseButton(
                buttonTitle = "Оплатить",
                backgroundColor = DomoBlue,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 22.dp),
                onClick = {
                    if (enableToPay.value) {
                       // byMainFormPayment.launch(payment)
                        payOrderViewModel.sendOrderToIIKO()
                    } else {

                    }
                }
            )
        }

    }
}