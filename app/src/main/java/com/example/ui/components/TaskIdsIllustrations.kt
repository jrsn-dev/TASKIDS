package com.example.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale

object TaskIdsAssets {
    const val LOGO = "ui/taskids_logo.png"
    const val STAR = "ui/star_mascot.png"
    const val GIFT = "ui/gift.png"
    const val CROWN = "ui/crown.png"
    const val REWARD_GAME = "ui/reward_game.png"
    const val REWARD_MOVIE = "ui/reward_movie.png"
    const val LIBRARY_COLORING = "ui/library_coloring.png"
    const val LIBRARY_EDUCATION = "ui/library_education.png"
    const val LIBRARY_MEMORY = "ui/library_memory.png"
    const val LIBRARY_WORDS = "ui/library_words.png"

    fun task(iconKey: String): String = when (iconKey.uppercase()) {
        "BOOK" -> "ui/mission_book.png"
        "BATH" -> "ui/mission_bath.png"
        "MEAL" -> "ui/mission_meal.png"
        "HOMEWORK" -> "ui/mission_homework.png"
        "TOOTH" -> "ui/mission_tooth.png"
        "TOYS", "CLEAN" -> "ui/mission_toys.png"
        "SPORT" -> "ui/mission_sport.png"
        else -> "ui/star_mascot.png"
    }
}

@Composable
fun TaskIdsLogo(
    modifier: Modifier = Modifier
) {
    AssetBitmapImage(
        assetName = TaskIdsAssets.LOGO,
        contentDescription = "TASKIDS",
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}

@Composable
fun TaskIllustration(
    iconKey: String,
    modifier: Modifier = Modifier
) {
    AssetBitmapImage(
        assetName = TaskIdsAssets.task(iconKey),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}

@Composable
fun RewardIllustration(
    rewardType: String,
    modifier: Modifier = Modifier
) {
    AssetBitmapImage(
        assetName = if (rewardType == "SCREEN_TIME") TaskIdsAssets.REWARD_GAME else TaskIdsAssets.REWARD_MOVIE,
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}
