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
import com.example.game.GameEngine
import com.example.model.Achievement
import com.example.model.Child
import com.example.model.DefaultProfiles
import com.example.model.DefaultTasks
import com.example.model.Reward
import com.example.model.RewardRedemption
import com.example.model.Routine
import com.example.model.Task
import com.example.model.TaskExecution
import com.example.model.TaskStatus
import com.example.ui.navigation.AppScreen
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.math.max

data class MissionCompletion(
    val taskTitle: String,
    val stars: Int,
    val xp: Int,
    val combo: Int,
    val comboBonusStars: Int,
    val perfectDayStars: Int = 0,
    val perfectDayXp: Int = 0
)

data class TimerState(
    val taskId: Int = 0,
    val totalSeconds: Int = 0,
    val remainingSeconds: Int = 0,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val progress: Float = 0f,
    val startedAt: Long = 0L,
    val endAt: Long = 0L
)

enum class PinVerificationResult {
    SUCCESS,
    INVALID,
    LOCKED
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(
    private val repository: TaskRepository,
    private val prefs: AppPreferencesRepository
) : ViewModel() {

    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.Home)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _selectedChildId = MutableStateFlow(prefs.selectedChildId)
    val selectedChildId: StateFlow<Long> = _selectedChildId.asStateFlow()

