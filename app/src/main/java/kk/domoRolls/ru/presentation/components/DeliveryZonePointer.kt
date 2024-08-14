package kk.domoRolls.ru.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kk.domoRolls.ru.presentation.theme.DomoBlue

@Composable
fun DeliveryZonePointer(
    deliveryTime: State<String>,
    modifier: Modifier,
    inDeliveryZone: Boolean,
    isDragFinished: Boolean
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .background(DomoBlue, RoundedCornerShape(20.dp))

        ) {
            Text(
                modifier = Modifier.padding(6.dp),
                text = if (inDeliveryZone) "~${deliveryTime.value} минут" else "Вне зоны доставки",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 8.sp,
                color = Color.White
            )
        }

        Divider(
            Modifier
                .width(2.dp)
                .height(4.dp),
            color = DomoBlue
        )

        AnimatedVisibility(visible = isDragFinished) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(DomoBlue)
            )
        }
    }
}