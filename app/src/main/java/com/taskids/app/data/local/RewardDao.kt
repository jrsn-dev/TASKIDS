package com.taskids.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.taskids.app.domain.model.Reward
import com.taskids.app.domain.model.RewardRedemption
import kotlinx.coroutines.flow.Flow

@Dao
interface RewardDao {
    @Query("SELECT * FROM rewards WHERE childId = :childId AND isEnabled = 1 ORDER BY costStars ASC")
    fun observeEnabled(childId: Int): Flow<List<Reward>>

    @Query("SELECT * FROM rewards WHERE childId = :childId ORDER BY costStars ASC")
    fun observeAll(childId: Int): Flow<List<Reward>>

    @Query("SELECT * FROM rewards WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Reward?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reward: Reward): Long

    @Update
    suspend fun update(reward: Reward)

    @Delete
    suspend fun delete(reward: Reward)

    @Insert
    suspend fun insertRedemption(redemption: RewardRedemption): Long

    @Query("SELECT COUNT(*) FROM rewards")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM rewards WHERE childId = :childId")
    suspend fun countForChild(childId: Int): Int
}
