package com.example.ui.game

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R

/** Pre-rendered PNG icons are shared by missions, the parental panel and the library. */
@Composable
fun GameIcon(key: String, modifier: Modifier = Modifier, @Suppress("UNUSED_PARAMETER") tint: Color = Color.White) {
    val image = when (key.uppercase()) {
        "BOOK" -> R.drawable.game_book
        "BATH" -> R.drawable.game_bath
        "MEAL" -> R.drawable.game_meal
        "HOMEWORK" -> R.drawable.game_homework
        "TOOTH" -> R.drawable.game_tooth
        "TOYS" -> R.drawable.game_toys
        "CLEAN" -> R.drawable.game_clean
        "SPORT" -> R.drawable.game_sport
        else -> R.drawable.game_generic
    }
    Image(painterResource(image), contentDescription = null, modifier = modifier, contentScale = ContentScale.Fit)
}
