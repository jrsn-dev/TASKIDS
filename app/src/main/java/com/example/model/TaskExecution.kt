package com.example.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_executions")
data class TaskExecution(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val taskId: Int,
    val childId: Long,
    val taskTitle: String,
    val startedAt: Long,
    val completedAt: Long,
    val plannedDurationMinutes: Int,
    val actualDurationSeconds: Int,
    val earnedStars: Int,
    @ColumnInfo(defaultValue = "0")
    val earnedXp: Int = 0,
    @ColumnInfo(defaultValue = "1")
    val combo: Int = 1,
    val result: TaskStatus = TaskStatus.COMPLETED
)
