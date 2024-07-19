package com.domorolls.domo.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.domorolls.domo.ui.theme.InterFont

@Composable
fun BaseButton(
    buttonTitle: String,
    backgroundColor: Color,
    titleColor:Color = Color.White,
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