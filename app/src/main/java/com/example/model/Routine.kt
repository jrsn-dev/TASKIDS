package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routines")
data class Routine(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val childId: Long,
    val title: String,
    val icon: String = "🌟",
    val startTime: String? = null,
    val daysCsv: String = "1,2,3,4,5",
    val isActive: Boolean = true
)

@Entity(
    tableName = "routine_tasks",
    primaryKeys = ["routineId", "taskId"]
)
data class RoutineTask(
    val routineId: Long,
    val taskId: Int,
    val position: Int
)
