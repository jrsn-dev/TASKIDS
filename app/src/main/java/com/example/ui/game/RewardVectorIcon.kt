package com.example.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun RewardVectorIcon(
    type: String,
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val stroke = minOf(w, h) * 0.08f

        when (type.uppercase()) {
            "SCREEN_TIME" -> {
                drawRoundRect(
                    color = tint.copy(alpha = 0.15f),
                    topLeft = Offset(w * 0.12f, h * 0.16f),
                    size = Size(w * 0.76f, h * 0.52f)
                )
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.12f, h * 0.16f),
                    size = Size(w * 0.76f, h * 0.52f),
                    style = Stroke(stroke)
                )
                drawLine(tint, Offset(w * 0.5f, h * 0.68f), Offset(w * 0.5f, h * 0.82f), stroke, StrokeCap.Round)
                drawLine(tint, Offset(w * 0.35f, h * 0.84f), Offset(w * 0.65f, h * 0.84f), stroke, StrokeCap.Round)
            }
            "MOVIE" -> {
                drawCircle(tint.copy(alpha = 0.15f), minOf(w,h) * 0.33f, Offset(w * 0.5f, h * 0.5f))
                drawCircle(tint, minOf(w,h) * 0.33f, Offset(w * 0.5f, h * 0.5f), style = Stroke(stroke))
                val p = Path().apply {
                    moveTo(w * 0.43f, h * 0.35f)
                    lineTo(w * 0.68f, h * 0.50f)
                    lineTo(w * 0.43f, h * 0.65f)
                    close()
                }
                drawPath(p, tint)
            }
            else -> {
                val p = Path().apply {
                    moveTo(w * 0.50f, h * 0.14f)
                    lineTo(w * 0.61f, h * 0.39f)
                    lineTo(w * 0.87f, h * 0.41f)
                    lineTo(w * 0.67f, h * 0.58f)
                    lineTo(w * 0.74f, h * 0.84f)
                    lineTo(w * 0.50f, h * 0.69f)
                    lineTo(w * 0.26f, h * 0.84f)
                    lineTo(w * 0.33f, h * 0.58f)
                    lineTo(w * 0.13f, h * 0.41f)
                    lineTo(w * 0.39f, h * 0.39f)
                    close()
                }
                drawPath(p, tint)
            }
        }
    }
}
