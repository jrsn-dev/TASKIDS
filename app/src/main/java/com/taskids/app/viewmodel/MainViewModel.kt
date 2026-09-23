package com.taskids.app.viewmodel

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.taskids.app.data.local.TaskDatabase
import com.taskids.app.data.preferences.AppPreferencesRepository
import com.taskids.app.data.preferences.PersistedTimer
import com.taskids.app.data.preferences.PinVerificationResult
import com.taskids.app.data.repository.KidsRepository
import com.taskids.app.domain.model.AchievementCatalog
import com.taskids.app.domain.model.AchievementDefinition
import com.taskids.app.domain.model.ApprovedContent
import com.taskids.app.domain.model.ChildProfile
import com.taskids.app.domain.model.DailyReport
import com.taskids.app.domain.model.Reward
import com.taskids.app.domain.model.RewardType
import com.taskids.app.domain.model.RoutinePeriod
import com.taskids.app.domain.model.Task
import com.taskids.app.domain.model.TaskExecution
import com.taskids.app.domain.model.TaskStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

sealed class Screen {
    data object Home : Screen()
    data class Timer(val taskId: Int) : Screen()
    data object Rewards : Screen()
    data object Parents : Screen()
    data object ApprovedPlayer : Screen()
}

data class TimerState(
    val taskId: Int = 0,
    val executionId: Long = 0,
    val totalSeconds: Int = 0,
    val remainingSeconds: Int = 0,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false
) {
    val progress: Float
        get() = if (totalSeconds <= 0) 0f else (remainingSeconds.toFloat() / totalSeconds).coerceIn(0f, 1f)
}

