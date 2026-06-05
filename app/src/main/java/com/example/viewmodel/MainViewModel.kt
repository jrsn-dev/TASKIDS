package com.example.viewmodel

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.TaskDatabase
import com.example.data.preferences.AppPreferencesRepository
import com.example.data.repository.TaskRepository
import com.example.model.DefaultTasks
import com.example.model.Task
import com.example.model.TaskStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

sealed class Screen {
    object TaskList : Screen()
    data class Timer(val taskId: Int) : Screen()
    object YouTubeReward : Screen()
    object YouTubePlayer : Screen()
    object Settings : Screen()
}

data class TimerState(
    val taskId: Int = 0,
    val totalSeconds: Int = 0,
    val remainingSeconds: Int = 0,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val progress: Float = 0f
)

class MainViewModel(
    private val repository: TaskRepository,
    private val prefs: AppPreferencesRepository
) : ViewModel() {

    // View Switching State
    private val _currentScreen = MutableStateFlow<Screen>(Screen.TaskList)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Tasks State
    val tasks: StateFlow<List<Task>> = repository.allTasks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current Task Details Screen or Active Timer Task
    private val _activeTask = MutableStateFlow<Task?>(null)
    val activeTask: StateFlow<Task?> = _activeTask.asStateFlow()

    // Task Timer State
    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()
    private var timerJob: Job? = null

    // YouTube Timer State
    private val _youtubeRemainingSeconds = MutableStateFlow(0)
    val youtubeRemainingSeconds: StateFlow<Int> = _youtubeRemainingSeconds.asStateFlow()
    private val _youtubeTotalSeconds = MutableStateFlow(0)
    val youtubeTotalSeconds: StateFlow<Int> = _youtubeTotalSeconds.asStateFlow()
    private val _youtubeTimerIsActive = MutableStateFlow(false)
    val youtubeTimerIsActive: StateFlow<Boolean> = _youtubeTimerIsActive.asStateFlow()
    private var youtubeTimerJob: Job? = null

    // Preferences exposed
    private val _soundEnabled = MutableStateFlow(prefs.soundEnabled)
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _youtubeConfigLimit = MutableStateFlow(prefs.youtubeTimeLimit)
    val youtubeConfigLimit: StateFlow<Int> = _youtubeConfigLimit.asStateFlow()

    private val _parentalPin = MutableStateFlow(prefs.parentalPin)
    val parentalPin: StateFlow<String> = _parentalPin.asStateFlow()

    init {
        // Feed and seed data on initial first turn if DB is empty
        viewModelScope.launch {
            repository.allTasks.take(1).collect { taskList ->
                if (taskList.isEmpty()) {
                    DefaultTasks.getDefaultTasks().forEach { task ->
                        repository.insertTask(task)
                    }
                }
            }
        }
    }

    // Screen navigation
    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
        
        // If navigating to YouTube player, start YouTube allowance timer
        if (screen is Screen.YouTubePlayer) {
            startYouTubeTimer()
        } else if (screen !is Screen.YouTubePlayer) {
            stopYouTubeTimer()
        }

        // If navigating out of timer screen, stop timer if it's not completed
        if (screen !is Screen.Timer) {
            stopTimer()
        }
    }

    // Task Management
    fun selectTask(task: Task) {
        _activeTask.value = task
        navigateTo(Screen.Timer(task.id))
        startTimer(task)
    }

    fun markTaskAsCompleted(task: Task) {
        viewModelScope.launch {
            val updated = task.copy(
                status = TaskStatus.COMPLETED,
                completedAt = System.currentTimeMillis()
            )
            repository.updateTask(updated)
            playAlertSound(ToneGenerator.TONE_PROP_ACK)
            
            // Check if all tasks are completed
            val list = tasks.value
            val otherTasksCompleted = list.filter { it.id != task.id }.all { it.status == TaskStatus.COMPLETED }
            
            if (otherTasksCompleted) {
                navigateTo(Screen.YouTubeReward)
            } else {
                navigateTo(Screen.TaskList)
            }
        }
    }

    fun updateTaskStatus(task: Task, status: TaskStatus) {
        viewModelScope.launch {
            repository.updateTask(task.copy(status = status))
        }
    }

    fun addTask(title: String, durationMinutes: Int, icon: String, description: String = "") {
        viewModelScope.launch {
            val list = tasks.value
            val nextOrder = (list.maxOfOrNull { it.orderIndex } ?: 0) + 1
            repository.insertTask(
                Task(
                    title = title,
                    durationMinutes = durationMinutes,
                    icon = icon,
                    description = description,
                    orderIndex = nextOrder,
                    status = TaskStatus.PENDING
                )
            )
        }
    }

    fun editTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun restartAllTasks() {
        viewModelScope.launch {
            val list = tasks.value
            list.forEach { task ->
                repository.updateTask(task.copy(status = TaskStatus.PENDING, completedAt = null))
            }
            navigateTo(Screen.TaskList)
        }
    }

    // Countdown Timer Logic
    private fun startTimer(task: Task) {
        // Set state as in progress
        viewModelScope.launch {
            repository.updateTask(task.copy(status = TaskStatus.IN_PROGRESS))
        }

        val totalSeconds = task.durationMinutes * 60
        _timerState.value = TimerState(
            taskId = task.id,
            totalSeconds = totalSeconds,
            remainingSeconds = totalSeconds,
            isRunning = true,
            isPaused = false,
            progress = 1.0f
        )

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timerState.value.remainingSeconds > 0) {
                delay(1000)
                if (_timerState.value.isRunning) {
                    val remaining = _timerState.value.remainingSeconds - 1
                    val progress = remaining.toFloat() / totalSeconds.toFloat()
                    _timerState.value = _timerState.value.copy(
                        remainingSeconds = remaining,
                        progress = progress
                    )
                }
            }
            onTimerFinished(task)
        }
    }

    fun pauseTimer() {
        _timerState.value = _timerState.value.copy(isRunning = false, isPaused = true)
    }

    fun resumeTimer() {
        _timerState.value = _timerState.value.copy(isRunning = true, isPaused = false)
    }

    fun extendTimer() {
        val current = _timerState.value
        val newTotal = current.totalSeconds + (5 * 60)
        val newRemaining = current.remainingSeconds + (5 * 60)
        val newProgress = newRemaining.toFloat() / newTotal.toFloat()
        
        _timerState.value = current.copy(
            totalSeconds = newTotal,
            remainingSeconds = newRemaining,
            progress = newProgress
        )
    }

    private fun stopTimer() {
        timerJob?.cancel()
        _timerState.value = TimerState()
    }

    private fun onTimerFinished(task: Task) {
        // Red alert sound, status OVERDUE
        viewModelScope.launch {
            repository.updateTask(task.copy(status = TaskStatus.OVERDUE))
        }
        _timerState.value = _timerState.value.copy(isRunning = false)
        playAlertSound(ToneGenerator.TONE_CDMA_HIGH_L)
    }

    // Parent Controlled YouTube Timer
    private fun startYouTubeTimer() {
        val seconds = prefs.youtubeTimeLimit * 60
        _youtubeTotalSeconds.value = seconds
        _youtubeRemainingSeconds.value = seconds
        _youtubeTimerIsActive.value = true

        youtubeTimerJob?.cancel()
        youtubeTimerJob = viewModelScope.launch {
            while (_youtubeRemainingSeconds.value > 0) {
                delay(1000)
                _youtubeRemainingSeconds.value = _youtubeRemainingSeconds.value - 1
            }
            onYouTubeTimerExpired()
        }
    }

    private fun stopYouTubeTimer() {
        youtubeTimerJob?.cancel()
        _youtubeTimerIsActive.value = false
    }

    private fun onYouTubeTimerExpired() {
        stopYouTubeTimer()
        playAlertSound(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK)
        navigateTo(Screen.TaskList)
    }

    // Preferences Actions
    fun setSoundEnabled(enabled: Boolean) {
        prefs.soundEnabled = enabled
        _soundEnabled.value = enabled
    }

    fun setYouTubeTimeLimit(minutes: Int) {
        prefs.youtubeTimeLimit = minutes
        _youtubeConfigLimit.value = minutes
    }

    fun updateParentalPin(pin: String) {
        if (pin.length == 4 && pin.all { it.isDigit() }) {
            prefs.parentalPin = pin
            _parentalPin.value = pin
        }
    }

    // Sound alert generator
    private fun playAlertSound(toneType: Int) {
        if (soundEnabled.value) {
            try {
                val toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
                toneGenerator.startTone(toneType, 400)
            } catch (e: Exception) {
                // Ignore audio play failures
            }
        }
    }
}

class MainViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            val database = TaskDatabase.getDatabase(context)
            val repository = TaskRepository(database.taskDao())
            val prefs = AppPreferencesRepository(context)
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository, prefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
