package com.taskids.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.taskids.app.domain.model.AchievementUnlock
import com.taskids.app.domain.model.ApprovedContent
import com.taskids.app.domain.model.ChildProfile
import com.taskids.app.domain.model.Reward
import com.taskids.app.domain.model.RewardRedemption
import com.taskids.app.domain.model.StarTransaction
import com.taskids.app.domain.model.Task
import com.taskids.app.domain.model.TaskExecution

@Database(
    entities = [
        Task::class,
        ChildProfile::class,
        TaskExecution::class,
        StarTransaction::class,
        AchievementUnlock::class,
        Reward::class,
        RewardRedemption::class,
        ApprovedContent::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun childDao(): ChildDao
    abstract fun historyDao(): HistoryDao
    abstract fun rewardDao(): RewardDao
    abstract fun approvedContentDao(): ApprovedContentDao

    companion object {
        @Volatile
        private var INSTANCE: TaskDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tasks ADD COLUMN childId INTEGER NOT NULL DEFAULT 1")
                db.execSQL("ALTER TABLE tasks ADD COLUMN rewardPoints INTEGER NOT NULL DEFAULT 10")
                db.execSQL("ALTER TABLE tasks ADD COLUMN recurrenceMask INTEGER NOT NULL DEFAULT 127")
                db.execSQL("ALTER TABLE tasks ADD COLUMN routinePeriod TEXT NOT NULL DEFAULT 'ANYTIME'")
                db.execSQL("ALTER TABLE tasks ADD COLUMN isEnabled INTEGER NOT NULL DEFAULT 1")
                db.execSQL("ALTER TABLE tasks ADD COLUMN dueHour INTEGER")
                db.execSQL("ALTER TABLE tasks ADD COLUMN dueMinute INTEGER")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS children (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        age INTEGER NOT NULL,
                        avatar TEXT NOT NULL,
                        accentColorHex TEXT NOT NULL,
                        totalStars INTEGER NOT NULL DEFAULT 0,
                        level INTEGER NOT NULL DEFAULT 1,
                        isActive INTEGER NOT NULL DEFAULT 1,
                        createdAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS task_executions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        taskId INTEGER NOT NULL,
                        childId INTEGER NOT NULL,
                        dateKey TEXT NOT NULL,
                        startedAt INTEGER NOT NULL,
                        completedAt INTEGER,
                        durationSeconds INTEGER NOT NULL,
                        status TEXT NOT NULL,
                        starsEarned INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_task_executions_taskId ON task_executions(taskId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_task_executions_childId ON task_executions(childId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_task_executions_childId_dateKey ON task_executions(childId, dateKey)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS star_transactions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        childId INTEGER NOT NULL,
                        amount INTEGER NOT NULL,
                        reason TEXT NOT NULL,
                        referenceType TEXT NOT NULL,
                        referenceId INTEGER,
                        dateKey TEXT NOT NULL,
                        createdAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_star_transactions_childId ON star_transactions(childId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_star_transactions_dateKey ON star_transactions(dateKey)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS achievement_unlocks (
                        achievementId TEXT NOT NULL,
                        childId INTEGER NOT NULL,
                        unlockedAt INTEGER NOT NULL,
                        PRIMARY KEY(achievementId, childId)
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_achievement_unlocks_childId ON achievement_unlocks(childId)")

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
                        valueMinutes INTEGER NOT NULL,
                        isEnabled INTEGER NOT NULL DEFAULT 1
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_rewards_childId ON rewards(childId)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS reward_redemptions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        rewardId INTEGER NOT NULL,
                        childId INTEGER NOT NULL,
                        redeemedAt INTEGER NOT NULL,
                        costStars INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_reward_redemptions_childId ON reward_redemptions(childId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_reward_redemptions_rewardId ON reward_redemptions(rewardId)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS approved_content (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        title TEXT NOT NULL,
                        youtubeVideoId TEXT NOT NULL,
                        thumbnailEmoji TEXT NOT NULL,
                        isEnabled INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                val now = System.currentTimeMillis()
                db.execSQL(
                    "INSERT OR IGNORE INTO children(id, name, age, avatar, accentColorHex, totalStars, level, isActive, createdAt) VALUES(1, 'Alex', 8, '👦', '#3B82F6', 0, 1, 1, $now)"
                )
                db.execSQL(
                    "INSERT OR IGNORE INTO children(id, name, age, avatar, accentColorHex, totalStars, level, isActive, createdAt) VALUES(2, 'Luna', 7, '👧', '#A855F7', 0, 1, 1, ${now + 1})"
                )
            }
        }

        fun getDatabase(context: Context): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
