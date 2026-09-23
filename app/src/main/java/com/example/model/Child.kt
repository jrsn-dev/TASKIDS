package com.example.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "children")
data class Child(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val avatarEmoji: String = "",
    val accentColor: String = "#2F80ED",
    val birthYear: Int? = null,
    val totalStars: Int = 0,
    @ColumnInfo(defaultValue = "0")
    val totalXp: Int = 0,
    @ColumnInfo(defaultValue = "0")
    val currentCombo: Int = 0,
    @ColumnInfo(defaultValue = "0")
    val bestCombo: Int = 0,
    @ColumnInfo(defaultValue = "2")
    val avatarSkinTone: Int = 2,
    @ColumnInfo(defaultValue = "0")
    val avatarHairStyle: Int = 0,
    @ColumnInfo(defaultValue = "0")
    val avatarHairColor: Int = 0,
    @ColumnInfo(defaultValue = "0")
    val avatarOutfitColor: Int = 0,
    @ColumnInfo(defaultValue = "'SKY'")
    val gameTheme: String = "SKY",
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastActiveEpochDay: Long? = null,
    val isActive: Boolean = true
)
