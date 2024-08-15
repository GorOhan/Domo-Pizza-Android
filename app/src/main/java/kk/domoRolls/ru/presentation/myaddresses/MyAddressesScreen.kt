package kk.domoRolls.ru.presentation.myaddresses

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kk.domoRolls.ru.R
import kk.domoRolls.ru.domain.model.address.Address
import kk.domoRolls.ru.presentation.components.BaseScreen
import kk.domoRolls.ru.presentation.components.DomoToolbar
import kk.domoRolls.ru.presentation.navigation.Screen
import kk.domoRolls.ru.presentation.theme.DomoBlue
import kk.domoRolls.ru.presentation.theme.DomoBorder
import kk.domoRolls.ru.presentation.theme.DomoGray


@Composable
fun MyAddressesScreen(
    navController: NavHostController,
    viewModel: MyAddressViewModel = hiltViewModel()
) {

    val screenEvent = viewModel.event.collectAsState(MyAddressesEvent.Nothing)

    LaunchedEffect(screenEvent.value) {
        when (val event = screenEvent.value) {
            MyAddressesEvent.BackClick -> {
                navController.popBackStack()
            }

            MyAddressesEvent.Nothing -> {}
            is MyAddressesEvent.NavigateClick -> {
                navController.navigate(event.route)
            }

            is MyAddressesEvent.UpdateAddress -> {}
        }
    }

    BaseScreen(
        onBackClick = { navController.popBackStack() },
        baseViewModel = viewModel,
    ) {
        MyAddressesScreenUI(
            address = viewModel.myAddresses.collectAsState(),
            onEvent = { viewModel.setEvent(it) }
        )
    }
}

@Composable
fun MyAddressesScreenUI(
    address: State<List<Address>> = mutableStateOf(emptyList()),
    onEvent: (type: MyAddressesEvent) -> Unit = { _ -> },
) {

    Scaffold(
        containerColor = Color.White,
        topBar = {
            DomoToolbar(
                title = "Мои адреса",
                onBackClick = { onEvent(MyAddressesEvent.BackClick) }
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

            address.value.forEach {
                AddressItem(
                    address = it,
                    onEditClick = {
                        onEvent(MyAddressesEvent.NavigateClick("${Screen.AddressMapScreen.route}/${it.id.ifEmpty { null }}"))
                    },
                    onCheck = {
                        onEvent(MyAddressesEvent.UpdateAddress(it))
                    }
                )
            }

            Column(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth()
                    .clickable {
                        onEvent(MyAddressesEvent.NavigateClick("${Screen.AddressMapScreen.route}/${null}"))
                    },
            ) {
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Добавить адрес",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Icon(
                        tint = DomoBlue,
                        painter = painterResource(id = R.drawable.ic_plus),
                        contentDescription = ""
                    )

                }
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp),
                    color = DomoBorder
                )
            }

        }
    }
}

@Composable
fun AddressItem(
    address: Address,
    showEdit: Boolean = true,
    onEditClick: () -> Unit = {},
    onCheck: (Address) -> Unit = {},
) {
    var isChecked = address.default

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .background(DomoBorder, RoundedCornerShape(20.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Checkbox(
            modifier = Modifier.padding(start = 4.dp),
            checked = isChecked,
            onCheckedChange = {
                onCheck(address.copy(default = it))
                isChecked = it
            },
            colors = CheckboxDefaults.colors(
                checkedColor = DomoBlue,
                uncheckedColor = Color.Gray,
                checkmarkColor = Color.White
            ),
        )
        Column(
            modifier = Modifier.fillMaxWidth(0.8f),

            ) {
            Text(
                modifier = Modifier.padding(top = 20.dp),
                text = address.street,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start
            )
            Text(
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp),
                text = "Не звоните в домофон, спит ребёнок",
                style = MaterialTheme.typography.bodySmall,
                color = DomoGray,
                textAlign = TextAlign.Start
            )

        }


        if (showEdit) {
            Icon(
                tint = DomoGray,
                modifier = Modifier
                    .padding(end = 20.dp)
                    .clickable { onEditClick() },
                painter = painterResource(id = R.drawable.ic_edit),
                contentDescription = ""
            )
        } else {
            Box(modifier = Modifier.size(20.dp))
        }
    }

}

sealed class MyAddressesEvent {
    data class UpdateAddress(val address: Address) : MyAddressesEvent()
    data class NavigateClick(val route: String) : MyAddressesEvent()
    data object BackClick : MyAddressesEvent()
    data object Nothing : MyAddressesEvent()
}