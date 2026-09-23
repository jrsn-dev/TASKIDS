package com.taskids.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.setContent
import androidx.activity.viewModels
import com.taskids.app.ui.TaskidsApp
import com.taskids.app.ui.theme.TaskidsTheme
import com.taskids.app.viewmodel.MainViewModel
import com.taskids.app.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels { MainViewModelFactory(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskidsTheme {
                TaskidsApp(viewModel)
            }
        }
    }
}
