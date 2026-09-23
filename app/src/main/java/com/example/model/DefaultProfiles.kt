package com.example.model

object DefaultProfiles {
    fun children(): List<Child> = listOf(
        Child(
            id = 1,
            name = "Alex",
            avatarEmoji = "🧒",
            accentColor = "#2F80ED"
        ),
        Child(
            id = 2,
            name = "Sofia",
            avatarEmoji = "👧",
            accentColor = "#9B51E0"
        )
    )

    fun rewards(): List<Reward> = listOf(
        Reward(
            id = 1,
            title = "Vídeos aprovados",
            description = "Tempo de tela dentro da área controlada",
            icon = "📺",
            costStars = 30,
            type = "SCREEN_TIME",
            durationMinutes = 20
        ),
        Reward(
            id = 2,
            title = "Escolher o filme",
            description = "Escolha o filme da família hoje",
            icon = "🍿",
            costStars = 50
        ),
        Reward(
            id = 3,
            title = "Recompensa especial",
            description = "Definida pelos responsáveis",
            icon = "🎁",
            costStars = 100
        )
    )
}
