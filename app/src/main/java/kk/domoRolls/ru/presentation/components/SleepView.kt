package kk.domoRolls.ru.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kk.domoRolls.ru.R
import kk.domoRolls.ru.presentation.theme.DomoOpacity
import kk.domoRolls.ru.presentation.theme.DomoTheme

@Composable
fun SleepView(
    seeMenuClick:()->Unit = {}
) {

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(DomoOpacity)

    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(22.dp)
                .height(280.dp)
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(24.dp))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.padding(top = 64.dp),
                    style = MaterialTheme.typography.titleSmall,
                    text = "Извините, мы уже спим (:"
                )
                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    text = "Вы можете ознакомиться с меню и завтра с 11:00 мы будем рады принять ваш заказ."
                )

                BaseButton(
                    buttonTitle = "Ознакомиться с меню",
                    backgroundColor = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .padding(top = 32.dp, start = 20.dp, end = 20.dp)
                        .fillMaxWidth(),
                    onClick = { seeMenuClick() },
                    enable = true
                )

            }
        }

        Image(
            modifier = Modifier
                .padding(bottom = 280.dp)
                .width(240.dp)
                .height(184.dp)
                .align(Alignment.Center),
            painter = painterResource(id = R.drawable.sleep_logo),
            contentDescription = "",
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SleepViewPreview() {
    DomoTheme {
        SleepView()
    }
}
