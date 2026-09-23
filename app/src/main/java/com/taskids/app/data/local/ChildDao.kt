package com.taskids.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.taskids.app.domain.model.ChildProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface ChildDao {
    @Query("SELECT * FROM children WHERE isActive = 1 ORDER BY createdAt ASC")
    fun observeActive(): Flow<List<ChildProfile>>

    @Query("SELECT * FROM children ORDER BY createdAt ASC")
    suspend fun getAll(): List<ChildProfile>

    @Query("SELECT * FROM children WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): ChildProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: ChildProfile): Long

    @Update
    suspend fun update(profile: ChildProfile)

    @Query("SELECT COUNT(*) FROM children")
    suspend fun count(): Int
}
