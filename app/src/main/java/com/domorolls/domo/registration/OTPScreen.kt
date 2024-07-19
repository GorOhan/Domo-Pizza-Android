package com.domorolls.domo.registration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.domorolls.domo.components.BaseButton
import com.domorolls.domo.components.RegistrationCodeInput
import com.domorolls.domo.ui.theme.DomoTheme
import com.domorolls.domo.ui.theme.InterFont

@Composable
fun OTPScreen() {

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
            text = "+7 999 999 99 99",
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.bodyMedium,

            )


        RegistrationCodeInput(
            modifier = Modifier
                .padding(vertical = 10.dp),
            codeLength = 4,
            initialCode = "23",
            onTextChanged = {
                if (it.length == 4) {
                    //  viewModel.loginUser(otp = it, phone = phoneNumber)
                }
                if (it.length <= 4) {
                    // otp = it
                }
//                if (otp.length != 6) {
//                    viewModel.setOtpError()
//                }

            },
            isError = false
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
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
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
                .fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OTPScreenPreview() {
    DomoTheme {
        OTPScreen()
    }
}


