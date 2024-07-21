package kk.domoRolls.ru.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kk.domoRolls.ru.ui.theme.DomoBlue
import kk.domoRolls.ru.ui.theme.DomoGray
import kotlinx.coroutines.delay


@Composable
fun RegistrationCodeInput(
    modifier: Modifier = Modifier,
    codeLength: Int,
    initialCode: String,
    onTextChanged: (String) -> Unit,
    isError: Boolean = false,
) {
    var code by remember(initialCode) {
        mutableStateOf(TextFieldValue(initialCode, TextRange(initialCode.length)))
    }
    val focusRequester = FocusRequester()
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(isError) {
        delay(1000)
        code = TextFieldValue("", TextRange(initialCode.length))
    }

    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        BasicTextField(
            value = code,
            onValueChange = { onTextChanged(it.text) },
            modifier = modifier.focusRequester(focusRequester = focusRequester),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            decorationBox = {
                CodeInputDecoration(code.text, codeLength, isError)

            }
        )
    }
}

@Composable
private fun CodeEntry(text: String, isError: Boolean = false) {

    val color = animateColorAsState(
        targetValue = if (isError) Color.Red
        else MaterialTheme.colorScheme.onSecondary, label = ""
    )

    Box(
        modifier = Modifier
            .padding(4.dp)
            .width(74.dp)
            .height(80.dp)
            .clip(RectangleShape)
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(24.dp),
                color = if (text.isEmpty()) DomoGray else DomoBlue
            ),

        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = text,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun CodeInputDecoration(code: String, length: Int, isError: Boolean = false) {
    Box(modifier = Modifier) {
        Row(horizontalArrangement = Arrangement.Center) {
            for (i in 0 until length) {
                val text = if (i < code.length) code[i].toString() else ""
                val isFinalError = isError && code.length == length
                CodeEntry(text, isFinalError)
            }
        }
    }
}
