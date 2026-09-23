package com.taskids.app.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    OVERDUE
}

enum class RoutinePeriod(val label: String, val icon: String) {
    MORNING("Manhã", "☀️"),
    AFTERNOON("Tarde", "🌤️"),
    EVENING("Noite", "🌙"),
    ANYTIME("Qualquer horário", "✨")
}

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(defaultValue = "1")
    val childId: Int = 1,
    val title: String,
    val description: String = "",
    val durationMinutes: Int,
    val icon: String = "⭐",
    val orderIndex: Int,
    @ColumnInfo(defaultValue = "10")
    val rewardPoints: Int = 10,
    @ColumnInfo(defaultValue = "127")
    val recurrenceMask: Int = EVERY_DAY,
    @ColumnInfo(defaultValue = "'ANYTIME'")
    val routinePeriod: RoutinePeriod = RoutinePeriod.ANYTIME,
    @ColumnInfo(defaultValue = "1")
    val isEnabled: Boolean = true,
    val status: TaskStatus = TaskStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val isActive: Boolean = false,
    val dueHour: Int? = null,
    val dueMinute: Int? = null
) {
    fun isScheduledFor(dayIndex: Int): Boolean {
        if (!isEnabled || dayIndex !in 0..6) return false
        return recurrenceMask and (1 shl dayIndex) != 0
    }

    companion object {
        const val EVERY_DAY = 127
        const val WEEKDAYS = 31
    }
}
