package com.taskids.app.domain.model

object AchievementCatalog {
    val definitions = listOf(
        AchievementDefinition(
            id = "first_mission",
            title = "Primeira missão",
            description = "Concluiu a primeira tarefa.",
            icon = "🚀"
        ),
        AchievementDefinition(
            id = "ten_missions",
            title = "Super ajudante",
            description = "Concluiu 10 tarefas.",
            icon = "🏅"
        ),
        AchievementDefinition(
            id = "hundred_stars",
            title = "Colecionador de estrelas",
            description = "Conquistou 100 estrelas.",
            icon = "⭐"
        ),
        AchievementDefinition(
            id = "perfect_day",
            title = "Dia perfeito",
            description = "Concluiu todas as tarefas programadas do dia.",
            icon = "🎯"
        ),
        AchievementDefinition(
            id = "seven_day_streak",
            title = "Sequência incrível",
            description = "Manteve a rotina por 7 dias seguidos.",
            icon = "🔥"
        )
    )
}
