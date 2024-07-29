package kk.domoRolls.ru.presentation.components

import android.annotation.SuppressLint
import android.graphics.Rect
import android.view.ViewTreeObserver
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import kk.domoRolls.ru.R
import kk.domoRolls.ru.data.model.order.MenuItem
import kk.domoRolls.ru.presentation.theme.DomoBlue
import kk.domoRolls.ru.presentation.theme.DomoBorder
import kk.domoRolls.ru.presentation.theme.DomoGray
import kk.domoRolls.ru.presentation.theme.DomoGreen
import kk.domoRolls.ru.presentation.theme.DomoRed
import kk.domoRolls.ru.presentation.theme.DomoSub
import kk.domoRolls.ru.presentation.theme.DomoTheme

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun CartMenuItem(
    menuItem: MenuItem,
    onMinusClick: () -> Unit = {},
    onPlusClick: () -> Unit = {},
    onProductClick: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .padding(4.dp)
                .clickable(
                    enabled = menuItem.isEnable,
                    interactionSource = MutableInteractionSource(),
                    indication = null,
                    onClick = {
                        onProductClick()
                    }
                )
                .clip(RoundedCornerShape(20.dp))
                .alpha(if (menuItem.isEnable) 1f else 0.3f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(120.dp)
            ) {

                Image(
                    contentScale = ContentScale.Crop,
                    painter = rememberAsyncImagePainter(menuItem.itemSizes?.first()?.buttonImageUrl),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
                if (menuItem.isHot) {
                    Row(
                        modifier = Modifier
                            .padding(10.dp)
                            .width(48.dp)
                            .height(16.dp)
                            .background(DomoRed, RoundedCornerShape(10.dp)),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(8.dp),
                            tint = Color.White,
                            painter = painterResource(id = R.drawable.ic_hot),
                            contentDescription = ""
                        )
                        Text(
                            fontSize = 6.sp,
                            style = MaterialTheme.typography.bodySmall,
                            text = "ОСТРЫЙ",
                            color = Color.White
                        )
                    }
                }

                if (menuItem.isNew) {
                    Row(
                        modifier = Modifier
                            .padding(10.dp)
                            .width(48.dp)
                            .height(16.dp)
                            .background(DomoGreen, RoundedCornerShape(10.dp)),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(8.dp),
                            tint = Color.White,
                            painter = painterResource(id = R.drawable.ic_new),
                            contentDescription = ""
                        )
                        Text(
                            fontSize = 6.sp,
                            style = MaterialTheme.typography.bodySmall,
                            text = "НОВЫЙ",
                            color = Color.White
                        )
                    }
                }

            }

            Column {
                Text(
                    minLines = 2,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp),
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

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween

                    ) {
                        Text(
                            text = "${
                                menuItem.itemSizes?.first()?.prices?.first()?.price?.toInt()
                                    .toString()
                            } ₽",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Row(
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .fillMaxWidth(1f),
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
            }

        }

        Divider(
            modifier = Modifier
                .align(
                    Alignment.BottomCenter
                )
                .padding(horizontal = 22.dp)
                .fillMaxWidth()
                .width(1.dp),
            color = DomoBorder
        )
    }
}

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun Devices() {
    Box {
        Row(
            modifier = Modifier
                .padding(22.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Приборы ",
                style = MaterialTheme.typography.titleSmall
            )
            Row(
                modifier = Modifier
                    .padding(start = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
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
                            onClick = { }
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
                    text = "0",
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
                            onClick = { }
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

        Divider(
            modifier = Modifier
                .align(
                    Alignment.BottomCenter
                )
                .padding(horizontal = 22.dp)
                .fillMaxWidth()
                .width(1.dp),
            color = DomoBorder
        )
    }
}

@Composable
fun SpicesSection(
    spices: List<MenuItem> = emptyList(),
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.padding(start = 22.dp, top = 20.dp),
            style = MaterialTheme.typography.titleSmall,
            text = "Добавить специи в заказ"
        )
        Text(
            modifier = Modifier.padding(start = 22.dp, top = 6.dp),
            color = DomoRed,
            style = MaterialTheme.typography.bodyMedium,
            text = "В заказ идёт 1 набор специй в подарок"
        )
        LazyRow(
            modifier = Modifier.padding(top = 32.dp)
        ) {
            itemsIndexed(spices) { index, item ->
                SpiceItem(item)
            }
        }

    }
}

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun SpiceItem(
    menuItem: MenuItem,
    onMinusClick: () -> Unit = {},
    onPlusClick: () -> Unit = {},
    onProductClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .padding(4.dp)
            .width(164.dp)
            .clickable(
                enabled = menuItem.isEnable,
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = {
                    onProductClick()
                }
            )
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(20.dp),
                color = DomoBorder
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .width(164.dp)
                .height(98.dp)
        ) {

            Image(
                contentScale = ContentScale.Crop,
                painter = rememberAsyncImagePainter(menuItem.itemSizes?.first()?.buttonImageUrl),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
            if (menuItem.isHot) {
                Row(
                    modifier = Modifier
                        .padding(10.dp)
                        .width(48.dp)
                        .height(16.dp)
                        .background(DomoRed, RoundedCornerShape(10.dp)),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(8.dp),
                        tint = Color.White,
                        painter = painterResource(id = R.drawable.ic_hot),
                        contentDescription = ""
                    )
                    Text(
                        fontSize = 6.sp,
                        style = MaterialTheme.typography.bodySmall,
                        text = "ОСТРЫЙ",
                        color = Color.White
                    )
                }
            }

            if (menuItem.isNew) {
                Row(
                    modifier = Modifier
                        .padding(10.dp)
                        .width(48.dp)
                        .height(16.dp)
                        .background(DomoGreen, RoundedCornerShape(10.dp)),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(8.dp),
                        tint = Color.White,
                        painter = painterResource(id = R.drawable.ic_new),
                        contentDescription = ""
                    )
                    Text(
                        fontSize = 6.sp,
                        style = MaterialTheme.typography.bodySmall,
                        text = "НОВЫЙ",
                        color = Color.White
                    )
                }
            }

        }

        Text(
            minLines = 2,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 16.dp),
            text = menuItem.name ?: ""
        )

        Text(
            text = "${
                menuItem.itemSizes?.first()?.prices?.first()?.price?.toInt().toString()
            } ₽",
            style = MaterialTheme.typography.bodyLarge,
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
                    modifier = Modifier
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

    }
}

