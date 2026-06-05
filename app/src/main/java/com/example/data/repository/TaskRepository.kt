package com.example.data.repository

import com.example.data.local.TaskDao
import com.example.model.Task
import com.example.model.TaskStatus
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()
    
    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }
    
    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }
    
    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }
    
    suspend fun getTaskById(id: Int): Task? {
        return taskDao.getTaskById(id)
    }
    
    fun getTasksByStatus(status: TaskStatus): Flow<List<Task>> {
        return taskDao.getTasksByStatus(status)
    }
    
    suspend fun deleteAllTasks() {
        taskDao.deleteAllTasks()
    }
}
