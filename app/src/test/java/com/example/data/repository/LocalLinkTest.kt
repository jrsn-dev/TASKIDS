package com.example.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import android.content.Context
import com.example.data.local.TaskDatabase
import com.example.model.Child
import com.example.model.Reward
import com.example.model.Routine
import com.example.model.Task
import com.example.model.TaskStatus
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class LocalLinkTest {
    private lateinit var database: TaskDatabase
    private lateinit var repo: TaskRepository

    @Before fun setUp() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext<Context>(), TaskDatabase::class.java)
            .allowMainThreadQueries().build()
        repo = TaskRepository(database.taskDao(), database.childDao(), database.executionDao(),
            database.rewardDao(), database.routineDao())
    }

    @After fun tearDown() { database.close() }

    @Test fun profileTransferUpdatesExistingTasksAndPreservesLocalIds() = runBlocking {
        val id = repo.upsertChild(Child(name = "Alex", totalStars = 12))
        val taskId = repo.insertTask(Task(childId = id, title = "Ler um livro", durationMinutes = 15, orderIndex = 1))
        repo.upsertReward(Reward(childId = id, title = "Cinema", costStars = 20))
        repo.upsertRoutine(Routine(childId = id, title = "Manhã"))
        val link = LocalLink(repo)
        val payload = link.export(id).replace("\"stars\":12", "\"stars\":30")
            .replace("\"status\":\"PENDING\"", "\"status\":\"COMPLETED\"")
        val resultId = link.importProfile(payload)
        assertEquals(id, resultId)
        assertEquals(30, repo.childById(id)?.totalStars)
        assertEquals(taskId.toInt(), repo.listTasks(id).single().id)
        assertEquals(TaskStatus.COMPLETED, repo.listTasks(id).single().status)
        assertEquals(1, repo.listRewards(id).size)
        assertEquals(1, repo.listRoutines(id).size)
        link.importProfile(payload)
        assertEquals(1, repo.listTasks(id).size)
    }

    @Test fun emptyTransferredCatalogRemovesItemsWithoutRecreatingDefaults() = runBlocking {
        val id = repo.upsertChild(Child(name = "Bia"))
        repo.insertTask(Task(childId = id, title = "Antiga", durationMinutes = 10, orderIndex = 1))
        val payload = LocalLink(repo).export(id)
        val edited = org.json.JSONObject(payload).apply { put("tasks", org.json.JSONArray()) }.toString()
        LocalLink(repo).importProfile(edited)
        assertTrue(repo.listTasks(id).isEmpty())
    }
}
