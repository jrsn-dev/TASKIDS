package com.example.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import com.example.game.GameThemeKey

@Composable
fun GameWorldBackground(
    theme: GameThemeKey,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val colors = when (theme) {
            GameThemeKey.SKY -> listOf(Color(0xFF102A68), Color(0xFF1855A8), Color(0xFF4BA3D9))
            GameThemeKey.SPACE -> listOf(Color(0xFF080E2C), Color(0xFF1C1750), Color(0xFF31277A))
            GameThemeKey.FOREST -> listOf(Color(0xFF0B3A3B), Color(0xFF145E52), Color(0xFF2B8062))
            GameThemeKey.ISLAND -> listOf(Color(0xFF0A4C78), Color(0xFF1789B7), Color(0xFF5BC0CC))
        }

        drawRect(Brush.verticalGradient(colors))

        when (theme) {
            GameThemeKey.SPACE -> {
                val stars = listOf(
                    0.08f to 0.12f, 0.16f to 0.32f, 0.28f to 0.18f, 0.42f to 0.09f,
                    0.54f to 0.26f, 0.67f to 0.14f, 0.76f to 0.34f, 0.88f to 0.16f,
                    0.94f to 0.42f, 0.34f to 0.39f
                )
                stars.forEachIndexed { index, (x, y) ->
                    drawCircle(
                        if (index % 3 == 0) Color(0xFFF2C94C) else Color.White.copy(alpha = 0.8f),
                        radius = if (index % 2 == 0) 3.5f else 2f,
                        center = Offset(w * x, h * y)
                    )
                }
                drawCircle(Color(0xFFB9C7FF).copy(alpha = 0.28f), w * 0.07f, Offset(w * 0.82f, h * 0.20f))
                drawCircle(Color(0xFF7D8AE8).copy(alpha = 0.5f), w * 0.045f, Offset(w * 0.82f, h * 0.20f))
            }
            GameThemeKey.FOREST -> {
                repeat(8) { i ->
                    val x = w * (i / 7f)
                    val base = h * (0.78f + (i % 2) * 0.03f)
                    val tree = Path().apply {
                        moveTo(x, base - h * 0.23f)
                        lineTo(x - w * 0.045f, base)
                        lineTo(x + w * 0.045f, base)
                        close()
                    }
                    drawPath(tree, Color(0xFF0C4A3D).copy(alpha = 0.8f))
                }
            }
            GameThemeKey.ISLAND -> {
                drawOval(
                    Color(0xFFEBD79C).copy(alpha = 0.72f),
                    topLeft = Offset(w * 0.12f, h * 0.68f),
                    size = Size(w * 0.76f, h * 0.28f)
                )
                repeat(5) { i ->
                    drawCircle(Color.White.copy(alpha = 0.24f), w * 0.025f, Offset(w * (0.18f + i * 0.16f), h * 0.58f))
                }
            }
            GameThemeKey.SKY -> {
                repeat(5) { i ->
                    val x = w * (0.08f + i * 0.20f)
                    val y = h * (0.16f + (i % 2) * 0.10f)
                    drawCircle(Color.White.copy(alpha = 0.14f), w * 0.045f, Offset(x, y))
                    drawCircle(Color.White.copy(alpha = 0.12f), w * 0.035f, Offset(x + w * 0.035f, y + h * 0.01f))
                }
            }
        }

        // distant hills / ground
        val ground = Path().apply {
            moveTo(0f, h)
            lineTo(0f, h * 0.78f)
            cubicTo(w * 0.18f, h * 0.65f, w * 0.30f, h * 0.84f, w * 0.48f, h * 0.72f)
            cubicTo(w * 0.67f, h * 0.58f, w * 0.80f, h * 0.78f, w, h * 0.66f)
            lineTo(w, h)
            close()
        }
        drawPath(
            ground,
            when (theme) {
                GameThemeKey.SPACE -> Color(0xFF17183A).copy(alpha = 0.86f)
                GameThemeKey.FOREST -> Color(0xFF083D35).copy(alpha = 0.88f)
                GameThemeKey.ISLAND -> Color(0xFF147D8D).copy(alpha = 0.58f)
                GameThemeKey.SKY -> Color(0xFF0D3D78).copy(alpha = 0.70f)
            }
        )
    }
}