data class ParentDashboardUiState(
    val completedLast7Days: Int = 0,
    val starsLast7Days: Int = 0,
    val streakDays: Int = 0,
    val averageMinutes: Int = 0,
    val dailyReports: List<DailyReport> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(
    private val repository: KidsRepository,
    private val prefs: AppPreferencesRepository
) : ViewModel() {

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _currentChildId = MutableStateFlow(prefs.currentChildId)
    val currentChildId: StateFlow<Int> = _currentChildId.asStateFlow()

    val children: StateFlow<List<ChildProfile>> = repository.observeChildren()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val currentChild: StateFlow<ChildProfile?> = combine(children, _currentChildId) { profiles, id ->
        profiles.firstOrNull { it.id == id } ?: profiles.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val tasks: StateFlow<List<Task>> = _currentChildId
        .flatMapLatest(repository::observeTasks)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val todayTasks: StateFlow<List<Task>> = tasks
        .map { list ->
            val day = KidsRepository.dayIndex()
            list.filter { it.isScheduledFor(day) }
                .sortedWith(compareBy<Task> { it.routinePeriod.ordinal }.thenBy { it.orderIndex })
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val rewards: StateFlow<List<Reward>> = _currentChildId
        .flatMapLatest(repository::observeRewards)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val history: StateFlow<List<TaskExecution>> = _currentChildId
        .flatMapLatest(repository::observeHistory)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val unlocks = _currentChildId
        .flatMapLatest(repository::observeAchievementUnlocks)

    val achievements: StateFlow<List<AchievementDefinition>> = unlocks
        .map { items ->
            val unlockedIds = items.map { it.achievementId }.toSet()
            AchievementCatalog.definitions.map { it.copy(unlocked = it.id in unlockedIds) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AchievementCatalog.definitions)

    val approvedContent: StateFlow<List<ApprovedContent>> = repository.observeApprovedContent()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val parentDashboard: StateFlow<ParentDashboardUiState> = history
        .map(::buildDashboard)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ParentDashboardUiState())

    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()
    private var timerJob: Job? = null
    private var timerEndAtMillis: Long = 0L

    private val _lastEarnedStars = MutableStateFlow(0)
    val lastEarnedStars: StateFlow<Int> = _lastEarnedStars.asStateFlow()

    private val _soundEnabled = MutableStateFlow(prefs.soundEnabled)
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _pinFeedback = MutableStateFlow<String?>(null)
    val pinFeedback: StateFlow<String?> = _pinFeedback.asStateFlow()

    private val _pinConfigured = MutableStateFlow(prefs.isParentalPinConfigured)
    val pinConfigured: StateFlow<Boolean> = _pinConfigured.asStateFlow()

    private val _screenTimeRemainingSeconds = MutableStateFlow(0)
    val screenTimeRemainingSeconds: StateFlow<Int> = _screenTimeRemainingSeconds.asStateFlow()
    private var screenTimeJob: Job? = null

    init {
        viewModelScope.launch {
            repository.seedIfNeeded()
            normalizeSelectedChild()
            resetForNewDayIfNeeded()
            restoreActiveTimer()
        }
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun selectChild(childId: Int) {
        if (_currentChildId.value == childId) return
        if (_timerState.value.taskId != 0) return
        pauseTimerIfNeeded()
        prefs.currentChildId = childId
        _currentChildId.value = childId
        _currentScreen.value = Screen.Home
    }

    fun startTask(task: Task) {
        if (!task.isEnabled || task.status == TaskStatus.COMPLETED) return
        viewModelScope.launch {
            val executionId = repository.startTask(task)
            val total = task.durationMinutes.coerceAtLeast(1) * 60
            timerEndAtMillis = System.currentTimeMillis() + total * 1_000L
            _timerState.value = TimerState(
                taskId = task.id,
                executionId = executionId,
                totalSeconds = total,
                remainingSeconds = total,
                isRunning = true,
                isPaused = false
            )
            persistTimer()
            _currentScreen.value = Screen.Timer(task.id)
            runTimer()
        }
    }

    fun pauseTimer() {
        val state = _timerState.value
        if (!state.isRunning) return
        val remaining = calculateRemaining()
        timerJob?.cancel()
        _timerState.value = state.copy(
            remainingSeconds = remaining,
            isRunning = false,
            isPaused = true
        )
        persistTimer()
    }

    fun resumeTimer() {
        val state = _timerState.value
        if (state.taskId == 0 || state.isRunning || state.remainingSeconds <= 0) return
        timerEndAtMillis = System.currentTimeMillis() + state.remainingSeconds * 1_000L
        _timerState.value = state.copy(isRunning = true, isPaused = false)
        persistTimer()
        runTimer()
    }

    fun extendTimer(minutes: Int = 5) {
        val state = _timerState.value
        if (state.taskId == 0) return
        val extra = minutes.coerceIn(1, 30) * 60
        if (state.isRunning) timerEndAtMillis += extra * 1_000L
        _timerState.value = state.copy(
            totalSeconds = state.totalSeconds + extra,
            remainingSeconds = state.remainingSeconds + extra
        )
        persistTimer()
    }

    fun leaveTimer() {
        pauseTimer()
        _currentScreen.value = Screen.Home
    }

    fun completeCurrentTask() {
        val state = _timerState.value
        if (state.taskId == 0 || state.executionId == 0L) return
        timerJob?.cancel()
        viewModelScope.launch {
            val remaining = if (state.isRunning) calculateRemaining() else state.remainingSeconds
            val elapsed = (state.totalSeconds - remaining).coerceAtLeast(0)
            val result = repository.finishTask(
                taskId = state.taskId,
                executionId = state.executionId,
                status = TaskStatus.COMPLETED,
                durationSeconds = elapsed
            )
            _lastEarnedStars.value = result.starsEarned
            clearTimer()
            playSound(ToneGenerator.TONE_PROP_ACK)
            _currentScreen.value = if (result.allScheduledTasksCompleted) Screen.Rewards else Screen.Home
        }
    }

    fun restartToday() {
        viewModelScope.launch {
            repository.restartChildTasks(_currentChildId.value)
            clearTimer()
            _currentScreen.value = Screen.Home
        }
    }

    fun redeemReward(reward: Reward) {
        viewModelScope.launch {
            val result = repository.redeemReward(reward.id)
            if (!result.success) return@launch
            if (reward.type == RewardType.SCREEN_TIME && reward.valueMinutes > 0) {
                startScreenTime(reward.valueMinutes)
                _currentScreen.value = Screen.ApprovedPlayer
            } else {
                _currentScreen.value = Screen.Home
            }
        }
    }

    fun closeApprovedPlayer() {
        screenTimeJob?.cancel()
        _screenTimeRemainingSeconds.value = 0
        _currentScreen.value = Screen.Home
    }

    fun verifyParentPin(pin: String): PinVerificationResult {
        val result = prefs.verifyPin(pin)
        _pinFeedback.value = when {
            result.success -> null
            result.lockedForMillis > 0 ->
                "Muitas tentativas. Tente novamente em ${((result.lockedForMillis + 999) / 1000)}s."
            else -> "PIN incorreto."
        }
        if (result.success) _currentScreen.value = Screen.Parents
        return result
    }

    fun clearPinFeedback() {
        _pinFeedback.value = null
    }

    fun updateParentalPin(pin: String): Boolean {
        val updated = prefs.updateParentalPin(pin)
        if (updated) {
            _pinConfigured.value = true
            _currentScreen.value = Screen.Parents
        }
        _pinFeedback.value = if (updated) "PIN atualizado com segurança." else "Use exatamente 4 números."
        return updated
    }

    fun setSoundEnabled(enabled: Boolean) {
        prefs.soundEnabled = enabled
        _soundEnabled.value = enabled
    }

    fun addTask(
        title: String,
        description: String,
        durationMinutes: Int,
        icon: String,
        rewardPoints: Int,
        recurrenceMask: Int,
        routinePeriod: RoutinePeriod
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addTask(
                Task(
                    childId = _currentChildId.value,
                    title = title.trim(),
                    description = description.trim(),
                    durationMinutes = durationMinutes.coerceIn(1, 240),
                    icon = icon,
                    orderIndex = 0,
                    rewardPoints = rewardPoints.coerceIn(1, 100),
                    recurrenceMask = recurrenceMask.coerceIn(1, Task.EVERY_DAY),
                    routinePeriod = routinePeriod
                )
            )
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch { repository.updateTask(task) }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch { repository.deleteTask(task) }
    }

    fun toggleTask(task: Task) {
        updateTask(task.copy(isEnabled = !task.isEnabled))
    }

    fun addChild(name: String, age: Int, avatar: String, colorHex: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val id = repository.addChild(
                ChildProfile(
                    name = name.trim(),
                    age = age.coerceIn(3, 17),
                    avatar = avatar,
                    accentColorHex = colorHex
                )
            )
            selectChild(id)
            _currentScreen.value = Screen.Parents
        }
    }

    fun updateChild(profile: ChildProfile) {
        viewModelScope.launch { repository.updateChild(profile) }
    }

    fun addReward(title: String, icon: String, cost: Int, type: RewardType, minutes: Int) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addReward(
                Reward(
                    childId = _currentChildId.value,
                    title = title.trim(),
                    icon = icon,
                    costStars = cost.coerceIn(1, 10_000),
                    type = type,
                    valueMinutes = if (type == RewardType.SCREEN_TIME) minutes.coerceIn(5, 120) else 0
                )
            )
        }
    }

    fun deleteReward(reward: Reward) {
        viewModelScope.launch { repository.deleteReward(reward) }
    }

    fun addApprovedContent(title: String, urlOrId: String, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            onResult(repository.addApprovedContent(title, urlOrId))
        }
    }

    fun deleteApprovedContent(content: ApprovedContent) {
        viewModelScope.launch { repository.deleteApprovedContent(content) }
    }

    private fun runTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timerState.value.isRunning) {
                val remaining = calculateRemaining()
                _timerState.value = _timerState.value.copy(remainingSeconds = remaining)
                if (remaining <= 0) {
                    onTimerExpired()
                    break
                }
                delay(500)
            }
        }
    }

    private suspend fun onTimerExpired() {
        val state = _timerState.value
        if (state.taskId == 0 || state.executionId == 0L) return
        repository.finishTask(
            taskId = state.taskId,
            executionId = state.executionId,
            status = TaskStatus.OVERDUE,
            durationSeconds = state.totalSeconds
        )
        _timerState.value = state.copy(remainingSeconds = 0, isRunning = false, isPaused = false)
        prefs.clearTimer()
        playSound(ToneGenerator.TONE_CDMA_HIGH_L)
    }

    private fun calculateRemaining(): Int {
        if (timerEndAtMillis <= 0L) return _timerState.value.remainingSeconds
        return (((timerEndAtMillis - System.currentTimeMillis()).coerceAtLeast(0L) + 999L) / 1_000L).toInt()
    }

    private fun persistTimer() {
        val state = _timerState.value
        if (state.taskId == 0 || state.executionId == 0L) return
        prefs.saveTimer(
            PersistedTimer(
                taskId = state.taskId,
                executionId = state.executionId,
                totalSeconds = state.totalSeconds,
                endAtMillis = timerEndAtMillis,
                pausedRemainingSeconds = state.remainingSeconds,
                running = state.isRunning
            )
        )
    }

    private suspend fun restoreActiveTimer() {
        val persisted = prefs.restoreTimer() ?: return
        val task = repository.getTask(persisted.taskId) ?: run {
            prefs.clearTimer()
            return
        }
        selectChildWithoutNavigation(task.childId)

        val remaining = if (persisted.running) {
            (((persisted.endAtMillis - System.currentTimeMillis()).coerceAtLeast(0L) + 999L) / 1_000L).toInt()
        } else persisted.pausedRemainingSeconds

        timerEndAtMillis = persisted.endAtMillis
        _timerState.value = TimerState(
            taskId = persisted.taskId,
            executionId = persisted.executionId,
            totalSeconds = persisted.totalSeconds,
            remainingSeconds = remaining,
            isRunning = persisted.running && remaining > 0,
            isPaused = !persisted.running && remaining > 0
        )
        _currentScreen.value = Screen.Timer(task.id)

        if (remaining <= 0) {
            onTimerExpired()
        } else if (persisted.running) {
            runTimer()
        }
    }

    private fun pauseTimerIfNeeded() {
        if (_timerState.value.isRunning) pauseTimer()
    }

    private fun clearTimer() {
        timerJob?.cancel()
        timerEndAtMillis = 0L
        _timerState.value = TimerState()
        prefs.clearTimer()
    }

    private suspend fun normalizeSelectedChild() {
        val profiles = repository.getChildren()
        if (profiles.isNotEmpty() && profiles.none { it.id == _currentChildId.value }) {
            selectChildWithoutNavigation(profiles.first().id)
        }
    }

    private fun selectChildWithoutNavigation(childId: Int) {
        prefs.currentChildId = childId
        _currentChildId.value = childId
    }

    private suspend fun resetForNewDayIfNeeded() {
        val today = KidsRepository.dateKey()
        if (prefs.lastDailyResetDate != today) {
            repository.resetDailyStatuses()
            prefs.lastDailyResetDate = today
        }
    }

    private fun startScreenTime(minutes: Int) {
        screenTimeJob?.cancel()
        _screenTimeRemainingSeconds.value = minutes * 60
        screenTimeJob = viewModelScope.launch {
            while (_screenTimeRemainingSeconds.value > 0) {
                delay(1_000)
                _screenTimeRemainingSeconds.value =
                    (_screenTimeRemainingSeconds.value - 1).coerceAtLeast(0)
            }
            _currentScreen.value = Screen.Home
        }
    }

    private fun buildDashboard(executions: List<TaskExecution>): ParentDashboardUiState {
        val keys = lastDateKeys(7)
        val keySet = keys.toSet()
        val completed = executions.filter {
            it.status == TaskStatus.COMPLETED && it.dateKey in keySet
        }
        val reports = keys.map { key ->
            val day = completed.filter { it.dateKey == key }
            DailyReport(key, day.size, day.sumOf { it.starsEarned })
        }
        return ParentDashboardUiState(
            completedLast7Days = completed.size,
            starsLast7Days = completed.sumOf { it.starsEarned },
            streakDays = KidsRepository.calculateStreak(
                executions.filter { it.status == TaskStatus.COMPLETED }.map { it.dateKey },
                KidsRepository.dateKey()
            ),
            averageMinutes = if (completed.isEmpty()) {
                0
            } else {
                (completed.map { it.durationSeconds }.average() / 60.0).toInt()
            },
            dailyReports = reports
        )
    }

    private fun lastDateKeys(days: Int): List<String> {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val calendar = Calendar.getInstance()
        return (0 until days).map {
            val key = format.format(Date(calendar.timeInMillis))
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            key
        }.reversed()
    }

    private fun playSound(tone: Int) {
        if (!_soundEnabled.value) return
        viewModelScope.launch {
            val generator =
                runCatching { ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80) }
                    .getOrNull() ?: return@launch
            runCatching { generator.startTone(tone, 350) }
            delay(400)
            generator.release()
        }
    }
}

class MainViewModelFactory(context: Context) : ViewModelProvider.Factory {
    private val appContext = context.applicationContext

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(MainViewModel::class.java)) {
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
        val db = TaskDatabase.getDatabase(appContext)
        val repository = KidsRepository(db)
        val prefs = AppPreferencesRepository(appContext)
        @Suppress("UNCHECKED_CAST")
        return MainViewModel(repository, prefs) as T
    }
}
