package com.taskids.app.data.repository

import androidx.room.withTransaction
import com.taskids.app.data.local.DefaultData
import com.taskids.app.data.local.TaskDatabase
import com.taskids.app.domain.model.AchievementCatalog
import com.taskids.app.domain.model.AchievementUnlock
import com.taskids.app.domain.model.ApprovedContent
import com.taskids.app.domain.model.ChildProfile
import com.taskids.app.domain.model.Reward
import com.taskids.app.domain.model.RewardRedemption
import com.taskids.app.domain.model.StarTransaction
import com.taskids.app.domain.model.Task
import com.taskids.app.domain.model.TaskExecution
import com.taskids.app.domain.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class CompletionResult(
    val starsEarned: Int,
    val allScheduledTasksCompleted: Boolean
)

data class RedemptionResult(
    val success: Boolean,
    val reward: Reward? = null,
    val remainingStars: Int = 0
)

class KidsRepository(private val db: TaskDatabase) {
    private val taskDao = db.taskDao()
    private val childDao = db.childDao()
    private val historyDao = db.historyDao()
    private val rewardDao = db.rewardDao()
    private val approvedContentDao = db.approvedContentDao()

    fun observeChildren(): Flow<List<ChildProfile>> = childDao.observeActive()
    fun observeTasks(childId: Int): Flow<List<Task>> = taskDao.observeForChild(childId)
    fun observeRewards(childId: Int): Flow<List<Reward>> = rewardDao.observeAll(childId)
    fun observeHistory(childId: Int): Flow<List<TaskExecution>> = historyDao.observeForChild(childId)
    fun observeStarTransactions(childId: Int): Flow<List<StarTransaction>> = historyDao.observeStarTransactions(childId)
    fun observeAchievementUnlocks(childId: Int): Flow<List<AchievementUnlock>> = historyDao.observeAchievements(childId)
    fun observeApprovedContent(): Flow<List<ApprovedContent>> = approvedContentDao.observeAll()

    suspend fun seedIfNeeded() {
        db.withTransaction {
            if (childDao.count() == 0) {
                DefaultData.children().forEach { childDao.insert(it) }
            }

            childDao.getAll().forEach { child ->
                if (taskDao.getForChild(child.id).isEmpty()) {
                    DefaultData.tasks(child.id).forEach { taskDao.insert(it) }
                }
                if (rewardDao.countForChild(child.id) == 0) {
                    DefaultData.rewards(child.id).forEach { rewardDao.insert(it) }
                }
            }
        }
    }

    suspend fun getChild(id: Int): ChildProfile? = childDao.getById(id)
    suspend fun getChildren(): List<ChildProfile> = childDao.getAll()
    suspend fun getTask(id: Int): Task? = taskDao.getById(id)
    suspend fun getTasks(childId: Int): List<Task> = taskDao.getForChild(childId)

    suspend fun resetDailyStatuses() {
        taskDao.resetDailyStatuses()
    }

    suspend fun restartChildTasks(childId: Int) {
        taskDao.resetStatusesForChild(childId)
    }

    suspend fun startTask(task: Task, now: Long = System.currentTimeMillis()): Long {
        return db.withTransaction {
            taskDao.setStatus(task.id, TaskStatus.IN_PROGRESS, true)
            historyDao.insertExecution(
                TaskExecution(
                    taskId = task.id,
                    childId = task.childId,
                    dateKey = dateKey(now),
                    startedAt = now,
                    status = TaskStatus.IN_PROGRESS
                )
            )
        }
    }

    suspend fun finishTask(
        taskId: Int,
        executionId: Long,
        status: TaskStatus,
        durationSeconds: Int,
        now: Long = System.currentTimeMillis()
    ): CompletionResult {
        val result = db.withTransaction {
            val task = taskDao.getById(taskId) ?: return@withTransaction CompletionResult(0, false)
            val today = dateKey(now)
            val alreadyCompleted = historyDao.getCompletedForDay(task.id, today)
            val currentExecution = historyDao.getExecution(executionId)
            val stars = if (
                status == TaskStatus.COMPLETED &&
                currentExecution?.status != TaskStatus.COMPLETED &&
                alreadyCompleted == null
            ) task.rewardPoints else 0

            if (status == TaskStatus.COMPLETED) {
                taskDao.complete(task.id, TaskStatus.COMPLETED, now)
            } else {
                taskDao.setStatus(task.id, status, false)
            }

            historyDao.finishExecution(
                executionId = executionId,
                status = status,
                completedAt = now,
                durationSeconds = durationSeconds.coerceAtLeast(0),
                stars = stars
            )

            if (stars > 0) {
                val child = childDao.getById(task.childId)
                if (child != null) {
                    val newTotal = child.totalStars + stars
                    childDao.update(
                        child.copy(
                            totalStars = newTotal,
                            level = 1 + (newTotal / 100)
                        )
                    )
                    historyDao.insertStarTransaction(
                        StarTransaction(
                            childId = task.childId,
                            amount = stars,
                            reason = task.title,
                            referenceType = "TASK",
                            referenceId = executionId,
                            dateKey = today
                        )
                    )
                }
            }

            val scheduled = taskDao.getForChild(task.childId)
                .filter { it.isScheduledFor(dayIndex(now)) }
            val perfectDay = scheduled.isNotEmpty() && scheduled.all {
                historyDao.getCompletedForDay(it.id, today) != null ||
                    it.id == task.id && status == TaskStatus.COMPLETED
            }

            CompletionResult(stars, perfectDay)
        }

        val task = taskDao.getById(taskId)
        if (task != null && status == TaskStatus.COMPLETED) {
            evaluateAchievements(task.childId, result.allScheduledTasksCompleted, now)
        }
        return result
    }

