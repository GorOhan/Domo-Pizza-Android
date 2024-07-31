package kk.domoRolls.ru.presentation.personaldata

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kk.domoRolls.ru.domain.model.User
import kk.domoRolls.ru.presentation.cart.Event
import kk.domoRolls.ru.presentation.components.DomoToolbar
import kk.domoRolls.ru.presentation.components.isKeyboardVisible
import kk.domoRolls.ru.presentation.theme.DomoBlue
import kk.domoRolls.ru.presentation.theme.DomoBorder
import kk.domoRolls.ru.presentation.theme.DomoGray
import kk.domoRolls.ru.presentation.theme.DomoTheme

@Composable
fun PersonalDataScreen(){
    PersonalDataUI()
}

@Composable
fun PersonalDataUI(
    user: State<User> = mutableStateOf(User()),
    onClick: (type: Event) -> Unit = { _ -> },
) {
    val keyboardVisible = isKeyboardVisible()

    val animatedIconDp by animateDpAsState(
        targetValue = if (keyboardVisible) 220.dp else 0.dp,
        animationSpec = tween(durationMillis = 200), label = ""
    )

    Scaffold(
        containerColor = Color.White,
        topBar = {
            DomoToolbar(
                title = "Личные данные",
                onBackClick = { onClick(Event.BackClick) }
            )
        },
        bottomBar = {},
        floatingActionButton = {}

    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PersonalDataItem(label = "Имя", value = "Регина", onValueChange = {})
            PersonalDataItem(label = "Телефон", value = "+7 999 999 99 99", onValueChange = {})
            PersonalDataItem(label = "Почта", value = "Почта", onValueChange = {})
            PersonalDataItem(label = "День рождения", value = "Введите дату", onValueChange = {})


        }
    }
}


@Preview(showBackground = true)
@Composable
fun PersonalDataPreview() {
    DomoTheme {
        PersonalDataUI()
    }
}

@Composable
fun PersonalDataItem(
    label: String,
    value: String,
    onValueChange: (String) -> Unit = {}
) {
    TextField(
        modifier = Modifier
            .padding(start = 22.dp, end = 22.dp, top = 10.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        label = { Text(label, color = DomoGray) },
        shape = RoundedCornerShape(24.dp),
        value = value,
        onValueChange = { onValueChange(it) },
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedContainerColor = DomoBorder,
            unfocusedContainerColor = DomoBorder,
            disabledContainerColor = DomoBorder,
            cursorColor = DomoBlue,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedPlaceholderColor = DomoGray,
            unfocusedPlaceholderColor = DomoGray,
        ),
        textStyle = MaterialTheme.typography.titleSmall,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        singleLine = true,
        placeholder = {
            Text(
                text = "+7 999 999 99 99",
                style = MaterialTheme.typography.titleSmall,
            )
        },

        )
}

