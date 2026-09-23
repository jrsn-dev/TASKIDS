package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.model.TaskExecution
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskExecutionDao {
    @Insert
    suspend fun insert(execution: TaskExecution): Long

    @Query("SELECT * FROM task_executions WHERE childId = :childId ORDER BY completedAt DESC")
    fun observeByChild(childId: Long): Flow<List<TaskExecution>>

    @Query("SELECT COUNT(*) FROM task_executions WHERE childId = :childId AND result = 'COMPLETED'")
    fun observeCompletedCount(childId: Long): Flow<Int>

    @Query("SELECT COALESCE(SUM(earnedStars), 0) FROM task_executions WHERE childId = :childId")
    fun observeEarnedStars(childId: Long): Flow<Int>
}
