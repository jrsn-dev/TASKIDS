package com.example.model

object DefaultProfiles {
    fun children(): List<Child> = listOf(
        Child(
            id = 1,
            name = "Alex",
            accentColor = "#2F80ED",
            avatarSkinTone = 2,
            avatarHairStyle = 0,
            avatarHairColor = 0,
            avatarOutfitColor = 0,
            avatarCharacter = "BOY",
            gameTheme = "SKY"
        ),
        Child(
            id = 2,
            name = "Sofia",
            accentColor = "#9B51E0",
            avatarSkinTone = 3,
            avatarHairStyle = 2,
            avatarHairColor = 1,
            avatarOutfitColor = 2,
            avatarCharacter = "GIRL",
            gameTheme = "FOREST"
        )
    )

    fun rewards(): List<Reward> = listOf(
        Reward(
            id = 1,
            title = "Vídeos aprovados",
            description = "Tempo de tela dentro da área controlada",
            icon = "",
            costStars = 30,
            type = "SCREEN_TIME",
            durationMinutes = 20
        ),
        Reward(
            id = 2,
            title = "Escolher o filme",
            description = "Escolha o filme da família hoje",
            icon = "",
            costStars = 50,
            type = "MOVIE"
        ),
        Reward(
            id = 3,
            title = "Recompensa especial",
            description = "Definida pelos responsáveis",
            icon = "",
            costStars = 100,
            type = "CUSTOM"
        )
    )
}
