package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.example.ui.design.TaskIdsColors
import com.example.ui.navigation.AppScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ParentDashboardScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.RewardUnlockedScreen
import com.example.ui.screens.RewardsScreen
import com.example.ui.screens.ScreenTimeScreen
import com.example.ui.screens.TimerScreenV2
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                TaskIdsApp(viewModel)
            }
        }
    }
}

@Composable
private fun TaskIdsApp(viewModel: MainViewModel) {
    val screen by viewModel.currentScreen.collectAsState()

    if (screen !is AppScreen.Home) {
        BackHandler {
            when (screen) {
                is AppScreen.ScreenTime -> Unit
                else -> viewModel.navigateTo(AppScreen.Home)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(
                        TaskIdsColors.Navy800,
                        TaskIdsColors.Navy700,
                        TaskIdsColors.Navy900
                    )
                )
            )
    ) {
        when (val current = screen) {
            AppScreen.Home -> HomeScreen(viewModel)
            is AppScreen.Timer -> TimerScreenV2(viewModel, current.taskId)
            AppScreen.RewardUnlocked -> RewardUnlockedScreen(viewModel)
            AppScreen.Rewards -> RewardsScreen(viewModel)
            AppScreen.ScreenTime -> ScreenTimeScreen(viewModel)
            AppScreen.Parent -> ParentDashboardScreen(viewModel)
            AppScreen.Reports -> ReportsScreen(viewModel)
        }
    }
}
