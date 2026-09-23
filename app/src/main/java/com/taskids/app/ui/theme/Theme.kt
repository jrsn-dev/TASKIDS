package com.taskids.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val TaskidsColorScheme = darkColorScheme(
    primary = Blue500,
    secondary = Yellow500,
    tertiary = Purple500,
    background = Navy950,
    surface = Navy900,
    onPrimary = White,
    onSecondary = Ink900,
    onBackground = White,
    onSurface = White
)

@Composable
fun TaskidsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TaskidsColorScheme,
        typography = TaskidsTypography,
        content = content
    )
}
