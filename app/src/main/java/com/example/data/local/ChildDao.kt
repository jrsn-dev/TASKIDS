package com.example.data.local

import androidx.room.*
import com.example.model.Child
import kotlinx.coroutines.flow.Flow

@Dao
interface ChildDao {
    @Query("SELECT * FROM children WHERE isActive = 1 ORDER BY id ASC")
    fun observeChildren(): Flow<List<Child>>

    @Query("SELECT * FROM children WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): Child?

    @Query("SELECT * FROM children WHERE isActive = 1 AND name = :name LIMIT 1")
    suspend fun findActiveByName(name: String): Child?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(child: Child): Long

    @Update
    suspend fun update(child: Child)

    @Query("SELECT COUNT(*) FROM children")
    suspend fun count(): Int
}
