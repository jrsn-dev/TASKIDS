package com.example.model

object DefaultTasks {
    fun getDefaultTasks(): List<Task> = listOf(
        Task(
            id = 1,
            childId = 1,
            title = "Ler Livro",
            description = "Leia por alguns minutos e conte depois o que mais gostou.",
            durationMinutes = 20,
            icon = "📚",
            orderIndex = 1,
            rewardStars = 10,
            scheduledTime = "17:00",
            recurrenceDays = "1,2,3,4,5",
            isRecurring = true
        ),
        Task(
            id = 2,
            childId = 1,
            title = "Tomar Banho",
            description = "Hora de se cuidar e se preparar para o restante do dia.",
            durationMinutes = 15,
            icon = "🛁",
            orderIndex = 2,
            rewardStars = 10,
            scheduledTime = "18:00",
            recurrenceDays = "1,2,3,4,5,6,7",
            isRecurring = true
        ),
        Task(
            id = 3,
            childId = 1,
            title = "Almoço",
            description = "Faça sua refeição com calma e ajude a organizar depois.",
            durationMinutes = 30,
            icon = "🍽️",
            orderIndex = 3,
            rewardStars = 10
        ),
        Task(
            id = 4,
            childId = 1,
            title = "Fazer Lição",
            description = "Conclua a atividade de hoje com atenção.",
            durationMinutes = 25,
            icon = "📝",
            orderIndex = 4,
            rewardStars = 15,
            scheduledTime = "16:00",
            recurrenceDays = "1,2,3,4,5",
            isRecurring = true
        ),
        Task(
            id = 101,
            childId = 2,
            title = "Organizar Brinquedos",
            description = "Guarde cada brinquedo no seu lugar.",
            durationMinutes = 15,
            icon = "🧸",
            orderIndex = 1,
            rewardStars = 10,
            scheduledTime = "17:30",
            recurrenceDays = "1,2,3,4,5,6,7",
            isRecurring = true
        ),
        Task(
            id = 102,
            childId = 2,
            title = "Ler uma História",
            description = "Escolha uma história e aproveite esse momento.",
            durationMinutes = 20,
            icon = "📖",
            orderIndex = 2,
            rewardStars = 15,
            scheduledTime = "19:30",
            recurrenceDays = "1,2,3,4,5,6,7",
            isRecurring = true
        ),
        Task(
            id = 103,
            childId = 2,
            title = "Escovar os Dentes",
            description = "Capriche na escovação e deixe o sorriso brilhando.",
            durationMinutes = 5,
            icon = "🦷",
            orderIndex = 3,
            rewardStars = 5,
            scheduledTime = "20:30",
            recurrenceDays = "1,2,3,4,5,6,7",
            isRecurring = true
        )
    )
}
