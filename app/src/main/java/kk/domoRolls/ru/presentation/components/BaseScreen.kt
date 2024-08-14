package kk.domoRolls.ru.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import kk.domoRolls.ru.util.BaseViewModel
import kk.domoRolls.ru.util.makeCall

@Composable
fun BaseScreen(
    baseViewModel: BaseViewModel,
    onBackClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    content.invoke()

    val showErrorDialog by baseViewModel.showMainError.collectAsState()
    if (showErrorDialog) {
        BaseDialog(
            title = "Извините, что-то пошло не так.",
            positiveButtonText = "Вернуться Назад",
            negativeButtonText = "",
            onConfirmClick = {
                baseViewModel.hideError()
                onBackClick()
            },
            onNegativeClick = {
                baseViewModel.hideError()
            },
            onDismissRequest = {
            }
        )
    }

    val showContactToSupport by baseViewModel.showContactToSupport.collectAsState()
    if (showContactToSupport) {
        BaseDialog(
            title = "Возникла ошибка при создании заказа! Свяжитесь с нами по номеру телефона \n+7 452 68 86 68",
            positiveButtonText = "Позже",
            negativeButtonText = "Позвонить",
            onConfirmClick = {
                baseViewModel.hideShowContactsError()
                onBackClick()
            },
            onNegativeClick = {
                context.makeCall("+7452688668")
                baseViewModel.hideShowContactsError()
            },
            onDismissRequest = {
            },
            hasNegativeButton = true
        )
    }
}