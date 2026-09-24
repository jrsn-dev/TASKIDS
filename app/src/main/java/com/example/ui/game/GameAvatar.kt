package com.example.ui.game

import android.graphics.BitmapFactory
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.Child

@Composable
fun GameAvatar(child: Child, modifier: Modifier = Modifier, celebrate: Boolean = false) {
    val transition = rememberInfiniteTransition(label = "avatar-idle")
    val bob by transition.animateFloat(0f, if (celebrate) -10f else -4f,
        infiniteRepeatable(tween(if (celebrate) 360 else 1100), RepeatMode.Reverse), label = "avatar-bob")
    val girl = child.avatarCharacter.uppercase() == "GIRL"
    val asset = if (girl) "avatar_girl.png" else "avatar_boy.png"
    val context = LocalContext.current
    val bitmap = remember(asset) {
        runCatching { context.assets.open(asset).use { BitmapFactory.decodeStream(it)?.asImageBitmap() } }.getOrNull()
    }
    Box(modifier.graphicsLayer { translationY = bob }, contentAlignment = Alignment.Center) {
        if (bitmap != null) Image(bitmap, "Avatar de ${child.name}", Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
        else Image(painterResource(if (girl) R.drawable.avatar_girl_base else R.drawable.avatar_boy_base),
            "Avatar de ${child.name}", Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
        if (child.totalStars >= 100 || child.currentStreak >= 3 || child.bestCombo >= 5 || child.totalXp >= 2500) {
            Image(painterResource(R.drawable.game_badge), "Conquista",
                Modifier.align(Alignment.TopEnd).size(if (celebrate) 36.dp else 28.dp))
        }
    }
}
