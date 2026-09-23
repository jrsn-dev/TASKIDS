package com.example.ui.game

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import com.example.model.Child

@Composable
fun GameAvatar(
    child: Child,
    modifier: Modifier = Modifier,
    celebrate: Boolean = false
) {
    val transition = rememberInfiniteTransition(label = "avatar-idle")
    val bob by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (celebrate) -12f else -5f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (celebrate) 360 else 1100),
            repeatMode = RepeatMode.Reverse
        ),
        label = "avatar-bob"
    )

    val skin = listOf(
        Color(0xFFFFD5B8),
        Color(0xFFF3BD91),
        Color(0xFFD99568),
        Color(0xFFAD6B45),
        Color(0xFF7A472E)
    )[child.avatarSkinTone.coerceIn(0, 4)]

    val hair = listOf(
        Color(0xFF3C2A23),
        Color(0xFF6B4327),
        Color(0xFF1C1B20),
        Color(0xFFC58D3E)
    )[child.avatarHairColor.coerceIn(0, 3)]

    val outfit = listOf(
        Color(0xFF2F80ED),
        Color(0xFF27AE60),
        Color(0xFF9B51E0),
        Color(0xFFF2994A),
        Color(0xFFEB5757)
    )[child.avatarOutfitColor.coerceIn(0, 4)]

    Canvas(
        modifier = modifier.graphicsLayer { translationY = bob }
    ) {
        val w = size.width
        val h = size.height
        val s = minOf(w, h)

        // shadow
        drawOval(
            Color.Black.copy(alpha = 0.18f),
            topLeft = Offset(w * 0.26f, h * 0.88f),
            size = Size(w * 0.48f, h * 0.07f)
        )

        // legs
        drawRoundRect(outfit.copy(alpha = 0.78f), Offset(w * 0.34f, h * 0.68f), Size(w * 0.12f, h * 0.20f), CornerRadius(s * 0.05f))
        drawRoundRect(outfit.copy(alpha = 0.78f), Offset(w * 0.54f, h * 0.68f), Size(w * 0.12f, h * 0.20f), CornerRadius(s * 0.05f))

        // shoes
        drawRoundRect(Color(0xFF24324A), Offset(w * 0.29f, h * 0.84f), Size(w * 0.20f, h * 0.08f), CornerRadius(s * 0.04f))
        drawRoundRect(Color(0xFF24324A), Offset(w * 0.51f, h * 0.84f), Size(w * 0.20f, h * 0.08f), CornerRadius(s * 0.04f))

        // body
        drawRoundRect(
            outfit,
            Offset(w * 0.27f, h * 0.46f),
            Size(w * 0.46f, h * 0.32f),
            CornerRadius(s * 0.12f)
        )

        // arms
        val armY = if (celebrate) h * 0.40f else h * 0.54f
        drawLine(skin, Offset(w * 0.30f, h * 0.53f), Offset(w * 0.12f, armY), s * 0.09f)
        drawLine(skin, Offset(w * 0.70f, h * 0.53f), Offset(w * 0.88f, armY), s * 0.09f)

        // neck
        drawRoundRect(skin, Offset(w * 0.44f, h * 0.39f), Size(w * 0.12f, h * 0.12f), CornerRadius(s * 0.05f))

        // head
        drawCircle(skin, s * 0.22f, Offset(w * 0.50f, h * 0.28f))

        // ears
        drawCircle(skin, s * 0.055f, Offset(w * 0.27f, h * 0.30f))
        drawCircle(skin, s * 0.055f, Offset(w * 0.73f, h * 0.30f))

        // hair variants
        when (child.avatarHairStyle % 4) {
            0 -> {
                drawArc(hair, 195f, 150f, true, Offset(w * 0.28f, h * 0.05f), Size(w * 0.44f, h * 0.34f))
                drawCircle(hair, s * 0.08f, Offset(w * 0.40f, h * 0.09f))
                drawCircle(hair, s * 0.08f, Offset(w * 0.52f, h * 0.08f))
                drawCircle(hair, s * 0.07f, Offset(w * 0.62f, h * 0.11f))
            }
            1 -> {
                drawArc(hair, 190f, 160f, true, Offset(w * 0.27f, h * 0.05f), Size(w * 0.46f, h * 0.35f))
            }
            2 -> {
                repeat(5) { i ->
                    drawCircle(hair, s * 0.07f, Offset(w * (0.34f + i * 0.08f), h * (0.10f + (i % 2) * 0.025f)))
                }
                drawArc(hair, 200f, 140f, true, Offset(w * 0.28f, h * 0.08f), Size(w * 0.44f, h * 0.29f))
            }
            else -> {
                drawArc(hair, 185f, 170f, true, Offset(w * 0.26f, h * 0.04f), Size(w * 0.48f, h * 0.37f))
                drawRoundRect(hair, Offset(w * 0.25f, h * 0.20f), Size(w * 0.09f, h * 0.25f), CornerRadius(s * 0.04f))
                drawRoundRect(hair, Offset(w * 0.66f, h * 0.20f), Size(w * 0.09f, h * 0.25f), CornerRadius(s * 0.04f))
            }
        }

        // eyes
        drawCircle(Color(0xFF18243B), s * 0.025f, Offset(w * 0.42f, h * 0.29f))
        drawCircle(Color(0xFF18243B), s * 0.025f, Offset(w * 0.58f, h * 0.29f))

        // smile
        val smile = Path().apply {
            moveTo(w * 0.43f, h * 0.35f)
            quadraticBezierTo(w * 0.50f, h * 0.40f, w * 0.57f, h * 0.35f)
        }
        drawPath(smile, Color(0xFF7C3E3E), style = Stroke(s * 0.014f))

        // TASKIDS chest badge
        drawCircle(Color.White.copy(alpha = 0.92f), s * 0.055f, Offset(w * 0.50f, h * 0.57f))
        val star = Path().apply {
            val cx = w * 0.50f
            val cy = h * 0.57f
            moveTo(cx, cy - s * 0.035f)
            lineTo(cx + s * 0.012f, cy - s * 0.010f)
            lineTo(cx + s * 0.040f, cy - s * 0.006f)
            lineTo(cx + s * 0.020f, cy + s * 0.012f)
            lineTo(cx + s * 0.026f, cy + s * 0.040f)
            lineTo(cx, cy + s * 0.025f)
            lineTo(cx - s * 0.026f, cy + s * 0.040f)
            lineTo(cx - s * 0.020f, cy + s * 0.012f)
            lineTo(cx - s * 0.040f, cy - s * 0.006f)
            lineTo(cx - s * 0.012f, cy - s * 0.010f)
            close()
        }
        drawPath(star, Color(0xFFF2C94C))
    }
}
