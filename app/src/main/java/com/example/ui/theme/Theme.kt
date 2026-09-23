package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.ui.design.TaskIdsColors

private val TaskIdsColorScheme = darkColorScheme(
    primary = TaskIdsColors.Blue,
    secondary = TaskIdsColors.Yellow,
    tertiary = TaskIdsColors.Green,
    background = TaskIdsColors.Navy900,
    surface = TaskIdsColors.Navy800,
    onPrimary = Color.White,
    onSecondary = TaskIdsColors.Ink,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TaskIdsColorScheme,
        typography = Typography,
        content = content
    )
}
