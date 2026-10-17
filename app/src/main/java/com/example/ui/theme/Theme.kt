package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val IdeDarkColorScheme = darkColorScheme(
    primary = IdePrimary,
    onPrimary = Color.White,
    secondary = IdeRunGreen,
    onSecondary = Color.White,
    tertiary = TokenKeyword,
    background = IdeBackground,
    onBackground = TokenIdentifier,
    surface = IdeSurface,
    onSurface = TokenIdentifier,
    surfaceVariant = IdeSurfaceVariant,
    onSurfaceVariant = TokenOperator,
    error = IdeDebugRed,
    onError = Color.White,
    outline = IdeBorder
)

@Composable
fun AndroidStudioMobileTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = IdeDarkColorScheme,
        typography = Typography,
        content = content
    )
}
