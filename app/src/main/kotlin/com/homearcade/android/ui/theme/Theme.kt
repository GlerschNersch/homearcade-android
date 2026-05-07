package com.homearcade.android.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary          = Orange500,
    onPrimary        = Slate900,
    primaryContainer = Orange400,
    secondary        = Purple400,
    background       = Slate900,
    surface          = Slate800,
    surfaceVariant   = Slate700,
    onBackground     = Slate200,
    onSurface        = Slate200,
    onSurfaceVariant = Slate400,
    error            = Red500,
)

@Composable
fun HomeArcadeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography  = Typography,
        content     = content,
    )
}
