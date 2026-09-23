package com.example.data.local

import androidx.room.*
import com.example.model.Task
import com.example.model.TaskStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM tasks WHERE childId = :childId ORDER BY orderIndex ASC")
    fun getTasksForChild(childId: Long): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Int): Task?

    @Query("SELECT * FROM tasks WHERE childId = :childId AND status = :status ORDER BY orderIndex ASC")
    fun getTasksByStatus(childId: Long, status: TaskStatus): Flow<List<Task>>

    @Query("UPDATE tasks SET status = 'PENDING', completedAt = NULL WHERE childId = :childId")
    suspend fun resetTasksForChild(childId: Long)

    @Query("SELECT COUNT(*) FROM tasks WHERE childId = :childId")
    suspend fun countForChild(childId: Long): Int

    @Query("DELETE FROM tasks WHERE childId = :childId")
    suspend fun deleteAllTasksForChild(childId: Long)
}
