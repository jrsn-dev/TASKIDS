package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reward_redemptions")
data class RewardRedemption(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val childId: Long,
    val rewardId: Long,
    val rewardTitle: String,
    val costStars: Int,
    val redeemedAt: Long = System.currentTimeMillis()
)
