package kk.domoRolls.ru.presentation.html

import android.text.SpannableStringBuilder
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import kk.domoRolls.ru.presentation.theme.DomoTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun HtmlScreen(
    htmlScreenType: HTMLScreenType = HTMLScreenType.TERMS,
    viewModel: HTMLViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.setContent(htmlScreenType)
    }

    HtmlScreenUI(
        viewModel.content
    )
}

@Composable
fun HtmlScreenUI(
     contentState: StateFlow<String> = MutableStateFlow("")
) {
    val content = contentState.collectAsState()

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val spannableString = SpannableStringBuilder(content.value).toString()
        val spanned = HtmlCompat.fromHtml(spannableString, HtmlCompat.FROM_HTML_MODE_COMPACT)

        val annotatedString = buildAnnotatedString {
            append(spanned)
        }

        Text(
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 32.dp),
            text = annotatedString,
            color = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DomoTheme {
        HtmlScreen()
    }
}

enum class HTMLScreenType {
    TERMS,
    OFFER
}
