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
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import coil.compose.rememberAsyncImagePainter
import kk.domoRolls.ru.R
import kk.domoRolls.ru.data.model.order.MenuItem
import kk.domoRolls.ru.presentation.theme.DomoBorder
import kk.domoRolls.ru.presentation.theme.DomoGray
import kk.domoRolls.ru.presentation.theme.DomoGreen
import kk.domoRolls.ru.presentation.theme.DomoPressed
import kk.domoRolls.ru.presentation.theme.DomoRed
import kk.domoRolls.ru.presentation.theme.DomoSub
import kk.domoRolls.ru.presentation.theme.DomoTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun BaseButton(
    buttonTitle: String,
    backgroundColor: Color,
    titleColor: Color = Color.White,
    modifier: Modifier,
    enable: Boolean = true,
    onClick: () -> Unit = {}
) {
    val buttonInteractionSource = remember { MutableInteractionSource() }
    val iconPressed by buttonInteractionSource.collectIsPressedAsState()

    Button(
        interactionSource = buttonInteractionSource,
        modifier = modifier
            .height(50.dp)
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
            containerColor = if (iconPressed) DomoPressed else backgroundColor,
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
    onProductClick: () -> Unit = {}
) {
    Column(
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
            if (menuItem.isHot){
                Row(modifier = Modifier
                    .padding(10.dp)
                    .width(48.dp)
                    .height(16.dp)
                    .background(DomoRed, RoundedCornerShape(10.dp)),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(
                        modifier = Modifier.size(8.dp),
                        tint = Color.White,
                        painter = painterResource(id = R.drawable.ic_hot),
                        contentDescription ="" )
                    Text(
                        fontSize = 6.sp,
                        style = MaterialTheme.typography.bodySmall,
                        text = "ОСТРЫЙ",
                        color = Color.White
                    )
                }
            }

            if (menuItem.isNew){
                Row(modifier = Modifier
                    .padding(10.dp)
                    .width(48.dp)
                    .height(16.dp)
                    .background(DomoGreen, RoundedCornerShape(10.dp)),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(
                        modifier = Modifier.size(8.dp),
                        tint = Color.White,
                        painter = painterResource(id = R.drawable.ic_new),
                        contentDescription ="" )
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
    val itemPrice = menu.filter { menuItem -> menuItem.countInCart > 0 }
        .map { Pair(it.countInCart, it.itemSizes?.first()?.prices?.first()?.price ?: 0.0) }
        .sumOf { it.second * it.first }

    val buttonInteractionSource = remember { MutableInteractionSource() }
    val buttonPressed by buttonInteractionSource.collectIsPressedAsState()

    Button(
        interactionSource = buttonInteractionSource,
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
            containerColor = if (!buttonPressed) backgroundColor else DomoPressed,
            contentColor = Color.White,
            disabledContainerColor = MaterialTheme.colorScheme.onSecondary,
            disabledContentColor = Color.White
        )
    )
}

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun ProductBottomSheet(
    menuItem: MenuItem = MenuItem(),
    menu: List<MenuItem> = emptyList(),
    onPlusClick: () -> Unit = {},
    onMinusClick: () -> Unit = {},
) {
    var countInCart by remember {
        mutableIntStateOf(0)
    }

    var itemPrice by remember {
        mutableDoubleStateOf(0.0)
    }


    LaunchedEffect(key1 = menu) {
        countInCart = menu.find { it.itemId == menuItem.itemId }?.countInCart ?: 0
        itemPrice = menu.filter { menuItem -> menuItem.countInCart > 0 }
            .map { Pair(it.countInCart, it.itemSizes?.first()?.prices?.first()?.price ?: 0.0) }
            .sumOf { it.second * it.first }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 32.dp)
            .background(Color.White)
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .height(246.dp),
            contentScale = ContentScale.Crop,
            painter = rememberAsyncImagePainter(menuItem.itemSizes?.first()?.buttonImageUrl),
            contentDescription = null,
        )

        Text(
            modifier = Modifier.padding(top = 12.dp),
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black,
            text = menuItem.name ?: ""
        )

        Text(
            modifier = Modifier.padding(top = 10.dp),
            style = MaterialTheme.typography.titleSmall,
            color = DomoGray,
            text = "Состав"
        )

        Text(
            modifier = Modifier.padding(top = 10.dp, bottom = 40.dp),
            style = MaterialTheme.typography.bodySmall,
            color = Color.Black,
            text = menuItem.description ?: ""
        )


        AnimatedVisibility(countInCart == 0) {
            BaseButton(
                buttonTitle = "В корзину",
                backgroundColor = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .fillMaxWidth(),
                onClick = {
                    onPlusClick()
                }
            )
        }
        AnimatedVisibility(countInCart != 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .padding(horizontal = 14.dp),
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

                    if (countInCart > 0) {
                        Text(
                            text = countInCart.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 16.sp,
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
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            modifier = Modifier.size(14.dp),
                            painter = painterResource(id = R.drawable.ic_plus),
                            contentDescription = ""
                        )
                    }
                }

                BaseButton(
                    buttonTitle = "${itemPrice.toInt()} ₽",
                    backgroundColor = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, bottom = 20.dp, top = 20.dp),
                    onClick = { }
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
