package kk.domoRolls.ru.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp


private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = DomoBlue,
    tertiary = Pink40,
    onSecondary = DomoGray,

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun DomoTheme(content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    val typography = Typography(
        titleMedium = TextStyle(
            fontWeight = FontWeight.Normal,
            fontFamily = InterFont,
            fontSize = 32.sp
        ),
        titleLarge = TextStyle(
            fontFamily = InterFont,
            fontWeight = FontWeight.Bold,
            fontSize = 64.sp
        ),
        titleSmall = TextStyle(
            fontWeight = FontWeight.Thin,
            fontSize = 16.sp,
            lineHeight = 40.sp,
            textAlign = TextAlign.Start,
            fontFamily = InterFont
        ),
        bodyMedium = TextStyle(
            fontFamily = InterFont,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp
        ),
        bodySmall = TextStyle(
            fontFamily = InterFont,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.tertiary
        ),
        bodyLarge = TextStyle(
            fontFamily = InterFont,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        ),
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}