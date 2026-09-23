package com.example.data.repository

import com.example.data.local.ChildDao
import com.example.data.local.RewardDao
import com.example.data.local.RoutineDao
import com.example.data.local.TaskDao
import com.example.data.local.TaskExecutionDao
import com.example.model.Child
import com.example.model.Reward
import com.example.model.RewardRedemption
import com.example.model.Routine
import com.example.model.Task
import com.example.model.TaskExecution
import com.example.model.TaskStatus
import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val taskDao: TaskDao,
    private val childDao: ChildDao,
    private val executionDao: TaskExecutionDao,
    private val rewardDao: RewardDao,
    private val routineDao: RoutineDao
) {
    val children: Flow<List<Child>> = childDao.observeChildren()

    fun tasksForChild(childId: Long): Flow<List<Task>> = taskDao.getTasksForChild(childId)

    suspend fun insertTask(task: Task): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: Task) = taskDao.updateTask(task)

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    suspend fun getTaskById(id: Int): Task? = taskDao.getTaskById(id)

    fun getTasksByStatus(childId: Long, status: TaskStatus): Flow<List<Task>> =
        taskDao.getTasksByStatus(childId, status)

    suspend fun resetTasksForChild(childId: Long) = taskDao.resetTasksForChild(childId)

    suspend fun countTasksForChild(childId: Long): Int = taskDao.countForChild(childId)

    suspend fun childById(childId: Long): Child? = childDao.getById(childId)

    suspend fun upsertChild(child: Child): Long = childDao.upsert(child)

    suspend fun updateChild(child: Child) = childDao.update(child)

    suspend fun childrenCount(): Int = childDao.count()

    suspend fun insertExecution(execution: TaskExecution): Long = executionDao.insert(execution)

    fun executionsForChild(childId: Long): Flow<List<TaskExecution>> =
        executionDao.observeByChild(childId)

    fun completedCount(childId: Long): Flow<Int> = executionDao.observeCompletedCount(childId)

    fun earnedStars(childId: Long): Flow<Int> = executionDao.observeEarnedStars(childId)

    fun rewardsForChild(childId: Long): Flow<List<Reward>> = rewardDao.observeRewards(childId)

    suspend fun upsertReward(reward: Reward): Long = rewardDao.upsert(reward)

    suspend fun rewardsCount(): Int = rewardDao.countRewards()

    suspend fun insertRedemption(redemption: RewardRedemption): Long =
        rewardDao.insertRedemption(redemption)

    fun redemptionsForChild(childId: Long): Flow<List<RewardRedemption>> =
        rewardDao.observeRedemptions(childId)

    fun routinesForChild(childId: Long): Flow<List<Routine>> = routineDao.observeRoutines(childId)

    suspend fun upsertRoutine(routine: Routine): Long = routineDao.upsert(routine)
}
