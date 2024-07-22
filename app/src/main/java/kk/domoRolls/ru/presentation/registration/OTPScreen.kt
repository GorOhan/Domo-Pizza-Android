package kk.domoRolls.ru.presentation.registration

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kk.domoRolls.ru.presentation.components.BaseButton
import kk.domoRolls.ru.presentation.components.RegistrationCodeInput
import kk.domoRolls.ru.presentation.navigation.Screen
import kk.domoRolls.ru.presentation.theme.DomoTheme
import kk.domoRolls.ru.presentation.theme.InterFont
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun OTPScreen(
    navController: NavController,
    registrationViewModel: RegistrationViewModel,
) {
    val loginUser by registrationViewModel.navigateToMain.collectAsState()

    LaunchedEffect(loginUser) {
        if (loginUser) navController.navigate(Screen.NotifyPermissionScreen.route)
    }

    OTPScreenUI(
        onGetOtpClick = { registrationViewModel.sendOTP() },
        onNavBackClick = { navController.popBackStack() },
        verifyOtp = { registrationViewModel.verifyOtpCode() },
        onOTPInput = { registrationViewModel.onOTPInput(it) },
        codeState = registrationViewModel.codeInput,
        phoneState = registrationViewModel.phoneNumber,
        otpLengthState = registrationViewModel.otpLength,
        loginButtonEnableState = registrationViewModel.isReadyToLogin,
        isOtpErrorState = registrationViewModel.isOtpError,
        setOtpError = { registrationViewModel.setOTPError(it) }
    )
}

@Composable
fun OTPScreenUI(
    onGetOtpClick: () -> Unit = {},
    onNavBackClick: () -> Unit = {},
    verifyOtp: () -> Unit = {},
    onOTPInput: (String) -> Unit = {},
    codeState: StateFlow<String> = MutableStateFlow(""),
    phoneState: StateFlow<String> = MutableStateFlow(""),
    otpLengthState: StateFlow<Int> = MutableStateFlow(0),
    loginButtonEnableState: StateFlow<Boolean> = MutableStateFlow(false),
    isOtpErrorState: StateFlow<Boolean> = MutableStateFlow(false),
    setOtpError: (Boolean) ->Unit = {},

) {
    val haptic = LocalHapticFeedback.current
    val code by codeState.collectAsState()
    val phone by phoneState.collectAsState()
    val otpLength by otpLengthState.collectAsState()
    val isOtpError by isOtpErrorState.collectAsState()
    val loginButtonEnable by loginButtonEnableState.collectAsState()

    LaunchedEffect(isOtpError) {
        if (isOtpError) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        } else {
            setOtpError.invoke(false)
        }
    }

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
            append("Мы очень")
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.secondary
                )
            ) {
                append(
                    " рады\nвас видеть"
                )
            }
            append(" Домо")
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 22.dp, top = 60.dp),
            text = annotatedString,
            fontSize = 36.sp,
            lineHeight = 42.sp,
            fontFamily = InterFont
        )

        Text(
            modifier = Modifier
                .padding(start = 22.dp, top = 24.dp)
                .fillMaxWidth(),
            text = "Код отправили сообщением на номер",
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            modifier = Modifier
                .padding(horizontal = 22.dp)
                .fillMaxWidth(),
            text = "+7$phone",
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.bodyMedium,
            )


        RegistrationCodeInput(
            modifier = Modifier
                .padding(vertical = 10.dp),
            codeLength = otpLength,
            initialCode = code,
            onTextChanged = {
                if (it.length == otpLength) {
                    //  viewModel.loginUser(otp = it, phone = phoneNumber)
                }
                if (it.length <= otpLength) {
                    onOTPInput(it)
                }

                setOtpError(false)
            },
            isError = isOtpError,
        )

        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = "Изменить номер",
                textAlign = TextAlign.Left,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.clickable { onNavBackClick() }
            )
            Text(
                modifier = Modifier.clickable { onGetOtpClick() },
                text = "Получить код повторно",
                textAlign = TextAlign.Left,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSecondary
            )
        }

        BaseButton(
            buttonTitle = "Войти",
            backgroundColor = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .padding(horizontal = 22.dp, vertical = 20.dp)
                .fillMaxWidth(),
            onClick = {
                verifyOtp()
            },
            enable = loginButtonEnable
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OTPScreenPreview() {
    DomoTheme {
        OTPScreenUI()
    }
}


