package com.example.ui.game

import android.graphics.BitmapFactory
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.model.Child

/**
 * TASKIDS 2D avatar.
 *
 * Avatars are loaded from assets through BitmapFactory instead of painterResource.
 * This prevents ResourceResolutionException from crashing the entire Home if a PNG
 * is ever malformed or incompatible with Android's drawable resource decoder.
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
        targetValue = if (celebrate) -10f else -4f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (celebrate) 360 else 1100),
            repeatMode = RepeatMode.Reverse
        ),
        label = "avatar-bob"
    )

    val assetName = when (child.avatarCharacter.uppercase()) {
        "GIRL" -> "avatar_girl_base.png"
        else -> "avatar_boy_base.png"
    }

    val context = LocalContext.current
    val bitmap = remember(assetName) {
        runCatching {
            context.assets.open(assetName).use { stream ->
                BitmapFactory.decodeStream(stream)?.asImageBitmap()
            }
        }.getOrNull()
    }

    Box(
        modifier = modifier.graphicsLayer { translationY = bob },
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = "Avatar de ${child.name}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        } else {
            SafeAvatarFallback(
                child = child,
                modifier = Modifier.fillMaxSize()
            )
        }

        AvatarAchievementOverlay(
            child = child,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(if (celebrate) 36.dp else 28.dp)
        )
    }
}

@Composable
private fun SafeAvatarFallback(
    child: Child,
    modifier: Modifier = Modifier
) {
    val outfit = when (child.avatarCharacter.uppercase()) {
        "GIRL" -> Color(0xFFFF7FAF)
        else -> Color(0xFF4198FF)
    }
    val skin = Color(0xFFFFC59E)
    val hair = Color(0xFF56311F)

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f

        drawCircle(
            color = skin,
            radius = w * 0.16f,
            center = Offset(cx, h * 0.26f)
        )
        drawCircle(
            color = hair,
            radius = w * 0.17f,
            center = Offset(cx, h * 0.22f)
        )
        drawCircle(
            color = skin,
            radius = w * 0.145f,
            center = Offset(cx, h * 0.275f)
        )

        drawRoundRect(
            color = outfit,
            topLeft = Offset(w * 0.34f, h * 0.39f),
            size = androidx.compose.ui.geometry.Size(w * 0.32f, h * 0.30f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.08f)
        )

        drawRoundRect(
            color = outfit,
            topLeft = Offset(w * 0.29f, h * 0.43f),
            size = androidx.compose.ui.geometry.Size(w * 0.08f, h * 0.26f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.04f)
        )
        drawRoundRect(
            color = outfit,
            topLeft = Offset(w * 0.63f, h * 0.43f),
            size = androidx.compose.ui.geometry.Size(w * 0.08f, h * 0.26f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.04f)
        )

        drawRoundRect(
            color = Color(0xFF46546E),
            topLeft = Offset(w * 0.40f, h * 0.67f),
            size = androidx.compose.ui.geometry.Size(w * 0.08f, h * 0.23f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.04f)
        )
        drawRoundRect(
            color = Color(0xFF46546E),
            topLeft = Offset(w * 0.52f, h * 0.67f),
            size = androidx.compose.ui.geometry.Size(w * 0.08f, h * 0.23f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.04f)
        )

        drawCircle(Color(0xFF2F241E), w * 0.015f, Offset(cx - w * 0.05f, h * 0.27f))
        drawCircle(Color(0xFF2F241E), w * 0.015f, Offset(cx + w * 0.05f, h * 0.27f))
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
        1 -> Color(0xFFCD7F32)
        2 -> Color(0xFFC0C0C0)
        3 -> Color(0xFFFFC928)
        else -> Color(0xFF70D6FF)
    }

    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val outer = size.minDimension * 0.48f
        drawCircle(Color.White.copy(alpha = 0.96f), outer)
        drawCircle(badgeColor, outer * 0.82f)

        val star = Path()
        repeat(10) { i ->
            val angle = Math.toRadians(-90.0 + i * 36.0)
            val radius = if (i % 2 == 0) outer * 0.62f else outer * 0.28f
            val x = cx + kotlin.math.cos(angle).toFloat() * radius
            val y = cy + kotlin.math.sin(angle).toFloat() * radius
            if (i == 0) star.moveTo(x, y) else star.lineTo(x, y)
        }
        star.close()
        drawPath(star, Color.White)

        if (tier >= 4) {
            drawCircle(Color(0xFFFF4D9D), outer * 0.10f, Offset(cx, cy))
        }
    }
}
