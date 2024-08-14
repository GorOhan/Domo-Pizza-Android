package kk.domoRolls.ru.presentation.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kk.domoRolls.ru.R
import kk.domoRolls.ru.presentation.theme.DomoBlue
import kk.domoRolls.ru.presentation.theme.DomoBorder
import kk.domoRolls.ru.presentation.theme.DomoGray


@Composable
fun BaseDialog(
    title: String,
    positiveButtonText: String,
    negativeButtonText: String,
    onConfirmClick: () -> Unit,
    onNegativeClick: () -> Unit,
    onDismissRequest: () -> Unit,
    positiveBackColor: Color  = DomoBlue
) {
    AlertDialog(
        icon = {
            Image(
                modifier = Modifier.size(82.dp),
                painter = painterResource(id = R.drawable.ic_error_alert),
                contentDescription = ""
            )
        },
        text = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                style = MaterialTheme.typography.titleSmall
            )
        },
        dismissButton = {

        },
        confirmButton = {
            Button(
                modifier = Modifier
                    .clip(RoundedCornerShape(0.4f)),
                enabled = true,
                onClick = {
                    onConfirmClick.invoke()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = DomoBlue,
                    contentColor = DomoGray,
                    disabledContainerColor = DomoGray,
                    disabledContentColor = DomoBlue
                )
            ){
                Text(
                    text = positiveButtonText,
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        },
        onDismissRequest = {
            onDismissRequest.invoke()
        },
    )
}