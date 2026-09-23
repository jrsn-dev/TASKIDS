package com.taskids.app.domain.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "task_executions",
    indices = [Index("taskId"), Index("childId"), Index(value = ["childId", "dateKey"])]
)
data class TaskExecution(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val taskId: Int,
    val childId: Int,
    val dateKey: String,
    val startedAt: Long,
    val completedAt: Long? = null,
    val durationSeconds: Int = 0,
    val status: TaskStatus = TaskStatus.IN_PROGRESS,
    val starsEarned: Int = 0
)

@Entity(tableName = "star_transactions", indices = [Index("childId"), Index("dateKey")])
data class StarTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val childId: Int,
    val amount: Int,
    val reason: String,
    val referenceType: String,
    val referenceId: Long? = null,
    val dateKey: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "achievement_unlocks",
    primaryKeys = ["achievementId", "childId"],
    indices = [Index("childId")]
)
data class AchievementUnlock(
    val achievementId: String,
    val childId: Int,
    val unlockedAt: Long = System.currentTimeMillis()
)

data class AchievementDefinition(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val unlocked: Boolean = false
)

@Entity(tableName = "approved_content")
data class ApprovedContent(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val youtubeVideoId: String,
    val thumbnailEmoji: String = "▶️",
    val isEnabled: Boolean = true
)

data class DailyReport(
    val dateKey: String,
    val completed: Int,
    val stars: Int
)
