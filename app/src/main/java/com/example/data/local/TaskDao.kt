package com.example.data.local

import androidx.room.*
import com.example.model.Task
import com.example.model.TaskStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)
    
    @Update
    suspend fun updateTask(task: Task)
    
    @Delete
    suspend fun deleteTask(task: Task)
    
    @Query("SELECT * FROM tasks ORDER BY orderIndex ASC")
    fun getAllTasks(): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Int): Task?
    
    @Query("SELECT * FROM tasks WHERE status = :status ORDER BY orderIndex ASC")
    fun getTasksByStatus(status: TaskStatus): Flow<List<Task>>
    
    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()
}
