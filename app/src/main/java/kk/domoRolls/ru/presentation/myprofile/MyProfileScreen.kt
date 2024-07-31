package kk.domoRolls.ru.presentation.myprofile

import android.widget.Switch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kk.domoRolls.ru.R
import kk.domoRolls.ru.domain.model.PromoCode
import kk.domoRolls.ru.domain.model.User
import kk.domoRolls.ru.presentation.components.BaseButton
import kk.domoRolls.ru.presentation.components.DomoToolbar
import kk.domoRolls.ru.presentation.html.HTMLScreenType
import kk.domoRolls.ru.presentation.navigation.Screen
import kk.domoRolls.ru.presentation.theme.DomoBlue
import kk.domoRolls.ru.presentation.theme.DomoBorder
import kk.domoRolls.ru.presentation.theme.DomoGray
import kk.domoRolls.ru.presentation.theme.DomoRed
import kk.domoRolls.ru.presentation.theme.DomoTheme
import kk.domoRolls.ru.util.copyTextToClipboard
import kk.domoRolls.ru.util.formatNumber

@Composable
fun MyProfileScreen(
    navController: NavHostController,
    viewModel: MyProfileViewModel = hiltViewModel(),
) {

    MyProfileScreenUI(
        promoCode = viewModel.promoCodes.collectAsState(),
        user = viewModel.user.collectAsState(),
        onClick = { event ->
            when (event) {
                Event.BackClick -> {
                    navController.navigateUp()
                }

                is Event.NavigateClick -> {
                    navController.navigate(event.route)
                }

                Event.LogOut -> {
                    viewModel.logOut()
                    navController.navigate(Screen.RegistrationScreen.route) {
                        popUpTo(navController.graph.id) {
                            inclusive = false
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun MyProfileScreenUI(
    promoCode: State<List<PromoCode>> = mutableStateOf(emptyList()),
    user: State<User> = mutableStateOf(User()),
    onClick: (type: Event) -> Unit = { _ -> }
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
            .background(Color.White)
    ) {
        DomoToolbar(
            onBackClick = { onClick(Event.BackClick) },
            title = "Мой профиль"
        )
        MyInfo(user = user,
            onPersonalDataClick = { onClick(Event.NavigateClick(Screen.PersonalDataScreen.route)) }
        )
        MyOrdersAndAddresses()
        MyGiftsView(
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 20.dp),
            promoCodes = promoCode.value
        )

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .width(2.dp)
                .padding(horizontal = 22.dp),
            color = DomoBorder
        )
        ProfileInfoItem(
            title = "Пуш-уведомления",
            showArrowIcon = false,
            showSwitch = true
        )
        ProfileInfoItem(
            title = "Пользовательское соглашение",
            onClick = {
                onClick(Event.NavigateClick("${Screen.HTMLScreen.route}/${HTMLScreenType.TERMS.name}"))
            }
        )
        ProfileInfoItem(
            title = "Оферта",
            onClick = {
                onClick(Event.NavigateClick("${Screen.HTMLScreen.route}/${HTMLScreenType.OFFER.name}"))

            }
        )
        ProfileInfoItem(
            onClick = { onClick(Event.LogOut) },
            title = "Выйти из профиля",
            titleColor = DomoRed,
            showArrowIcon = false,
        )
        ProfileInfoItem(
            title = "Версия 01.1.1",
            titleColor = DomoGray,
            showArrowIcon = false,
        )

        BaseButton(
            buttonTitle = "Связаться с поддержкой",
            titleColor = Color.Black,
            backgroundColor = DomoBorder,
            modifier = Modifier
                .padding(horizontal = 22.dp, vertical = 20.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
fun MyGiftsView(
    modifier: Modifier = Modifier,
    promoCodes: List<PromoCode> = emptyList(),
) {
    val context = LocalContext.current
    if (promoCodes.isNotEmpty()) {
        Column {
            Text(
                modifier = Modifier.padding(start = 22.dp, top = 20.dp),
                text = "Мои акции"
            )

            promoCodes.forEach { promoCode ->
                Box(
                    modifier = modifier
                        .fillMaxWidth()
                        .height(146.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(DomoBorder),
                ) {
                    Column(
                        modifier = Modifier
                            .padding(start = 20.dp)
                            .align(Alignment.CenterStart)
                    ) {
                        Text(
                            style = MaterialTheme.typography.bodyMedium,
                            text = "Скидка ${(promoCode.discount * 100).toInt()}%"
                        )
                        Text(
                            style = MaterialTheme.typography.bodyMedium,
                            text = "при заказе от ${promoCode.minPrice.toInt()} ₽"
                        )
                        Text(
                            modifier = Modifier.padding(top = 10.dp),
                            style = MaterialTheme.typography.bodySmall,
                            text = "действует до ",
                            color = Color.Red
                        )

                        Text(
                            modifier = Modifier.padding(top = 18.dp),
                            fontSize = 8.sp,
                            style = MaterialTheme.typography.bodySmall,
                            text = "по промокоду",
                            color = DomoGray
                        )
                        Row(
                            modifier = Modifier.padding(top = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                style = MaterialTheme.typography.bodyMedium,
                                text = promoCode.value
                            )
                            Icon(
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .size(16.dp)
                                    .clickable {
                                        copyTextToClipboard(
                                            context,
                                            promoCode.value
                                        )
                                    },
                                painter = painterResource(id = R.drawable.ic_copy),
                                tint = DomoGray,
                                contentDescription = ""
                            )
                        }

                    }
                    Image(
                        contentScale = ContentScale.FillHeight,
                        modifier = Modifier
                            .fillMaxHeight()
                            .align(Alignment.CenterEnd),
                        painter = painterResource(id = R.drawable.mygifts),
                        contentDescription = "",
                    )
                }
            }
        }
    }
}

@Composable
fun MyInfo(
    user: State<User>,
    onPersonalDataClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 22.dp, end = 22.dp, top = 25.dp)
            .background(DomoBorder, RoundedCornerShape(20.dp))
            .clickable { onPersonalDataClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .padding(all = 20.dp)
                .size(32.dp),
            painter = painterResource(id = R.drawable.ic_profile),
            contentDescription = ""
        )

        Column {
            Text(
                style = MaterialTheme.typography.bodyMedium,
                text = "Личные данные"
            )

            Text(
                modifier = Modifier.padding(top = 4.dp),
                color = Color.Black,
                style = MaterialTheme.typography.bodySmall,
                text = "${user.value.name} | ${formatNumber(user.value.phone, "+7 ### ### ## ##")}",
            )
        }
    }
}


@Composable
fun MyOrdersAndAddresses() {

    val buttonInteractionSource = remember { MutableInteractionSource() }
    val buttonPressed by buttonInteractionSource.collectIsPressedAsState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 22.dp, end = 22.dp, top = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween

    ) {
        Column(
            modifier = Modifier
                .clickable(
                    interactionSource = buttonInteractionSource,
                    indication = null,
                ) {

                }
                .padding(end = 8.dp)
                .fillMaxWidth(0.5f)
                .background(DomoBorder, RoundedCornerShape(20.dp)),
        ) {
            Text(
                modifier = Modifier.padding(start = 20.dp, top = 20.dp),
                style = MaterialTheme.typography.bodyMedium,
                text = "Мои заказы"
            )

            Text(
                modifier = Modifier.padding(start = 20.dp, bottom = 20.dp, top = 6.dp),
                style = MaterialTheme.typography.bodyMedium,
                text = "2 заказа"
            )
        }

        Column(
            modifier = Modifier
                .padding(start = 8.dp)
                .fillMaxWidth()
                .background(DomoBorder, RoundedCornerShape(20.dp)),
        ) {
            Text(
                modifier = Modifier.padding(start = 20.dp, top = 20.dp),
                style = MaterialTheme.typography.bodyMedium,
                text = "Мои адреса"
            )

            Text(
                modifier = Modifier.padding(start = 20.dp, bottom = 20.dp, top = 6.dp),
                style = MaterialTheme.typography.bodyMedium,
                text = "4 адреса"
            )
        }
    }
}

@Composable
fun ProfileInfoItem(
    title: String = "Пуш-уведомления",
    titleColor: Color = Color.Black,
    showArrowIcon: Boolean = true,
    showSwitch: Boolean = false,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .padding(horizontal = 22.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 28.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                color = titleColor,
                style = MaterialTheme.typography.titleSmall,
                text = title,
            )
            if (showArrowIcon) {
                Icon(
                    modifier = Modifier.size(12.dp),
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = "",
                )
            }

            if (showSwitch) {
                var checked by remember { mutableStateOf(true) }

                Switch(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = DomoBlue,
                        uncheckedThumbColor = DomoBlue,
                        uncheckedTrackColor = Color.White,
                    )
                )
            }
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .width(10.dp),
            color = DomoBorder
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MyProfileScreenPreview() {
    DomoTheme {
        MyProfileScreenUI()
    }
}

sealed class Event {
    data object BackClick : Event()
    data class NavigateClick(val route: String) : Event()
    data object LogOut : Event()

}