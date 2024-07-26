package kk.domoRolls.ru.presentation.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import coil.compose.rememberAsyncImagePainter
import kk.domoRolls.ru.R
import kk.domoRolls.ru.data.model.order.ItemPrice
import kk.domoRolls.ru.data.model.order.MenuItem
import kk.domoRolls.ru.data.model.order.MenuItemSize
import kk.domoRolls.ru.presentation.theme.DomoBorder
import kk.domoRolls.ru.presentation.theme.DomoSub
import kk.domoRolls.ru.presentation.theme.DomoTheme
import kotlin.time.times

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
            .clickable(
                enabled = menuItem.isEnable,
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = {}
            )
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(20.dp),
                color = DomoBorder
            )
            .alpha(if (menuItem.isEnable) 1f else 0.3f)
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
            text = "${menuItem.itemSizes?.first()?.portionWeightGrams.toString()} гр",
            color = DomoSub
        )

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {


            AnimatedVisibility(visible = menuItem.countInCart > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(0.5f),
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
                            modifier = Modifier.size(14.dp),
                            painter = painterResource(id = R.drawable.ic_minus),
                            contentDescription = ""
                        )
                    }

                    Text(
                        text = menuItem.countInCart.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 16.sp,
                    )
                }
            }
            AnimatedVisibility(visible = menuItem.countInCart == 0) {
                Text(
                    text = "${
                        menuItem.itemSizes?.first()?.prices?.first()?.price?.toInt().toString()
                    } ₽",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
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
                    modifier = Modifier.size(14.dp),
                    painter = painterResource(id = R.drawable.ic_plus),
                    contentDescription = ""
                )
            }

        }

    }
}

@Composable
fun DomoLoading() {
    val infiniteTransition = rememberInfiniteTransition(label = "")

    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .alpha(0.4f)
            .background(Color.White)
    ) {
        Image(
            modifier = Modifier
                .size((alpha + 0.3) * 72.dp)
                .alpha(alpha),
            painter = painterResource(id = R.drawable.ic_domo),
            contentDescription = ""
        )
    }
}

@Composable
fun CartButton(
    menu: List<MenuItem>,
    backgroundColor: Color,
    titleColor: Color = Color.White,
    modifier: Modifier,
    enable: Boolean = true,
    onClick: () -> Unit = {}
) {
    val size = menu.sumOf { it.countInCart }
    val itemPrice = menu.filter { menuItem ->  menuItem.countInCart > 0 }
        .map { Pair(it.countInCart,it.itemSizes?.first()?.prices?.first()?.price?:0.0) }
        .sumOf { it.second*it.first }

    Button(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(0.4f)),
        enabled = enable,
        content = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.padding(end = 4.dp),
                        text = "Корзина",
                        color = titleColor,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Divider(
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxHeight()  //fill the max height
                            .width(2.dp)
                            .padding(vertical = 4.dp)
                    )

                    Text(
                        modifier = Modifier.padding(start = 6.dp),
                        text = size.toString(),
                        color = titleColor,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Text(
                    text = "${itemPrice.toInt()} ₽",
                    color = titleColor,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

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

@Preview(showBackground = true)
@Composable
fun RegistrationPreview() {
    DomoTheme {
        MenuItemComponent(MenuItem(name = "name"))
    }
}
