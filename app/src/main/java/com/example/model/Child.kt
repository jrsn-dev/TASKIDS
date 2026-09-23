package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "children")
data class Child(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val avatarEmoji: String = "⭐",
    val accentColor: String = "#2F80ED",
    val birthYear: Int? = null,
    val totalStars: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastActiveEpochDay: Long? = null,
    val isActive: Boolean = true
)
