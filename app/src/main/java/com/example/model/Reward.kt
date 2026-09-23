package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rewards")
data class Reward(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val childId: Long = 0,
    val title: String,
    val description: String = "",
    val icon: String = "🎁",
    val costStars: Int,
    val type: String = "CUSTOM",
    val durationMinutes: Int? = null,
    val isEnabled: Boolean = true
)
