package kk.domoRolls.ru.registration

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kk.domoRolls.ru.components.BaseButton
import kk.domoRolls.ru.ui.theme.DomoGray
import kk.domoRolls.ru.ui.theme.DomoTheme
import kk.domoRolls.ru.ui.theme.InterFont

@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModel = hiltViewModel(),
) {
    RegistrationScreenUI()
}

@Composable
fun RegistrationScreenUI(){
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

        Box(
            modifier = Modifier
                .padding(start = 22.dp, end = 22.dp, top = 30.dp)
                .height(48.dp)
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    shape = RoundedCornerShape(24.dp),
                    color = DomoGray
                ),
            contentAlignment = Alignment.Center
        ) {
            BasicTextField(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp, vertical = 12.dp),
                value = "Ваше имя",
                textStyle = MaterialTheme.typography.titleSmall,
                onValueChange = { },
                //  modifier = Modifier.focusRequester(focusRequester = focusRequester),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            )
        }

        Box(
            modifier = Modifier
                .padding(start = 22.dp, end = 22.dp, top = 10.dp)
                .height(48.dp)
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    shape = RoundedCornerShape(24.dp),
                    color = DomoGray
                ),
            contentAlignment = Alignment.Center
        ) {
            BasicTextField(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp, vertical = 12.dp),
                value = "+7 999 999 99 99",
                singleLine = true,
                cursorBrush = Brush.verticalGradient(
                    0.00f to MaterialTheme.colorScheme.secondary,
                    0.35f to MaterialTheme.colorScheme.secondary,
                    0.35f to MaterialTheme.colorScheme.secondary,
                    0.90f to Color.Green,
                    0.90f to Color.Green,
                    1.00f to Color.Green
                ),
                textStyle = MaterialTheme.typography.titleSmall,
                onValueChange = { },
                //  modifier = Modifier.focusRequester(focusRequester = focusRequester),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            )

        }

        BaseButton(
            buttonTitle = "Получить смс-код ",
            backgroundColor = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .padding(horizontal = 22.dp, vertical = 10.dp)
                .fillMaxWidth()
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


