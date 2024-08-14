package kk.domoRolls.ru.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kk.domoRolls.ru.util.BaseViewModel

@Composable
fun BaseScreen(
    baseViewModel: BaseViewModel,
    onBackClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
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
}