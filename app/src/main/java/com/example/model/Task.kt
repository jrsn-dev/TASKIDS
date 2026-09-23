package com.example.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

enum class TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    OVERDUE
}

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(defaultValue = "1")
    val childId: Long = 1,
    val title: String,
    val description: String = "",
    val durationMinutes: Int,
    val icon: String = "",
    @ColumnInfo(defaultValue = "'GENERIC'")
    val iconKey: String = "GENERIC",
    val orderIndex: Int,
    @ColumnInfo(defaultValue = "10")
    val rewardStars: Int = 10,
    @ColumnInfo(defaultValue = "100")
    val rewardXp: Int = 100,
    val scheduledTime: String? = null,
    @ColumnInfo(defaultValue = "''")
    val recurrenceDays: String = "",
    @ColumnInfo(defaultValue = "0")
    val isRecurring: Boolean = false,
    val status: TaskStatus = TaskStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val isActive: Boolean = true
) : Serializable
