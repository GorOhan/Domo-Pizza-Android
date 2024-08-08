package kk.domoRolls.ru.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kk.domoRolls.ru.data.model.order.Order
import kk.domoRolls.ru.data.model.order.OrderStatus
import kk.domoRolls.ru.data.model.order.orderStatusTitle

@Composable
fun CurrentOrder(
    order: Order,
    onOrderClick:()->Unit = {}
){
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF4F43DC),Color(0xFF7668EC))
                )
            )
            .clickable {
                onOrderClick()
            }
        ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .padding(start = 22.dp)
        ) {
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = "Заказ принят",
                color = Color.White,
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                modifier = Modifier.padding(top = 3.dp,bottom = 16.dp),
                text = OrderStatus.entries.find { it.value == order.orderItem?.status }?.orderStatusTitle?:"",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Text(
            modifier = Modifier.padding(end = 22.dp),
            text = "~65 мин",
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}