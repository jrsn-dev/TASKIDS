package com.example.ui.game

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.example.ui.design.TaskIdsColors

/** Raster reward art follows the illustrated TASKIDS visual system. */
@Composable
fun RewardArt(type: String, modifier: Modifier = Modifier) {
    val asset = when (type.uppercase()) {
        "SCREEN_TIME" -> "reward_game.png"
        "MOVIE" -> "reward_movie.png"
        else -> "reward_gift.png"
    }
    val context = LocalContext.current
    val bitmap = remember(asset) {
        runCatching { context.assets.open(asset).use { BitmapFactory.decodeStream(it)?.asImageBitmap() } }.getOrNull()
    }
    if (bitmap != null) {
        Image(bitmap, contentDescription = null, modifier = modifier, contentScale = ContentScale.Fit)
    } else {
        RewardVectorIcon(type, modifier, TaskIdsColors.Blue)
    }
}