    val children: StateFlow<List<Child>> = repository.children
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val currentChild: StateFlow<Child?> = combine(children, selectedChildId) { profiles, selectedId ->
        profiles.firstOrNull { it.id == selectedId } ?: profiles.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val tasks: StateFlow<List<Task>> = selectedChildId
        .flatMapLatest { repository.tasksForChild(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val rewards: StateFlow<List<Reward>> = selectedChildId
        .flatMapLatest { repository.rewardsForChild(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val executions: StateFlow<List<TaskExecution>> = selectedChildId
        .flatMapLatest { repository.executionsForChild(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val routines: StateFlow<List<Routine>> = selectedChildId
        .flatMapLatest { repository.routinesForChild(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val achievements: StateFlow<List<Achievement>> = combine(currentChild, executions) { profile, history ->
        val stars = profile?.totalStars ?: 0
        val streak = profile?.currentStreak ?: 0
        listOf(
            Achievement(
                id = "first_task",
                title = "Primeira missão",
                description = "Concluiu a primeira tarefa.",
                icon = "🌟",
                unlocked = history.isNotEmpty()
            ),
            Achievement(
                id = "ten_tasks",
                title = "Super consistente",
                description = "Concluiu 10 tarefas.",
                icon = "🏆",
                unlocked = history.size >= 10
            ),
            Achievement(
                id = "streak_3",
                title = "Três dias seguidos",
                description = "Manteve uma sequência de 3 dias.",
                icon = "🔥",
                unlocked = streak >= 3
            ),
            Achievement(
                id = "stars_100",
                title = "Colecionador(a) de estrelas",
                description = "Alcançou 100 estrelas na jornada.",
                icon = "⭐",
                unlocked = stars >= 100
            ),
            Achievement(
                id = "combo_5",
                title = "Combo de missões",
                description = "Concluiu 5 missões em sequência.",
                icon = "",
                unlocked = (profile?.bestCombo ?: 0) >= 5
            ),
            Achievement(
                id = "xp_2500",
                title = "Guardião da jornada",
                description = "Conquistou 2500 XP.",
                icon = "",
                unlocked = (profile?.totalXp ?: 0) >= 2500
            )
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _soundEnabled = MutableStateFlow(prefs.soundEnabled)
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()
    private var timerJob: Job? = null

    private val _screenTimeRemainingSeconds = MutableStateFlow(0)
    val screenTimeRemainingSeconds: StateFlow<Int> = _screenTimeRemainingSeconds.asStateFlow()
    private var screenTimeJob: Job? = null

    private val _lastRewardMessage = MutableStateFlow<String?>(null)
    val lastRewardMessage: StateFlow<String?> = _lastRewardMessage.asStateFlow()

    private val _lastMissionCompletion = MutableStateFlow<MissionCompletion?>(null)
    val lastMissionCompletion: StateFlow<MissionCompletion?> = _lastMissionCompletion.asStateFlow()

    init {
        viewModelScope.launch {
            seedInitialData()
            val selectedExists = repository.childById(prefs.selectedChildId) != null
            if (!selectedExists) {
                selectChild(children.value.firstOrNull()?.id ?: 1L)
            }
            restoreTimerIfNeeded()
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun selectChild(childId: Long) {
        prefs.selectedChildId = childId
        _selectedChildId.value = childId
        _lastRewardMessage.value = null
        navigateTo(AppScreen.Home)
    }

    fun selectTask(task: Task) {
        navigateTo(AppScreen.Timer(task.id))
        if (timerState.value.taskId != task.id || timerState.value.remainingSeconds <= 0) {
            startTimer(task)
        }
    }

    fun addTask(
        title: String,
        durationMinutes: Int,
        icon: String,
        description: String = "",
        rewardStars: Int = 10,
        rewardXp: Int = 100,
        iconKey: String = "GENERIC",
        scheduledTime: String? = null,
        recurrenceDays: String = "",
        isRecurring: Boolean = false
    ) {
        val childId = selectedChildId.value
        viewModelScope.launch {
            val nextOrder = (tasks.value.maxOfOrNull { it.orderIndex } ?: 0) + 1
            repository.insertTask(
                Task(
                    childId = childId,
                    title = title.trim(),
                    description = description.trim(),
                    durationMinutes = durationMinutes.coerceIn(1, 240),
                    icon = icon,
                    orderIndex = nextOrder,
                    rewardStars = rewardStars.coerceIn(1, 100),
                    rewardXp = rewardXp.coerceIn(10, 1000),
                    iconKey = iconKey,
                    scheduledTime = scheduledTime?.takeIf { it.isNotBlank() },
                    recurrenceDays = recurrenceDays,
                    isRecurring = isRecurring
                )
            )
        }
    }

    fun editTask(task: Task) {
        viewModelScope.launch { repository.updateTask(task) }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch { repository.deleteTask(task) }
    }

    fun restartAllTasks() {
        viewModelScope.launch {
            repository.resetTasksForChild(selectedChildId.value)
            currentChild.value?.let { repository.updateChild(it.copy(currentCombo = 0)) }
            prefs.clearTimer()
            timerJob?.cancel()
            _timerState.value = TimerState()
            navigateTo(AppScreen.Home)
        }
    }

    fun markTaskAsCompleted(task: Task) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val child = currentChild.value ?: return@launch
            val startedAt = if (timerState.value.taskId == task.id && timerState.value.startedAt > 0) {
                timerState.value.startedAt
            } else {
                now - task.durationMinutes * 60_000L
            }
            val actualSeconds = ((now - startedAt) / 1_000L).toInt().coerceAtLeast(1)

            val baseReward = GameEngine.rewardForCompletion(
                baseStars = task.rewardStars,
                baseXp = task.rewardXp,
                previousCombo = child.currentCombo
            )

            val updatedTasks = tasks.value.map {
                if (it.id == task.id) it.copy(status = TaskStatus.COMPLETED) else it
            }
            val activeTasks = updatedTasks.filter { it.isActive }
            val completedActive = activeTasks.count { it.status == TaskStatus.COMPLETED }
            val perfectBonus = GameEngine.perfectDayBonus(activeTasks.size, completedActive)
            val perfectStars = perfectBonus.first
            val perfectXp = perfectBonus.second

            repository.updateTask(
                task.copy(
                    status = TaskStatus.COMPLETED,
                    completedAt = now
                )
            )

            repository.insertExecution(
                TaskExecution(
                    taskId = task.id,
                    childId = task.childId,
                    taskTitle = task.title,
                    startedAt = startedAt,
                    completedAt = now,
                    plannedDurationMinutes = task.durationMinutes,
                    actualDurationSeconds = actualSeconds,
                    earnedStars = baseReward.earnedStars + perfectStars,
                    earnedXp = baseReward.earnedXp + perfectXp,
                    combo = baseReward.newCombo
                )
            )

            val today = TimeUnit.MILLISECONDS.toDays(now)
            val newStreak = when (child.lastActiveEpochDay) {
                today -> child.currentStreak
                today - 1 -> child.currentStreak + 1
                else -> 1
            }

            repository.updateChild(
                child.copy(
                    totalStars = child.totalStars + baseReward.earnedStars + perfectStars,
                    totalXp = child.totalXp + baseReward.earnedXp + perfectXp,
                    currentCombo = baseReward.newCombo,
                    bestCombo = max(child.bestCombo, baseReward.newCombo),
                    currentStreak = newStreak,
                    longestStreak = max(child.longestStreak, newStreak),
                    lastActiveEpochDay = today
                )
            )

            _lastMissionCompletion.value = MissionCompletion(
                taskTitle = task.title,
                stars = baseReward.earnedStars + perfectStars,
                xp = baseReward.earnedXp + perfectXp,
                combo = baseReward.newCombo,
                comboBonusStars = baseReward.comboBonusStars,
                perfectDayStars = perfectStars,
                perfectDayXp = perfectXp
            )

            playAlertSound(ToneGenerator.TONE_PROP_ACK)
            clearActiveTimer()

            val allCompleted = activeTasks.isNotEmpty() &&
                activeTasks.all { it.status == TaskStatus.COMPLETED }

            navigateTo(AppScreen.RewardUnlocked)
        }
    }

    private fun startTimer(task: Task) {
        val totalSeconds = task.durationMinutes * 60
        val now = System.currentTimeMillis()
        val endAt = now + totalSeconds * 1_000L

        viewModelScope.launch {
            repository.updateTask(task.copy(status = TaskStatus.IN_PROGRESS))
        }

        _timerState.value = TimerState(
            taskId = task.id,
            totalSeconds = totalSeconds,
            remainingSeconds = totalSeconds,
            isRunning = true,
            isPaused = false,
            progress = 1f,
            startedAt = now,
            endAt = endAt
        )
        persistTimer()
        runTimerTicker()
    }

    fun pauseTimer() {
        val current = _timerState.value
        if (current.taskId == 0) return
        _timerState.value = current.copy(isRunning = false, isPaused = true)
        persistTimer()
    }

    fun resumeTimer() {
        val current = _timerState.value
        if (current.taskId == 0 || current.remainingSeconds <= 0) return
        val newEnd = System.currentTimeMillis() + current.remainingSeconds * 1_000L
        _timerState.value = current.copy(
            isRunning = true,
            isPaused = false,
            endAt = newEnd
        )
        persistTimer()
        runTimerTicker()
    }

    fun extendTimer(minutes: Int = 5) {
        val current = _timerState.value
        if (current.taskId == 0) return
        val extra = minutes.coerceIn(1, 30) * 60
        val newTotal = current.totalSeconds + extra
        val newRemaining = current.remainingSeconds + extra
        val newEnd = if (current.isRunning) {
            System.currentTimeMillis() + newRemaining * 1_000L
        } else current.endAt

        _timerState.value = current.copy(
            totalSeconds = newTotal,
            remainingSeconds = newRemaining,
            endAt = newEnd,
            progress = newRemaining.toFloat() / newTotal.toFloat()
        )
        persistTimer()
    }

    fun cancelActiveTimer() {
        clearActiveTimer()
        navigateTo(AppScreen.Home)
    }

    private fun runTimerTicker() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                val current = _timerState.value
                if (current.taskId == 0 || current.remainingSeconds <= 0) break

                if (current.isRunning && !current.isPaused) {
                    val remaining = ((current.endAt - System.currentTimeMillis()) / 1_000L)
                        .toInt()
                        .coerceAtLeast(0)
                    val progress = if (current.totalSeconds == 0) 0f
                    else remaining.toFloat() / current.totalSeconds.toFloat()

                    _timerState.value = current.copy(
                        remainingSeconds = remaining,
                        progress = progress.coerceIn(0f, 1f)
                    )

                    if (remaining % 5 == 0) persistTimer()

                    if (remaining == 0) {
                        onTimerFinished(current.taskId)
                        break
                    }
                }
                delay(1_000)
            }
        }
    }

    private fun onTimerFinished(taskId: Int) {
        viewModelScope.launch {
            repository.getTaskById(taskId)?.let {
                repository.updateTask(it.copy(status = TaskStatus.OVERDUE))
            }
        }
        _timerState.value = _timerState.value.copy(
            remainingSeconds = 0,
            isRunning = false,
            isPaused = false,
            progress = 0f
        )
        persistTimer()
        playAlertSound(ToneGenerator.TONE_CDMA_HIGH_L)
    }

    private suspend fun restoreTimerIfNeeded() {
        val taskId = prefs.activeTaskId
        if (taskId == 0) return
        val task = repository.getTaskById(taskId) ?: run {
            prefs.clearTimer()
            return
        }

        val totalSeconds = task.durationMinutes * 60
        val remaining = if (prefs.timerPaused) {
            prefs.timerRemainingSeconds
        } else {
            ((prefs.timerEndAt - System.currentTimeMillis()) / 1_000L).toInt()
        }.coerceAtLeast(0)

        if (remaining <= 0) {
            repository.updateTask(task.copy(status = TaskStatus.OVERDUE))
            prefs.clearTimer()
            return
        }

        _timerState.value = TimerState(
            taskId = task.id,
            totalSeconds = totalSeconds.coerceAtLeast(remaining),
            remainingSeconds = remaining,
            isRunning = !prefs.timerPaused,
            isPaused = prefs.timerPaused,
            progress = remaining.toFloat() / totalSeconds.coerceAtLeast(1).toFloat(),
            startedAt = prefs.timerStartedAt,
            endAt = if (prefs.timerPaused) 0L else prefs.timerEndAt
        )
        navigateTo(AppScreen.Timer(task.id))
        if (!prefs.timerPaused) runTimerTicker()
    }

    private fun persistTimer() {
        val timer = _timerState.value
        if (timer.taskId == 0) return
        prefs.persistTimer(
            taskId = timer.taskId,
            startedAt = timer.startedAt,
            endAt = timer.endAt,
            remainingSeconds = timer.remainingSeconds,
            paused = timer.isPaused
        )
    }

    private fun clearActiveTimer() {
        timerJob?.cancel()
        prefs.clearTimer()
        _timerState.value = TimerState()
    }

    fun redeemReward(reward: Reward) {
        val child = currentChild.value ?: return
        if (child.totalStars < reward.costStars) {
            _lastRewardMessage.value = "Faltam ${reward.costStars - child.totalStars} estrelas para esta recompensa."
            return
        }

        viewModelScope.launch {
            repository.updateChild(child.copy(totalStars = child.totalStars - reward.costStars))
            repository.insertRedemption(
                RewardRedemption(
                    childId = child.id,
                    rewardId = reward.id,
                    rewardTitle = reward.title,
                    costStars = reward.costStars
                )
            )
            _lastRewardMessage.value = "${reward.icon} ${reward.title} desbloqueada!"

            if (reward.type == "SCREEN_TIME" && reward.durationMinutes != null) {
                startScreenTime(reward.durationMinutes)
            }
        }
    }

    private fun startScreenTime(minutes: Int) {
        screenTimeJob?.cancel()
        _screenTimeRemainingSeconds.value = minutes * 60
        navigateTo(AppScreen.ScreenTime)
        screenTimeJob = viewModelScope.launch {
            while (_screenTimeRemainingSeconds.value > 0) {
                delay(1_000)
                _screenTimeRemainingSeconds.value = (_screenTimeRemainingSeconds.value - 1).coerceAtLeast(0)
            }
            playAlertSound(ToneGenerator.TONE_PROP_BEEP2)
            navigateTo(AppScreen.Home)
        }
    }

    fun stopScreenTime() {
        screenTimeJob?.cancel()
        _screenTimeRemainingSeconds.value = 0
        navigateTo(AppScreen.Home)
    }

    fun addReward(title: String, costStars: Int, icon: String, description: String) {
        viewModelScope.launch {
            repository.upsertReward(
                Reward(
                    childId = selectedChildId.value,
                    title = title.trim(),
                    description = description.trim(),
                    icon = icon,
                    costStars = costStars.coerceIn(1, 10_000)
                )
            )
        }
    }

    fun addChild(name: String, avatarEmoji: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.upsertChild(
                Child(
                    name = name.trim(),
                    avatarEmoji = avatarEmoji
                )
            )
        }
    }

    fun addRoutine(title: String, icon: String, startTime: String?, daysCsv: String) {
        viewModelScope.launch {
            repository.upsertRoutine(
                Routine(
                    childId = selectedChildId.value,
                    title = title.trim(),
                    icon = icon,
                    startTime = startTime,
                    daysCsv = daysCsv
                )
            )
        }
    }


    fun updateAvatar(
        character: String,
        skinTone: Int,
        hairStyle: Int,
        hairColor: Int,
        outfitColor: Int
    ) {
        val child = currentChild.value ?: return
        viewModelScope.launch {
            repository.updateChild(
                child.copy(
                    avatarCharacter = if (character.uppercase() == "GIRL") "GIRL" else "BOY",
                    avatarSkinTone = skinTone.coerceIn(0, 4),
                    avatarHairStyle = hairStyle.coerceIn(0, 3),
                    avatarHairColor = hairColor.coerceIn(0, 3),
                    avatarOutfitColor = outfitColor.coerceIn(0, 4)
                )
            )
        }
    }

    fun setGameTheme(theme: String) {
        val child = currentChild.value ?: return
        viewModelScope.launch {
            repository.updateChild(child.copy(gameTheme = theme))
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        prefs.soundEnabled = enabled
        _soundEnabled.value = enabled
    }

    fun verifyParentalPin(pin: String): PinVerificationResult {
        val now = System.currentTimeMillis()
        if (prefs.pinLockUntil > now) return PinVerificationResult.LOCKED

        if (prefs.verifyPin(pin)) {
            prefs.pinFailedAttempts = 0
            prefs.pinLockUntil = 0L
            return PinVerificationResult.SUCCESS
        }

        val attempts = prefs.pinFailedAttempts + 1
        if (attempts >= 3) {
            prefs.pinFailedAttempts = 0
            prefs.pinLockUntil = now + 30_000L
            return PinVerificationResult.LOCKED
        }

        prefs.pinFailedAttempts = attempts
        return PinVerificationResult.INVALID
    }

    fun updateParentalPin(pin: String): Boolean {
        if (pin.length != 4 || !pin.all { it.isDigit() }) return false
        prefs.updatePin(pin)
        return true
    }

    private suspend fun seedInitialData() {
        if (repository.childrenCount() == 0) {
            DefaultProfiles.children().forEach { repository.upsertChild(it) }
        }

        DefaultProfiles.children().forEach { profile ->
            if (repository.countTasksForChild(profile.id) == 0) {
                DefaultTasks.getDefaultTasks()
                    .filter { it.childId == profile.id }
                    .forEach { repository.insertTask(it) }
            }
        }

        if (repository.rewardsCount() == 0) {
            DefaultProfiles.rewards().forEach { repository.upsertReward(it) }
        }
    }

    private fun playAlertSound(toneType: Int) {
        if (!soundEnabled.value) return
        runCatching {
            ToneGenerator(AudioManager.STREAM_NOTIFICATION, 90)
                .startTone(toneType, 350)
        }
    }
}

class MainViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            val database = TaskDatabase.getDatabase(context)
            val repository = TaskRepository(
                taskDao = database.taskDao(),
                childDao = database.childDao(),
                executionDao = database.executionDao(),
                rewardDao = database.rewardDao(),
                routineDao = database.routineDao()
            )
            val prefs = AppPreferencesRepository(context)
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository, prefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
