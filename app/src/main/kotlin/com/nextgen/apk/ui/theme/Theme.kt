package com.nextgen.apk.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color

// Color palette for NextGen APK
private val Purple80 = Color(0xFFD0BCFF)
private val PurpleGrey80 = Color(0xFFCCC2DC)
private val Pink80 = Color(0xFFEFB8C8)

private val Purple40 = Color(0xFF6650a4)
private val PurpleGrey40 = Color(0xFF625b71)
private val Pink40 = Color(0xFF7D5260)

// NextGen APK specific colors
private val NextGenPrimary = Color(0xFF2196F3)
private val NextGenSecondary = Color(0xFF4CAF50)
private val NextGenError = Color(0xFFF44336)

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    error = NextGenError
)

private val LightColorScheme = lightColorScheme(
    primary = NextGenPrimary,
    secondary = NextGenSecondary,
    tertiary = Pink40,
    error = NextGenError
)

@Composable
fun NextGenAPKTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}