package kk.domoRolls.ru.presentation.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import kk.domoRolls.ru.R
import kk.domoRolls.ru.data.model.order.MenuItem
import kk.domoRolls.ru.presentation.theme.DomoBorder
import kk.domoRolls.ru.presentation.theme.DomoRed
import kk.domoRolls.ru.presentation.theme.DomoSub

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun GiftProductItem(
    menuItem: MenuItem,
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
                            painter = painterResource(id = R.drawable.ic_gift),
                            contentDescription = ""
                        )
                        Text(
                            fontSize = 6.sp,
                            style = MaterialTheme.typography.bodySmall,
                            text = "ПОДАРОК",
                            color = Color.White
                        )
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
                            text = "0 ₽",
                            style = MaterialTheme.typography.bodyLarge,
                        )
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