package com.example.ui.game

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R

/** Raster reward illustrations used by compact buttons and TV cards. */
@Composable
fun RewardVectorIcon(type: String, modifier: Modifier = Modifier, @Suppress("UNUSED_PARAMETER") tint: Color = Color.White) {
    val image = when (type.uppercase()) {
        "SCREEN_TIME" -> R.drawable.game_sport
        "MOVIE" -> R.drawable.game_toys
        else -> R.drawable.game_generic
    }
    Image(painterResource(image), contentDescription = null, modifier = modifier, contentScale = ContentScale.Fit)
}
