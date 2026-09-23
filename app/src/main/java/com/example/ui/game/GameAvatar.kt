package com.example.ui.game

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.Child

/**
 * Main TASKIDS avatar.
 *
 * The visual base is a transparent PNG character. Personalization fields remain
 * persisted on Child so hair/skin/outfit layers can be added as independent PNG
 * overlays without changing the profile schema again.
 */
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

    val avatarRes = when (child.avatarCharacter.uppercase()) {
        "GIRL" -> R.drawable.avatar_girl_base
        else -> R.drawable.avatar_boy_base
    }

    Box(
        modifier = modifier.graphicsLayer { translationY = bob },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(avatarRes),
            contentDescription = "Avatar de ${child.name}",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )

        AvatarAchievementOverlay(
            child = child,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(if (celebrate) 38.dp else 30.dp)
        )
    }
}

@Composable
private fun AvatarAchievementOverlay(
    child: Child,
    modifier: Modifier = Modifier
) {
    val tier = when {
        child.totalXp >= 5_000 -> 4
        child.totalXp >= 2_500 -> 3
        child.bestCombo >= 5 -> 2
        child.totalStars >= 100 || child.currentStreak >= 3 -> 1
        else -> 0
    }
    if (tier == 0) return

    val badgeColor = when (tier) {
        1 -> Color(0xFFCD7F32) // bronze
        2 -> Color(0xFFC0C0C0) // silver
        3 -> Color(0xFFFFC928) // gold
        else -> Color(0xFF70D6FF) // legendary
    }

    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val outer = size.minDimension * 0.48f
        drawCircle(Color.White.copy(alpha = 0.96f), outer)
        drawCircle(badgeColor, outer * 0.82f)

        val star = Path()
        val points = 10
        for (i in 0 until points) {
            val angle = Math.toRadians((-90.0 + i * 36.0))
            val radius = if (i % 2 == 0) outer * 0.62f else outer * 0.28f
            val x = cx + kotlin.math.cos(angle).toFloat() * radius
            val y = cy + kotlin.math.sin(angle).toFloat() * radius
            if (i == 0) star.moveTo(x, y) else star.lineTo(x, y)
        }
        star.close()
        drawPath(star, Color.White)

        if (tier >= 4) {
            drawCircle(
                Color(0xFFFF4D9D),
                radius = outer * 0.10f,
                center = Offset(cx, cy)
            )
        }
    }
}