@Composable
fun PromoInput(
    modifier: Modifier
) {
    Column(
        modifier = modifier
    ) {
        Divider(
            modifier = Modifier
                .padding(start = 22.dp, end = 22.dp, top = 22.dp)
                .fillMaxWidth()
                .width(1.dp),
            color = DomoBorder
        )
        Row(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .padding(start = 22.dp)
                    .fillMaxWidth(.6f),
                shape = RoundedCornerShape(24.dp),
                value = "",
                singleLine = true,
                onValueChange = { value -> },
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = DomoBlue,
                    unfocusedBorderColor = DomoGray,
                    focusedBorderColor = DomoBlue,
                    unfocusedTextColor = DomoGray,
                    focusedTextColor = Color.Black,
                    unfocusedPlaceholderColor = DomoGray,
                    focusedPlaceholderColor = DomoGray,
                ),
                textStyle = MaterialTheme.typography.titleSmall,
                placeholder = {
                    Text(
                        text = "Промокод",
                        style = MaterialTheme.typography.titleSmall
                    )
                },
            )

            BaseButton(
                buttonTitle = "Ввести",
                backgroundColor = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .padding(horizontal = 22.dp)
                    .fillMaxWidth()
            )
        }
        Row(
            modifier = Modifier
                .padding(start = 22.dp, end = 22.dp, top = 24.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "5 товаров")
            Text(text = "3544 ₽")
        }

        Row(
            modifier = Modifier
                .padding(start = 22.dp, end = 22.dp, top = 10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "5 товаров")
            Text(text = "3544 ₽")
        }

        Row(
            modifier = Modifier
                .padding(start = 22.dp, end = 22.dp, top = 10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "5 товаров")
            Text(text = "3544 ₽")
        }

        Divider(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth()
                .width(1.dp),
            color = DomoBorder
        )

        BaseButton(
            buttonTitle = "Оформить заказ на 3444 ₽",
            backgroundColor = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .imePadding()
                .padding(horizontal = 22.dp, vertical = 20.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
fun isKeyboardVisible(): Boolean {
    val view = LocalView.current
    val density = LocalDensity.current
    var isKeyboardVisible by remember { mutableStateOf(false) }

    DisposableEffect(view) {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.height
            val keypadHeight = screenHeight - rect.bottom
            isKeyboardVisible = keypadHeight > screenHeight * 0.15 // 0.15 ratio can be adjusted
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(listener)
        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }

    return isKeyboardVisible
}

@Preview(showBackground = true)
@Composable
fun CartMenuItemPreview() {
    DomoTheme {
        CartMenuItem(MenuItem(name = "name"))
    }
}