    suspend fun addTask(task: Task) {
        val current = taskDao.getForChild(task.childId)
        val nextOrder = (current.maxOfOrNull { it.orderIndex } ?: 0) + 1
        taskDao.insert(task.copy(orderIndex = nextOrder))
    }

    suspend fun updateTask(task: Task) = taskDao.update(task)
    suspend fun deleteTask(task: Task) = taskDao.delete(task)

    suspend fun addChild(profile: ChildProfile): Int {
        val id = childDao.insert(profile).toInt()
        DefaultData.tasks(id).forEach { taskDao.insert(it) }
        DefaultData.rewards(id).forEach { rewardDao.insert(it) }
        return id
    }

    suspend fun updateChild(profile: ChildProfile) = childDao.update(profile)

    suspend fun addReward(reward: Reward) = rewardDao.insert(reward)
    suspend fun updateReward(reward: Reward) = rewardDao.update(reward)
    suspend fun deleteReward(reward: Reward) = rewardDao.delete(reward)

    suspend fun redeemReward(rewardId: Int, now: Long = System.currentTimeMillis()): RedemptionResult {
        return db.withTransaction {
            val reward = rewardDao.getById(rewardId) ?: return@withTransaction RedemptionResult(false)
            val child = childDao.getById(reward.childId) ?: return@withTransaction RedemptionResult(false)
            if (!reward.isEnabled || child.totalStars < reward.costStars) {
                return@withTransaction RedemptionResult(false, reward, child.totalStars)
            }

            val remaining = child.totalStars - reward.costStars
            childDao.update(child.copy(totalStars = remaining, level = 1 + (remaining / 100)))
            val redemptionId = rewardDao.insertRedemption(
                RewardRedemption(
                    rewardId = reward.id,
                    childId = child.id,
                    redeemedAt = now,
                    costStars = reward.costStars
                )
            )
            historyDao.insertStarTransaction(
                StarTransaction(
                    childId = child.id,
                    amount = -reward.costStars,
                    reason = "Resgate: ${reward.title}",
                    referenceType = "REWARD",
                    referenceId = redemptionId,
                    dateKey = dateKey(now)
                )
            )
            RedemptionResult(true, reward, remaining)
        }
    }

    suspend fun addApprovedContent(title: String, videoInput: String): Boolean {
        val videoId = extractYoutubeVideoId(videoInput) ?: return false
        approvedContentDao.insert(
            ApprovedContent(
                title = title.trim().ifBlank { "Vídeo aprovado" },
                youtubeVideoId = videoId
            )
        )
        return true
    }

    suspend fun updateApprovedContent(content: ApprovedContent) = approvedContentDao.update(content)
    suspend fun deleteApprovedContent(content: ApprovedContent) = approvedContentDao.delete(content)

    suspend fun historyBetween(childId: Int, startDate: String, endDate: String): List<TaskExecution> =
        historyDao.getBetween(childId, startDate, endDate)

    suspend fun completionDates(childId: Int): List<String> = historyDao.completionDates(childId)

    private suspend fun evaluateAchievements(childId: Int, perfectDay: Boolean, now: Long) {
        val completedCount = historyDao.countCompleted(childId)
        val child = childDao.getById(childId) ?: return
        val streak = calculateStreak(historyDao.completionDates(childId), dateKey(now))

        val unlocked = buildSet {
            if (completedCount >= 1) add("first_mission")
            if (completedCount >= 10) add("ten_missions")
            if (child.totalStars >= 100) add("hundred_stars")
            if (perfectDay) add("perfect_day")
            if (streak >= 7) add("seven_day_streak")
        }

        AchievementCatalog.definitions
            .filter { it.id in unlocked }
            .forEach { historyDao.unlockAchievement(AchievementUnlock(it.id, childId, now)) }
    }

    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        fun dateKey(timestamp: Long = System.currentTimeMillis()): String =
            synchronized(dateFormat) { dateFormat.format(Date(timestamp)) }

        fun dayIndex(timestamp: Long = System.currentTimeMillis()): Int {
            val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
            return (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7
        }

        fun calculateStreak(dateKeys: List<String>, todayKey: String): Int {
            val distinct = dateKeys.distinct().toSet()
            if (distinct.isEmpty()) return 0
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val calendar = Calendar.getInstance()
            calendar.time = runCatching { format.parse(todayKey) }.getOrNull() ?: return 0

            var streak = 0
            while (true) {
                val key = format.format(calendar.time)
                if (key !in distinct) break
                streak++
                calendar.add(Calendar.DAY_OF_YEAR, -1)
            }
            return streak
        }

        fun extractYoutubeVideoId(input: String): String? {
            val raw = input.trim()
            if (raw.matches(Regex("^[A-Za-z0-9_-]{11}$"))) return raw
            val patterns = listOf(
                Regex("(?:v=)([A-Za-z0-9_-]{11})"),
                Regex("""youtu\.be/([A-Za-z0-9_-]{11})"""),
                Regex("""youtube\.com/shorts/([A-Za-z0-9_-]{11})""")
            )
            return patterns.firstNotNullOfOrNull { it.find(raw)?.groupValues?.getOrNull(1) }
        }
    }
}
