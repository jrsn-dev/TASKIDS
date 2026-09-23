package com.example.data.local

import androidx.room.*
import com.example.model.Reward
import com.example.model.RewardRedemption
import kotlinx.coroutines.flow.Flow

@Dao
interface RewardDao {
    @Query("SELECT * FROM rewards WHERE isEnabled = 1 AND (childId = 0 OR childId = :childId) ORDER BY costStars ASC")
    fun observeRewards(childId: Long): Flow<List<Reward>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(reward: Reward): Long

    @Insert
    suspend fun insertRedemption(redemption: RewardRedemption): Long

    @Query("SELECT COUNT(*) FROM rewards")
    suspend fun countRewards(): Int

    @Query("SELECT * FROM reward_redemptions WHERE childId = :childId ORDER BY redeemedAt DESC LIMIT 50")
    fun observeRedemptions(childId: Long): Flow<List<RewardRedemption>>
}
