package com.example

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.ui.game.GameHomeScreen
import com.example.ui.game.GameMissionScreen
import com.example.ui.game.MissionCompleteScreen
import com.example.ui.navigation.AppScreen
import com.example.ui.screens.MissionsScreen
import com.example.ui.screens.ParentAccessScreen
import com.example.ui.screens.ParentDashboardScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.RewardsScreen
import com.example.ui.screens.ScreenTimeScreen
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
    val context = LocalContext.current
    val activity = context as? Activity

    LaunchedEffect(screen) {
        // The approved product design is mobile-first and portrait-oriented.
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
    }

    if (screen !is AppScreen.Home) {
        BackHandler {
            when (screen) {
                AppScreen.Parent -> viewModel.navigateTo(AppScreen.ParentAccess)
                AppScreen.ParentAccess -> viewModel.navigateTo(AppScreen.Home)
                AppScreen.Reports -> viewModel.navigateTo(AppScreen.Parent)
                is AppScreen.ScreenTime -> Unit
                else -> viewModel.navigateTo(AppScreen.Home)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7FAFF))
    ) {
        when (val current = screen) {
            AppScreen.Home -> GameHomeScreen(viewModel)
            AppScreen.Missions -> MissionsScreen(viewModel)
            is AppScreen.Timer -> GameMissionScreen(viewModel, current.taskId)
            AppScreen.RewardUnlocked -> MissionCompleteScreen(viewModel)
            AppScreen.Rewards -> RewardsScreen(viewModel)
            AppScreen.ScreenTime -> ScreenTimeScreen(viewModel)
            AppScreen.ParentAccess -> ParentAccessScreen(viewModel)
            AppScreen.Parent -> ParentDashboardScreen(viewModel)
            AppScreen.Reports -> ReportsScreen(viewModel)
        }
    }
}
