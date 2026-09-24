package com.example.ui.game

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.R

@Composable
fun MascotStar(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val bitmap = remember {
        runCatching { context.assets.open("mascot_star.png").use { BitmapFactory.decodeStream(it)?.asImageBitmap() } }.getOrNull()
    }
    if (bitmap != null) Image(bitmap, "Estrela sorridente", modifier, contentScale = ContentScale.Fit)
    else Image(painterResource(R.drawable.game_badge), "Estrela", modifier, contentScale = ContentScale.Fit)
}
