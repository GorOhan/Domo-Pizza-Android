package kk.domoRolls.ru.presentation.registration

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kk.domoRolls.ru.data.model.order.MenuItem
import kk.domoRolls.ru.presentation.components.BaseButton
import kk.domoRolls.ru.presentation.navigation.Screen
import kk.domoRolls.ru.presentation.theme.DomoBlue
import kk.domoRolls.ru.presentation.theme.DomoGray
import kk.domoRolls.ru.presentation.theme.DomoTheme
import kk.domoRolls.ru.presentation.theme.InterFont
import kk.domoRolls.ru.util.MaskVisualTransformation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun RegistrationScreen(
    navController: NavHostController,
    viewModel: RegistrationViewModel,
) {

    LaunchedEffect(true) {
        viewModel.getToken()
    }

    RegistrationScreenUI(
        onGetSmsClick = {
            viewModel.sendOTP()
            navController.navigate(Screen.OTPScreen.route)
        },
        getSmsEnableState = viewModel.isReadyToSendOtp,
        userNameState = viewModel.userName,
        userPhoneState = viewModel.phoneNumber,
        onUserNameInput = { viewModel.onUserNameInput(it) },
        onUserPhoneInput = { viewModel.onUserPhoneInput(it) },
        onTermsNav = { navController.navigate(Screen.HTMLScreen.route) }
    )
}

@Composable
fun RegistrationScreenUI(
    onGetSmsClick: () -> Unit = {},
    getSmsEnableState: StateFlow<Boolean> = MutableStateFlow(false),
    userNameState: StateFlow<String> = MutableStateFlow(""),
    userPhoneState: StateFlow<String> = MutableStateFlow(""),
    onUserNameInput: (String) -> Unit = {},
    onUserPhoneInput: (String) -> Unit = {},
    onTermsNav: () -> Unit = {},
) {
    val userName by userNameState.collectAsState()
    val phone by userPhoneState.collectAsState()
    val getSmsEnable by getSmsEnableState.collectAsState()

    Column(
        modifier = Modifier
            .background(color = Color.White)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.padding(top = 70.dp),
            text = "Регистрация",
            fontSize = 20.sp,
            fontFamily = InterFont
        )

        val annotatedString = buildAnnotatedString {
            append("Чувствуйте")
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.secondary
                )
            ) {
                append(
                    " себя\nкак"
                )
            }
            append(" Домо")
        }

        Text(
            modifier = Modifier.padding(top = 60.dp),
            text = annotatedString,
            fontSize = 36.sp,
            lineHeight = 42.sp,
            fontFamily = InterFont
        )

        OutlinedTextField(
            modifier = Modifier
                .padding(start = 22.dp, end = 22.dp, top = 30.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            value = userName,
            singleLine = true,
            onValueChange = { value -> onUserNameInput(value) },
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = DomoBlue,
                unfocusedBorderColor = DomoGray,
                focusedBorderColor = DomoBlue,
                unfocusedTextColor = DomoGray,
                focusedTextColor = Color.Black,
                unfocusedPlaceholderColor = DomoGray,
                focusedPlaceholderColor = DomoGray,
            ),
            textStyle = MaterialTheme.typography.titleSmall,
            placeholder = { Text(text = "Ваше имя", style = MaterialTheme.typography.titleSmall) },
        )

        OutlinedTextField(
            modifier = Modifier
                .padding(start = 22.dp, end = 22.dp, top = 10.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            value = phone,
            onValueChange = { onUserPhoneInput(it) },
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = DomoBlue,
                unfocusedBorderColor = DomoGray,
                focusedBorderColor = DomoBlue,
                unfocusedTextColor = DomoGray,
                unfocusedPlaceholderColor = DomoGray,
                focusedTextColor = Color.Black,
                focusedPlaceholderColor = DomoGray,
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
            visualTransformation = MaskVisualTransformation("+7 ### ### ## ##")

        )

        BaseButton(
            buttonTitle = "Получить смс-код ",
            backgroundColor = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .padding(horizontal = 22.dp, vertical = 10.dp)
                .fillMaxWidth(),
            onClick = {
                onGetSmsClick.invoke()
            },
            enable = getSmsEnable
        )

        Text(
            modifier = Modifier
                .padding(start = 22.dp, top = 20.dp)
                .fillMaxWidth(),
            text = "Продолжая, вы соглашаетесь",
            textAlign = TextAlign.Left,
            fontSize = 14.sp,
            fontFamily = InterFont,
        )

        Text(
            modifier = Modifier
                .clickable { onTermsNav() }
                .padding(horizontal = 22.dp)
                .fillMaxWidth(),
            text = "со сбором и обработкой персональных данных и пользовательским соглашением",
            textAlign = TextAlign.Left,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.secondary,
            lineHeight = 16.sp,
            fontFamily = InterFont,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationPreview() {
    DomoTheme {
        RegistrationScreenUI()
    }
}

fun LazyListScope.gridItems(
    count: Int,
    nColumns: Int,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    itemContent: @Composable BoxScope.(Int) -> Unit,
) {
    gridItems(
        data = List(count) { it },
        nColumns = nColumns,
        horizontalArrangement = horizontalArrangement,
        itemContent = itemContent,
    )
}

fun <T> LazyListScope.gridItems(
    data: List<T>,
    nColumns: Int,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    key: ((item: T) -> Any)? = null,
    itemContent: @Composable BoxScope.(T) -> Unit,
) {
    val rows = if (data.isEmpty()) 0 else 1 + (data.count() - 1) / nColumns
    items(rows) { rowIndex ->
        Row(horizontalArrangement = horizontalArrangement) {
            for (columnIndex in 0 until nColumns) {
                val itemIndex = rowIndex * nColumns + columnIndex
                if (itemIndex < data.count()) {
                    val item = data[itemIndex]
                    androidx.compose.runtime.key(key?.invoke(item)) {
                        Box(
                            modifier = Modifier.weight(1f, fill = true),
                            propagateMinConstraints = true
                        ) {
                            itemContent.invoke(this, item)
                        }
                    }
                } else {
                    Spacer(Modifier.weight(1f, fill = true))
                }
            }
        }
    }
}

@Composable
fun TestContainer(
    menu:List<MenuItem>,
){
    LazyColumn {
        item {
            Text(text = "My LazyColumn Title")
        }
        // with coun
        // or with list of items
        gridItems(menu, nColumns = 2) { item ->
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .padding(5.dp)
                    .background(Color.Gray)
            )
        }
    }
}

