package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.model.Child
import com.example.model.Reward
import com.example.model.RewardRedemption
import com.example.model.Routine
import com.example.model.RoutineTask
import com.example.model.Task
import com.example.model.TaskExecution

@Database(
    entities = [
        Task::class,
        Child::class,
        TaskExecution::class,
        Reward::class,
        RewardRedemption::class,
        Routine::class,
        RoutineTask::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun childDao(): ChildDao
    abstract fun executionDao(): TaskExecutionDao
    abstract fun rewardDao(): RewardDao
    abstract fun routineDao(): RoutineDao

    companion object {
        @Volatile
        private var INSTANCE: TaskDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tasks ADD COLUMN childId INTEGER NOT NULL DEFAULT 1")
                db.execSQL("ALTER TABLE tasks ADD COLUMN rewardStars INTEGER NOT NULL DEFAULT 10")
                db.execSQL("ALTER TABLE tasks ADD COLUMN scheduledTime TEXT")
                db.execSQL("ALTER TABLE tasks ADD COLUMN recurrenceDays TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE tasks ADD COLUMN isRecurring INTEGER NOT NULL DEFAULT 0")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS children (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        avatarEmoji TEXT NOT NULL,
                        accentColor TEXT NOT NULL,
                        birthYear INTEGER,
                        totalStars INTEGER NOT NULL,
                        currentStreak INTEGER NOT NULL,
                        longestStreak INTEGER NOT NULL,
                        lastActiveEpochDay INTEGER,
                        isActive INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS task_executions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        taskId INTEGER NOT NULL,
                        childId INTEGER NOT NULL,
                        taskTitle TEXT NOT NULL,
                        startedAt INTEGER NOT NULL,
                        completedAt INTEGER NOT NULL,
                        plannedDurationMinutes INTEGER NOT NULL,
                        actualDurationSeconds INTEGER NOT NULL,
                        earnedStars INTEGER NOT NULL,
                        result TEXT NOT NULL
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS rewards (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        childId INTEGER NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT NOT NULL,
                        icon TEXT NOT NULL,
                        costStars INTEGER NOT NULL,
                        type TEXT NOT NULL,
                        durationMinutes INTEGER,
                        isEnabled INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS reward_redemptions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        childId INTEGER NOT NULL,
                        rewardId INTEGER NOT NULL,
                        rewardTitle TEXT NOT NULL,
                        costStars INTEGER NOT NULL,
                        redeemedAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS routines (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        childId INTEGER NOT NULL,
                        title TEXT NOT NULL,
                        icon TEXT NOT NULL,
                        startTime TEXT,
                        daysCsv TEXT NOT NULL,
                        isActive INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS routine_tasks (
                        routineId INTEGER NOT NULL,
                        taskId INTEGER NOT NULL,
                        position INTEGER NOT NULL,
                        PRIMARY KEY(routineId, taskId)
                    )
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE children ADD COLUMN totalXp INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE children ADD COLUMN currentCombo INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE children ADD COLUMN bestCombo INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE children ADD COLUMN avatarSkinTone INTEGER NOT NULL DEFAULT 2")
                db.execSQL("ALTER TABLE children ADD COLUMN avatarHairStyle INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE children ADD COLUMN avatarHairColor INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE children ADD COLUMN avatarOutfitColor INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE children ADD COLUMN gameTheme TEXT NOT NULL DEFAULT 'SKY'")

                db.execSQL("ALTER TABLE tasks ADD COLUMN iconKey TEXT NOT NULL DEFAULT 'GENERIC'")
                db.execSQL("ALTER TABLE tasks ADD COLUMN rewardXp INTEGER NOT NULL DEFAULT 100")

                db.execSQL("ALTER TABLE task_executions ADD COLUMN earnedXp INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE task_executions ADD COLUMN combo INTEGER NOT NULL DEFAULT 1")
            }
        }

        fun getDatabase(context: Context): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
