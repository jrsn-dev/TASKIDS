package com.example.game

import kotlin.math.max

data class LevelProgress(
    val level: Int,
    val title: String,
    val currentXp: Int,
    val xpForCurrentLevel: Int,
    val xpForNextLevel: Int,
    val progress: Float
)

data class MissionReward(
    val baseStars: Int,
    val comboBonusStars: Int,
    val earnedStars: Int,
    val earnedXp: Int,
    val newCombo: Int
)

object GameEngine {
    private val levelTitles = listOf(
        "Explorador(a)",
        "Descobridor(a)",
        "Aventureiro(a)",
        "Guardião(ã)",
        "Mestre das Missões",
        "Lenda TASKIDS"
    )

    fun levelFor(totalXp: Int): LevelProgress {
        val safeXp = totalXp.coerceAtLeast(0)
        val level = (safeXp / 500) + 1
        val currentStart = (level - 1) * 500
        val next = level * 500
        val within = safeXp - currentStart
        return LevelProgress(
            level = level,
            title = levelTitles.getOrElse(level - 1) { levelTitles.last() },
            currentXp = within,
            xpForCurrentLevel = currentStart,
            xpForNextLevel = next,
            progress = (within / 500f).coerceIn(0f, 1f)
        )
    }

    fun rewardForCompletion(
        baseStars: Int,
        baseXp: Int,
        previousCombo: Int
    ): MissionReward {
        val newCombo = (previousCombo + 1).coerceAtMost(10)
        val comboBonus = when {
            newCombo >= 10 -> 10
            newCombo >= 7 -> 7
            newCombo >= 5 -> 5
            newCombo >= 3 -> 3
            else -> 0
        }
        return MissionReward(
            baseStars = baseStars,
            comboBonusStars = comboBonus,
            earnedStars = max(1, baseStars + comboBonus),
            earnedXp = max(10, baseXp),
            newCombo = newCombo
        )
    }

    fun perfectDayBonus(activeTaskCount: Int, completedTaskCount: Int): Pair<Int, Int> {
        return if (activeTaskCount > 0 && activeTaskCount == completedTaskCount) {
            20 to 50
        } else {
            0 to 0
        }
    }
}
