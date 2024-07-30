package kk.domoRolls.ru.presentation.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kk.domoRolls.ru.presentation.components.BaseButton
import kk.domoRolls.ru.presentation.theme.DomoTheme
import kk.domoRolls.ru.presentation.theme.InterFont
import kk.domoRolls.ru.R

@Composable
fun NotifyPermissionScreen() {

    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.secondary)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 22.dp, top = 148.dp),
            text = "Разрешите нам\n" +
                    "сообщать вам\n" +
                    "о важном",
            fontSize = 36.sp,
            lineHeight = 42.sp,
            fontFamily = InterFont,
            color = Color.White
        )

        Text(
            modifier = Modifier
                .padding(start = 22.dp, top = 20.dp)
                .fillMaxWidth(),
            text = "Чтобы отслеживать статус вашего заказа,\n" +
                    "получать важные уведомления о скидках и\n" +
                    "новостях нужно включить уведомления.",
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 22.dp, vertical = 48.dp)
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .fillMaxWidth()
                    .height(66.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Image(
                    modifier = Modifier.padding(start = 26.dp),
                    painter = painterResource(id = R.drawable.ic_domo),
                    contentDescription = "",
                )
                Column(
                    modifier = Modifier.padding(start = 26.dp),
                ) {
                    Text(
                        text = "Курьер в пути!",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = "Спешит к вам со всех сил",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

            }
            Image(
                modifier = Modifier
                    .padding(end = 32.dp)
                    .height(106.dp)
                    .width(86.dp)
                    .align(Alignment.CenterEnd),
                painter = painterResource(id = R.drawable.ic_text_attention),
                contentDescription = "",
            )
        }

        Spacer(Modifier.weight(1f))

        BaseButton(
            buttonTitle = "Разрешить",
            backgroundColor = Color.White,
            titleColor = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .padding(horizontal = 22.dp, vertical = 10.dp)
                .fillMaxWidth()
        )

        BaseButton(
            buttonTitle = "Не нужно",
            backgroundColor = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .padding(horizontal = 22.dp, vertical = 10.dp)
                .fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DomoTheme {
        NotifyPermissionScreen()
    }
}


