package com.taskids.app.data.local

import com.taskids.app.domain.model.ChildProfile
import com.taskids.app.domain.model.Reward
import com.taskids.app.domain.model.RewardType
import com.taskids.app.domain.model.RoutinePeriod
import com.taskids.app.domain.model.Task

object DefaultData {
    fun children(): List<ChildProfile> = listOf(
        ChildProfile(name = "Alex", age = 8, avatar = "👦", accentColorHex = "#3B82F6"),
        ChildProfile(name = "Luna", age = 7, avatar = "👧", accentColorHex = "#A855F7")
    )

    fun tasks(childId: Int): List<Task> = listOf(
        Task(
            childId = childId,
            title = "Ler Livro",
            description = "Separe alguns minutos para ler uma história ou um livro escolar.",
            durationMinutes = 20,
            icon = "📚",
            orderIndex = 1,
            rewardPoints = 10,
            routinePeriod = RoutinePeriod.AFTERNOON
        ),
        Task(
            childId = childId,
            title = "Tomar Banho",
            description = "Hora de se cuidar e colocar uma roupa confortável.",
            durationMinutes = 15,
            icon = "🛁",
            orderIndex = 2,
            rewardPoints = 10,
            routinePeriod = RoutinePeriod.EVENING
        ),
        Task(
            childId = childId,
            title = "Organizar",
            description = "Guarde brinquedos, materiais e deixe seu espaço em ordem.",
            durationMinutes = 10,
            icon = "🧸",
            orderIndex = 3,
            rewardPoints = 10,
            routinePeriod = RoutinePeriod.EVENING
        ),
        Task(
            childId = childId,
            title = "Fazer Lição",
            description = "Conclua a atividade escolar do dia com calma e atenção.",
            durationMinutes = 30,
            icon = "📝",
            orderIndex = 4,
            rewardPoints = 15,
            recurrenceMask = Task.WEEKDAYS,
            routinePeriod = RoutinePeriod.AFTERNOON
        )
    )

    fun rewards(childId: Int): List<Reward> = listOf(
        Reward(
            childId = childId,
            title = "20 min de vídeos",
            description = "Escolha um conteúdo aprovado pelos responsáveis.",
            icon = "📺",
            costStars = 30,
            type = RewardType.SCREEN_TIME,
            valueMinutes = 20
        ),
        Reward(
            childId = childId,
            title = "Escolher o filme",
            description = "Você escolhe o filme da família hoje.",
            icon = "🍿",
            costStars = 50,
            type = RewardType.ACTIVITY
        ),
        Reward(
            childId = childId,
            title = "Recompensa especial",
            description = "Combine uma recompensa especial com um responsável.",
            icon = "🎁",
            costStars = 100,
            type = RewardType.CUSTOM
        )
    )
}
