package kk.domoRolls.ru.presentation.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kk.domoRolls.ru.R
import kk.domoRolls.ru.presentation.theme.DomoTheme

@Composable
fun DomoToolbar(
    title: String = "Корзина",
    onBackClick: () -> Unit = {},
) {

    val buttonInteractionSource = remember { MutableInteractionSource() }
    val iconPressed by buttonInteractionSource.collectIsPressedAsState()

    val animatedIconDp by animateDpAsState(

        targetValue = if (iconPressed) 16.dp else 22.dp,
        animationSpec = tween(durationMillis = 500), label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .height(48.dp)
            .background(Color.White),
    ) {
        IconButton(
            interactionSource = buttonInteractionSource,
            onClick = { onBackClick.invoke() },
            modifier = Modifier
                .align(Alignment.CenterStart)
        )
        {
            Icon(
                modifier = Modifier.size(animatedIconDp),
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = ""
            )

        }

        Text(
            modifier = Modifier
                .height(24.dp)
                .align(Alignment.Center),
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontSize = 20.sp
        )

    }
}

@Preview(showBackground = true)
@Composable
fun DomoToolbarPreview() {
    DomoTheme {
        DomoToolbar()
    }
}