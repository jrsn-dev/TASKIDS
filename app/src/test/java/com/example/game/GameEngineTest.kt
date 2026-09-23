package com.example.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GameEngineTest {

    @Test
    fun levelProgress_startsAtLevelOne() {
        val level = GameEngine.levelFor(0)
        assertEquals(1, level.level)
        assertEquals("Explorador(a)", level.title)
        assertEquals(0f, level.progress)
    }

    @Test
    fun levelProgress_advancesEveryFiveHundredXp() {
        val level = GameEngine.levelFor(750)
        assertEquals(2, level.level)
        assertEquals(250, level.currentXp)
        assertEquals(0.5f, level.progress)
    }

    @Test
    fun comboThree_addsBonusStars() {
        val reward = GameEngine.rewardForCompletion(
            baseStars = 10,
            baseXp = 100,
            previousCombo = 2
        )
        assertEquals(3, reward.newCombo)
        assertEquals(3, reward.comboBonusStars)
        assertEquals(13, reward.earnedStars)
        assertEquals(100, reward.earnedXp)
    }

    @Test
    fun perfectDay_returnsBonusOnlyWhenAllActiveTasksCompleted() {
        assertEquals(20 to 50, GameEngine.perfectDayBonus(4, 4))
        assertEquals(0 to 0, GameEngine.perfectDayBonus(4, 3))
        assertEquals(0 to 0, GameEngine.perfectDayBonus(0, 0))
    }

    @Test
    fun comboIsCappedAtTen() {
        val reward = GameEngine.rewardForCompletion(10, 100, 10)
        assertEquals(10, reward.newCombo)
        assertTrue(reward.comboBonusStars >= 10)
    }
}
