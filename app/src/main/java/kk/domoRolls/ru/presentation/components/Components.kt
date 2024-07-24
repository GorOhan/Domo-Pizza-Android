package kk.domoRolls.ru.presentation.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import kk.domoRolls.ru.R
import kk.domoRolls.ru.data.model.order.MenuItem
import kk.domoRolls.ru.presentation.theme.DomoBorder
import kk.domoRolls.ru.presentation.theme.DomoSub
import kk.domoRolls.ru.presentation.theme.DomoTheme

@Composable
fun BaseButton(
    buttonTitle: String,
    backgroundColor: Color,
    titleColor: Color = Color.White,
    modifier: Modifier,
    enable: Boolean = true,
    onClick: () -> Unit = {}
) {
    Button(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(0.4f)),
        enabled = enable,
        content = {
            Text(
                text = buttonTitle,
                color = titleColor,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        onClick = { onClick.invoke() },
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = Color.White,
            disabledContainerColor = MaterialTheme.colorScheme.onSecondary,
            disabledContentColor = Color.White
        )
    )
}

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun MenuItemComponent(
    menuItem: MenuItem,
    onMinusClick: () -> Unit = {},
    onPlusClick: () -> Unit = {},
    ) {
    Column(
        modifier = Modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(20.dp),
                color = DomoBorder
            ),
    ) {
        Box(
            modifier = Modifier
                .height(104.dp)
        ) {
            Image(
                contentScale = ContentScale.Crop,
                painter = rememberAsyncImagePainter(menuItem.itemSizes?.first()?.buttonImageUrl),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }

        Text(
            minLines = 2,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 16.dp),
            text = menuItem.name ?: ""
        )

        Text(
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 16.dp),
            text = menuItem.itemSizes?.first()?.portionWeightGrams.toString(),
            color = DomoSub
        )

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DomoBorder)
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = rememberRipple(),
                        onClick = { onMinusClick() }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(id = R.drawable.ic_minus),
                    contentDescription = ""
                )
            }

            Text(
                text = "4",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 16.sp,
            )
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DomoBorder)
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = rememberRipple(),
                        onClick = { onPlusClick() }
                    ),
                contentAlignment = Alignment.Center,
            ) {

                Image(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(id = R.drawable.ic_plus),
                    contentDescription = ""
                )
            }

        }

    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationPreview() {
    DomoTheme {
        MenuItemComponent(MenuItem(name = "name"))
    }
}
