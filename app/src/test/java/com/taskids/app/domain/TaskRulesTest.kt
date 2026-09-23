package com.taskids.app.domain

import com.taskids.app.domain.model.Task
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TaskRulesTest {
    @Test
    fun weekdaysIncludeMondayAndExcludeSunday() {
        val task = Task(
            title = "Lição",
            durationMinutes = 20,
            orderIndex = 1,
            recurrenceMask = Task.WEEKDAYS
        )
        assertTrue(task.isScheduledFor(0))
        assertTrue(task.isScheduledFor(4))
        assertFalse(task.isScheduledFor(6))
    }

    @Test
    fun disabledTaskIsNeverScheduled() {
        val task = Task(
            title = "Banho",
            durationMinutes = 15,
            orderIndex = 1,
            isEnabled = false
        )
        assertFalse(task.isScheduledFor(0))
    }
}
