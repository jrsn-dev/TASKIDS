package com.taskids.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.taskids.app.ui.home.HomeScreen
import com.taskids.app.ui.parental.ParentScreen
import com.taskids.app.ui.player.ApprovedContentScreen
import com.taskids.app.ui.rewards.RewardsScreen
import com.taskids.app.ui.timer.TimerScreen
import com.taskids.app.viewmodel.MainViewModel
import com.taskids.app.viewmodel.Screen

@Composable
fun TaskidsApp(viewModel: MainViewModel) {
    val screen by viewModel.currentScreen.collectAsState()

    BackHandler(enabled = screen !is Screen.Home) {
        when (screen) {
            is Screen.Timer -> viewModel.leaveTimer()
            is Screen.Rewards, is Screen.Parents -> viewModel.navigateTo(Screen.Home)
            is Screen.ApprovedPlayer -> Unit
            is Screen.Home -> Unit
        }
    }

    when (val destination = screen) {
        is Screen.Home -> HomeScreen(viewModel)
        is Screen.Timer -> TimerScreen(viewModel, destination.taskId)
        is Screen.Rewards -> RewardsScreen(viewModel)
        is Screen.Parents -> ParentScreen(viewModel)
        is Screen.ApprovedPlayer -> ApprovedContentScreen(viewModel)
    }
}
