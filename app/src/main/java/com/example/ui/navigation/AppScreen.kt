package com.example.ui.navigation

sealed class AppScreen {
    data object Home : AppScreen()
    data class Timer(val taskId: Int) : AppScreen()
    data object RewardUnlocked : AppScreen()
    data object Rewards : AppScreen()
    data object ScreenTime : AppScreen()
    data object Parent : AppScreen()
    data object Reports : AppScreen()
}
