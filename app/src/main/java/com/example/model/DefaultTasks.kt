package com.example.model

object DefaultTasks {
    fun getDefaultTasks(): List<Task> = listOf(
        Task(
            id = 1,
            title = "Ler Livro",
            description = "Ler 20 páginas do livro escolar ou ler uma historinha",
            durationMinutes = 20,
            icon = "📚",
            orderIndex = 1
        ),
        Task(
            id = 2,
            title = "Tomar Banho",
            description = "Tomar banho de chuveiro e trocar de roupa",
            durationMinutes = 15,
            icon = "🧼",
            orderIndex = 2
        ),
        Task(
            id = 3,
            title = "Almoço",
            description = "Almoçar de forma saudável e limpar seu prato",
            durationMinutes = 30,
            icon = "🍽️",
            orderIndex = 3
        ),
        Task(
            id = 4,
            title = "Fazer Lição",
            description = "Concluir a lição de casa do dia",
            durationMinutes = 45,
            icon = "📝",
            orderIndex = 4
        )
    )
}
