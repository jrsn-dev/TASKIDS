package com.taskids.app.domain

import com.taskids.app.data.repository.KidsRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RepositoryRulesTest {
    @Test
    fun extractsYoutubeVideoIdFromCommonUrls() {
        assertEquals(
            "dQw4w9WgXcQ",
            KidsRepository.extractYoutubeVideoId("https://www.youtube.com/watch?v=dQw4w9WgXcQ")
        )
        assertEquals(
            "dQw4w9WgXcQ",
            KidsRepository.extractYoutubeVideoId("https://youtu.be/dQw4w9WgXcQ")
        )
        assertNull(KidsRepository.extractYoutubeVideoId("https://example.com/video"))
    }

    @Test
    fun streakRequiresConsecutiveDatesEndingToday() {
        val dates = listOf("2026-09-23", "2026-09-22", "2026-09-21")
        assertEquals(3, KidsRepository.calculateStreak(dates, "2026-09-23"))
        assertEquals(0, KidsRepository.calculateStreak(dates, "2026-09-24"))
    }
}
