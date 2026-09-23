package com.taskids.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.taskids.app.domain.model.ApprovedContent
import kotlinx.coroutines.flow.Flow

@Dao
interface ApprovedContentDao {
    @Query("SELECT * FROM approved_content WHERE isEnabled = 1 ORDER BY id DESC")
    fun observeEnabled(): Flow<List<ApprovedContent>>

    @Query("SELECT * FROM approved_content ORDER BY id DESC")
    fun observeAll(): Flow<List<ApprovedContent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(content: ApprovedContent): Long

    @Update
    suspend fun update(content: ApprovedContent)

    @Delete
    suspend fun delete(content: ApprovedContent)
}
