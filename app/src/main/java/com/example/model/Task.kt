package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

enum class TaskStatus {
    PENDING,      // Não iniciada
    IN_PROGRESS,  // Em progresso
    COMPLETED,    // Concluída
    OVERDUE       // Atrasada
}

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String = "",
    val durationMinutes: Int,
    val icon: String = "📋",
    val orderIndex: Int,
    val status: TaskStatus = TaskStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val isActive: Boolean = false
) : Serializable
