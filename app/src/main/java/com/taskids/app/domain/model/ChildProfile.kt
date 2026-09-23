package com.taskids.app.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "children")
data class ChildProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val age: Int = 7,
    val avatar: String = "🧒",
    val accentColorHex: String = "#3B82F6",
    @ColumnInfo(defaultValue = "0")
    val totalStars: Int = 0,
    @ColumnInfo(defaultValue = "1")
    val level: Int = 1,
    @ColumnInfo(defaultValue = "1")
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
