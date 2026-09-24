package com.example.ui.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.game.GameThemeKey

/** Shared artwork from the TASKIDS visual direction. Content sits above this image. */
@Composable
fun GameWorldBackground(@Suppress("UNUSED_PARAMETER") theme: GameThemeKey, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier) {
        val portrait = maxHeight > maxWidth
        Image(
            painter = painterResource(if (portrait) R.drawable.sky_scene_portrait else R.drawable.sky_scene_landscape),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
