package com.example.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun StarIcon(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFF2C94C)
) {
    Canvas(modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val outer = minOf(size.width, size.height) * 0.47f
        val inner = outer * 0.45f
        val path = Path()
        repeat(10) { i ->
            val radius = if (i % 2 == 0) outer else inner
            val angle = -PI / 2 + i * PI / 5
            val x = cx + cos(angle).toFloat() * radius
            val y = cy + sin(angle).toFloat() * radius
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        drawPath(path, color)
    }
}

@Composable
fun CheckIcon(
    modifier: Modifier = Modifier,
    color: Color = Color.White
) {
    Canvas(modifier) {
        drawLine(
            color,
            Offset(size.width * 0.18f, size.height * 0.52f),
            Offset(size.width * 0.42f, size.height * 0.76f),
            strokeWidth = size.width * 0.13f
        )
        drawLine(
            color,
            Offset(size.width * 0.42f, size.height * 0.76f),
            Offset(size.width * 0.84f, size.height * 0.24f),
            strokeWidth = size.width * 0.13f
        )
    }
}
