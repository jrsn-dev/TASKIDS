package com.taskids.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.taskids.app.domain.model.AchievementUnlock
import com.taskids.app.domain.model.StarTransaction
import com.taskids.app.domain.model.TaskExecution
import com.taskids.app.domain.model.TaskStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Insert
    suspend fun insertExecution(execution: TaskExecution): Long

    @Update
    suspend fun updateExecution(execution: TaskExecution)

    @Query("SELECT * FROM task_executions WHERE id = :id LIMIT 1")
    suspend fun getExecution(id: Long): TaskExecution?

    @Query("SELECT * FROM task_executions WHERE taskId = :taskId AND dateKey = :dateKey AND status = 'COMPLETED' LIMIT 1")
    suspend fun getCompletedForDay(taskId: Int, dateKey: String): TaskExecution?

    @Query("SELECT * FROM task_executions WHERE childId = :childId ORDER BY startedAt DESC")
    fun observeForChild(childId: Int): Flow<List<TaskExecution>>

    @Query("SELECT * FROM task_executions WHERE childId = :childId AND dateKey >= :startDate AND dateKey <= :endDate ORDER BY startedAt ASC")
    suspend fun getBetween(childId: Int, startDate: String, endDate: String): List<TaskExecution>

    @Query("SELECT COUNT(*) FROM task_executions WHERE childId = :childId AND status = 'COMPLETED'")
    suspend fun countCompleted(childId: Int): Int

    @Query("SELECT DISTINCT dateKey FROM task_executions WHERE childId = :childId AND status = 'COMPLETED' ORDER BY dateKey DESC")
    suspend fun completionDates(childId: Int): List<String>

    @Insert
    suspend fun insertStarTransaction(transaction: StarTransaction): Long

    @Query("SELECT * FROM star_transactions WHERE childId = :childId ORDER BY createdAt DESC")
    fun observeStarTransactions(childId: Int): Flow<List<StarTransaction>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun unlockAchievement(unlock: AchievementUnlock): Long

    @Query("SELECT * FROM achievement_unlocks WHERE childId = :childId ORDER BY unlockedAt DESC")
    fun observeAchievements(childId: Int): Flow<List<AchievementUnlock>>

    @Query("UPDATE task_executions SET status = :status, completedAt = :completedAt, durationSeconds = :durationSeconds, starsEarned = :stars WHERE id = :executionId")
    suspend fun finishExecution(
        executionId: Long,
        status: TaskStatus,
        completedAt: Long?,
        durationSeconds: Int,
        stars: Int
    )
}
