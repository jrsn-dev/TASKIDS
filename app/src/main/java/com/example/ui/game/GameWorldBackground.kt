package com.example.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.game.GameThemeKey

/** Soft landscape shared by the child screens. Decoration stays behind the content. */
@Composable
fun GameWorldBackground(theme: GameThemeKey, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val sky = when (theme) {
            GameThemeKey.SPACE -> Color(0xFFE9E7FF)
            GameThemeKey.FOREST -> Color(0xFFE5F8E9)
            GameThemeKey.ISLAND -> Color(0xFFE1F8FA)
            GameThemeKey.SKY -> Color(0xFFD7F1FF)
        }
        drawRect(Brush.verticalGradient(listOf(sky, Color(0xFFF7FBFF), Color.White)))
        val cloud = Color.White.copy(alpha = 0.82f)
        listOf(0.08f to 0.16f, 0.43f to 0.10f, 0.80f to 0.19f, 0.17f to 0.84f, 0.70f to 0.87f).forEach { (x, y) ->
            val cx = w * x
            val cy = h * y
            val r = h * 0.065f
            drawCircle(cloud, r, Offset(cx - r * 0.72f, cy + r * 0.16f))
            drawCircle(cloud, r * 1.25f, Offset(cx, cy - r * 0.32f))
            drawCircle(cloud, r * 0.90f, Offset(cx + r * 0.86f, cy + r * 0.10f))
        }
        val accent = listOf(Color(0xFFFFD95A), Color(0xFF9BEEB3), Color(0xFFFFB9DC), Color(0xFF91D5FF))
        listOf(0.10f to 0.40f, 0.36f to 0.79f, 0.65f to 0.24f, 0.91f to 0.68f).forEachIndexed { index, (x, y) ->
            drawCircle(accent[index].copy(alpha = 0.28f), h * 0.10f, Offset(w * x, h * y))
        }
        listOf(0.05f to 0.10f, 0.93f to 0.12f, 0.10f to 0.72f, 0.87f to 0.80f).forEach { (x, y) ->
            drawCircle(Color(0xFFFFCB32).copy(alpha = 0.8f), h * 0.011f, Offset(w * x, h * y))
        }
    }
}
