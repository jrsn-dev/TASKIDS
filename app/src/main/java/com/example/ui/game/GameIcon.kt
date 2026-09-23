package com.example.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.ui.design.TaskIdsColors

@Composable
fun GameIcon(
    key: String,
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val s = minOf(w, h)
        val stroke = s * 0.075f

        when (key.uppercase()) {
            "BOOK" -> {
                drawRoundRect(
                    color = tint.copy(alpha = 0.18f),
                    topLeft = Offset(w * 0.12f, h * 0.18f),
                    size = Size(w * 0.76f, h * 0.68f),
                    cornerRadius = CornerRadius(s * 0.08f)
                )
                drawLine(tint, Offset(w * 0.5f, h * 0.24f), Offset(w * 0.5f, h * 0.80f), stroke)
                drawLine(tint, Offset(w * 0.20f, h * 0.28f), Offset(w * 0.43f, h * 0.34f), stroke * 0.55f, StrokeCap.Round)
                drawLine(tint, Offset(w * 0.57f, h * 0.34f), Offset(w * 0.80f, h * 0.28f), stroke * 0.55f, StrokeCap.Round)
            }
            "BATH" -> {
                drawRoundRect(
                    tint.copy(alpha = 0.22f),
                    Offset(w * 0.12f, h * 0.48f),
                    Size(w * 0.76f, h * 0.28f),
                    CornerRadius(s * 0.10f)
                )
                drawLine(tint, Offset(w * 0.18f, h * 0.76f), Offset(w * 0.14f, h * 0.88f), stroke, StrokeCap.Round)
                drawLine(tint, Offset(w * 0.82f, h * 0.76f), Offset(w * 0.86f, h * 0.88f), stroke, StrokeCap.Round)
                drawCircle(tint.copy(alpha = 0.9f), s * 0.08f, Offset(w * 0.30f, h * 0.34f))
                drawCircle(tint.copy(alpha = 0.65f), s * 0.055f, Offset(w * 0.48f, h * 0.26f))
                drawCircle(tint.copy(alpha = 0.45f), s * 0.045f, Offset(w * 0.62f, h * 0.36f))
            }
            "MEAL" -> {
                drawCircle(tint.copy(alpha = 0.17f), s * 0.35f, Offset(w * 0.5f, h * 0.52f))
                drawCircle(tint, s * 0.29f, Offset(w * 0.5f, h * 0.52f), style = Stroke(stroke))
                drawLine(tint, Offset(w * 0.17f, h * 0.20f), Offset(w * 0.17f, h * 0.82f), stroke * 0.7f, StrokeCap.Round)
                drawLine(tint, Offset(w * 0.83f, h * 0.20f), Offset(w * 0.83f, h * 0.82f), stroke * 0.7f, StrokeCap.Round)
            }
            "HOMEWORK" -> {
                drawRoundRect(
                    tint.copy(alpha = 0.18f),
                    Offset(w * 0.18f, h * 0.16f),
                    Size(w * 0.56f, h * 0.70f),
                    CornerRadius(s * 0.06f)
                )
                repeat(3) { i ->
                    val y = h * (0.34f + i * 0.15f)
                    drawLine(tint, Offset(w * 0.28f, y), Offset(w * 0.62f, y), stroke * 0.45f, StrokeCap.Round)
                }
                drawLine(TaskIdsColors.Yellow, Offset(w * 0.72f, h * 0.22f), Offset(w * 0.46f, h * 0.72f), stroke, StrokeCap.Round)
            }
            "TOOTH" -> {
                val p = Path().apply {
                    moveTo(w * 0.30f, h * 0.18f)
                    cubicTo(w * 0.18f, h * 0.30f, w * 0.25f, h * 0.58f, w * 0.36f, h * 0.82f)
                    cubicTo(w * 0.41f, h * 0.93f, w * 0.48f, h * 0.72f, w * 0.50f, h * 0.62f)
                    cubicTo(w * 0.55f, h * 0.72f, w * 0.60f, h * 0.93f, w * 0.66f, h * 0.82f)
                    cubicTo(w * 0.78f, h * 0.58f, w * 0.82f, h * 0.30f, w * 0.70f, h * 0.18f)
                    cubicTo(w * 0.58f, h * 0.08f, w * 0.48f, h * 0.18f, w * 0.30f, h * 0.18f)
                    close()
                }
                drawPath(p, tint.copy(alpha = 0.20f))
                drawPath(p, tint, style = Stroke(stroke))
            }
            "TOYS" -> {
                drawRoundRect(tint.copy(alpha = 0.18f), Offset(w * 0.14f, h * 0.48f), Size(w * 0.72f, h * 0.34f), CornerRadius(s * 0.08f))
                drawCircle(tint, s * 0.10f, Offset(w * 0.35f, h * 0.38f), style = Stroke(stroke * 0.8f))
                drawCircle(tint, s * 0.10f, Offset(w * 0.65f, h * 0.38f), style = Stroke(stroke * 0.8f))
                drawLine(tint, Offset(w * 0.35f, h * 0.38f), Offset(w * 0.65f, h * 0.38f), stroke * 0.7f, StrokeCap.Round)
            }
            "CLEAN" -> {
                drawLine(tint, Offset(w * 0.35f, h * 0.18f), Offset(w * 0.62f, h * 0.78f), stroke, StrokeCap.Round)
                drawRoundRect(tint.copy(alpha = 0.22f), Offset(w * 0.44f, h * 0.66f), Size(w * 0.38f, h * 0.18f), CornerRadius(s * 0.05f))
            }
            "SPORT" -> {
                drawCircle(tint.copy(alpha = 0.15f), s * 0.33f, Offset(w * 0.5f, h * 0.5f))
                drawCircle(tint, s * 0.33f, Offset(w * 0.5f, h * 0.5f), style = Stroke(stroke))
                drawLine(tint, Offset(w * 0.25f, h * 0.5f), Offset(w * 0.75f, h * 0.5f), stroke * 0.55f)
                drawLine(tint, Offset(w * 0.5f, h * 0.25f), Offset(w * 0.5f, h * 0.75f), stroke * 0.55f)
            }
            else -> {
                drawRoundRect(
                    tint.copy(alpha = 0.18f),
                    Offset(w * 0.18f, h * 0.18f),
                    Size(w * 0.64f, h * 0.64f),
                    CornerRadius(s * 0.16f)
                )
                drawCircle(tint, s * 0.12f, Offset(w * 0.5f, h * 0.5f))
            }
        }
    }
}
