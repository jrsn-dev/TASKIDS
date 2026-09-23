package com.taskids.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.taskids.app.domain.model.Task
import com.taskids.app.domain.model.TaskStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task): Long

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("SELECT * FROM tasks ORDER BY childId, orderIndex ASC")
    fun observeAll(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE childId = :childId ORDER BY orderIndex ASC")
    fun observeForChild(childId: Int): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE childId = :childId ORDER BY orderIndex ASC")
    suspend fun getForChild(childId: Int): List<Task>

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Task?

    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun count(): Int

    @Query("UPDATE tasks SET status = :status, completedAt = NULL, isActive = 0 WHERE isEnabled = 1")
    suspend fun resetDailyStatuses(status: TaskStatus = TaskStatus.PENDING)

    @Query("UPDATE tasks SET status = :status, completedAt = NULL, isActive = 0 WHERE childId = :childId")
    suspend fun resetStatusesForChild(childId: Int, status: TaskStatus = TaskStatus.PENDING)

    @Query("UPDATE tasks SET status = :status, isActive = :active WHERE id = :taskId")
    suspend fun setStatus(taskId: Int, status: TaskStatus, active: Boolean)

    @Query("UPDATE tasks SET status = :status, completedAt = :completedAt, isActive = 0 WHERE id = :taskId")
    suspend fun complete(taskId: Int, status: TaskStatus, completedAt: Long)
}
