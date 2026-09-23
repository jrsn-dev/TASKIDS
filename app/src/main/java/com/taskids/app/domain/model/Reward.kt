package com.taskids.app.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class RewardType {
    SCREEN_TIME,
    ACTIVITY,
    CUSTOM
}

@Entity(tableName = "rewards", indices = [Index("childId")])
data class Reward(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val childId: Int,
    val title: String,
    val description: String = "",
    val icon: String = "🎁",
    val costStars: Int,
    val type: RewardType = RewardType.CUSTOM,
    val valueMinutes: Int = 0,
    @ColumnInfo(defaultValue = "1")
    val isEnabled: Boolean = true
)

@Entity(tableName = "reward_redemptions", indices = [Index("childId"), Index("rewardId")])
data class RewardRedemption(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val rewardId: Int,
    val childId: Int,
    val redeemedAt: Long = System.currentTimeMillis(),
    val costStars: Int
)
