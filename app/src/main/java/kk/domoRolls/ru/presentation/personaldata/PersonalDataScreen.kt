@file:Suppress("NAME_SHADOWING")

package kk.domoRolls.ru.presentation.personaldata

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kk.domoRolls.ru.presentation.components.BaseButton
import kk.domoRolls.ru.presentation.components.DomoToolbar
import kk.domoRolls.ru.presentation.theme.DomoBlue
import kk.domoRolls.ru.presentation.theme.DomoBorder
import kk.domoRolls.ru.presentation.theme.DomoGray
import kk.domoRolls.ru.presentation.theme.DomoRed
import kk.domoRolls.ru.presentation.theme.DomoTheme
import kk.domoRolls.ru.util.MaskVisualTransformation
import kk.domoRolls.ru.util.formatToScreenType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalDataScreen(
    navController: NavHostController,
    personalDataViewModel: PersonalDataViewModel = hiltViewModel()
) {
    val event = personalDataViewModel.onEvent.collectAsState()
    val dateState = rememberDatePickerState()

    LaunchedEffect(event.value) {
        when (val event = event.value) {
            is PersonalDataEvent.OnNavigateClick -> navController.navigate(event.route)
            PersonalDataEvent.BackClick -> navController.popBackStack()
            PersonalDataEvent.ConfirmChanges,
            PersonalDataEvent.DeleteAccount,
            is PersonalDataEvent.InputEmail,
            is PersonalDataEvent.InputName,
            is PersonalDataEvent.InputPhone,
            PersonalDataEvent.Nothing -> {
            }

            is PersonalDataEvent.ConfirmBirthDay -> {

            }
        }


    }
    PersonalDataUI(
        currentDate = personalDataViewModel.currentDate.collectAsState(),
        dateState = dateState,
        confirmButtonEnable = personalDataViewModel.confirmButtonEnable.collectAsState(initial = false),
        userNameInput = personalDataViewModel.inputUserName.collectAsState(),
        userEmailInput = personalDataViewModel.inputUserEmail.collectAsState(),
        userPhoneInput = personalDataViewModel.inputUserPhone.collectAsState(),
        onEvent = { personalDataViewModel.setEvent(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalDataUI(
    currentDate: State<String> = mutableStateOf(""),
    dateState: DatePickerState = rememberDatePickerState(),
    confirmButtonEnable: State<Boolean> = mutableStateOf(false),
    userEmailInput: State<String> = mutableStateOf(""),
    userPhoneInput: State<String> = mutableStateOf(""),
    userNameInput: State<String> = mutableStateOf(""),
    onEvent: (type: PersonalDataEvent) -> Unit = { _ -> },
) {

    val showDatePicker = remember { mutableStateOf(false) }


    Scaffold(
        containerColor = Color.White,
        topBar = {
            DomoToolbar(
                title = "Личные данные",
                onBackClick = { onEvent(PersonalDataEvent.BackClick) }
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
            PersonalDataItem(
                label = "Имя",
                value = userNameInput.value,
                placeHolder = "user name",
                onValueChange = { onEvent(PersonalDataEvent.InputName(it)) })

            PersonalDataPhoneItem(
                label = "Телефон",
                value = userPhoneInput.value,
                onValueChange = { onEvent(PersonalDataEvent.InputPhone(it)) },
            )

            PersonalDataItem(
                label = "Почта",
                placeHolder = "example@mail.ru",
                value = userEmailInput.value,
                onValueChange = { onEvent(PersonalDataEvent.InputEmail(it)) })

            PersonalDataDateItem(
                label = "День рождения",
                value = if (currentDate.value.isEmpty()) "Введите дату" else currentDate.value.formatToScreenType(),
                onClick = { showDatePicker.value = true }
            )

            Spacer(Modifier.weight(1f))
            BaseButton(
                onClick = { onEvent(PersonalDataEvent.ConfirmChanges) },
                buttonTitle = "Сохранить",
                backgroundColor = DomoBlue,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 16.dp),
                enable = confirmButtonEnable.value,
            )

            BaseButton(
                onClick = { onEvent(PersonalDataEvent.DeleteAccount) },
                buttonTitle = "Удалить профиль",
                backgroundColor = DomoRed,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 22.dp, end = 22.dp, bottom = 32.dp)
            )
        }
        AnimatedVisibility(visible = showDatePicker.value) {
            DatePickerDialog(
                onDismissRequest = {
                    showDatePicker.value = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDatePicker.value = false
                            dateState.selectedDateMillis?.let {
                                onEvent(PersonalDataEvent.ConfirmBirthDay(it))
                            }
                        }
                    ) {
                        Text("Применить")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDatePicker.value = false
                        }
                    ) {
                        Text("Отмена")
                    }
                }
            ) {
                DatePicker(
                    modifier = Modifier.background(DomoBorder),
                    state = dateState
                )
            }

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun PersonalDataPreview() {
    DomoTheme {
        PersonalDataUI()
    }
}

@Composable
fun PersonalDataItem(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    placeHolder: String,
    readOnly: Boolean = false,
    onValueChange: (String) -> Unit = {},
    onClick: () -> Unit = {},
    onDone:() -> Unit = {}
) {

    TextField(
        modifier = modifier
            .padding(start = 22.dp, end = 22.dp, top = 10.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        label = { Text(label, color = DomoGray) },
        shape = RoundedCornerShape(24.dp),
        value = value,
        readOnly = readOnly,
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
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
        keyboardActions = KeyboardActions(
            onDone = {
                onDone()
            }
        ),
        singleLine = true,
        placeholder = {
            Text(
                text = placeHolder,
                style = MaterialTheme.typography.titleSmall,
            )
        },
    )
}

@Composable
fun PersonalDataPhoneItem(
    label: String,
    value: String,
    onValueChange: (String) -> Unit = {},
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
        readOnly = true,
        singleLine = true,
        placeholder = {
            Text(
                text = "+7 999 999 99 99",
                style = MaterialTheme.typography.titleSmall,
            )
        },
        visualTransformation = MaskVisualTransformation("+7 ### ### ## ##")
    )
}

@Composable
fun PersonalDataDateItem(
    label: String,
    value: String,
    onClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .padding(start = 22.dp, end = 22.dp, top = 10.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(DomoBorder, RoundedCornerShape(24.dp))
            .clickable { onClick() })
    {
        Text(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp),
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = DomoGray
        )

        Text(
            modifier = Modifier.padding(start = 16.dp, bottom = 12.dp, top = 2.dp),
            text = value,
            style = MaterialTheme.typography.titleSmall,
        )
    }
}

sealed class PersonalDataEvent {
    data class OnNavigateClick(val route: String) : PersonalDataEvent()
    data class ConfirmBirthDay(val currentMillis: Long) : PersonalDataEvent()
    data class InputName(val input: String) : PersonalDataEvent()
    data class InputPhone(val input: String) : PersonalDataEvent()
    data class InputEmail(val input: String) : PersonalDataEvent()
    data object BackClick : PersonalDataEvent()
    data object ConfirmChanges : PersonalDataEvent()
    data object DeleteAccount : PersonalDataEvent()
    data object Nothing : PersonalDataEvent()
}