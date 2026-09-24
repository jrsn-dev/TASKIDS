package com.example.data.local

import androidx.room.*
import com.example.model.Routine
import com.example.model.RoutineTask
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routines WHERE childId = :childId AND isActive = 1 ORDER BY id ASC")
    fun observeRoutines(childId: Long): Flow<List<Routine>>

    @Delete
    suspend fun delete(item: Routine)

    @Query("SELECT * FROM routines WHERE childId = :childId AND isActive = 1 ORDER BY id ASC")
    suspend fun listForChild(childId: Long): List<Routine>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(routine: Routine): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRoutineTask(item: RoutineTask)

    @Query("DELETE FROM routine_tasks WHERE routineId = :routineId")
    suspend fun clearRoutineTasks(routineId: Long)
}
