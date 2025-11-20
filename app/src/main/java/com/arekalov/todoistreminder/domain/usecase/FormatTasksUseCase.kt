package com.arekalov.todoistreminder.domain.usecase

import com.arekalov.todoistreminder.data.remote.yandex.YandexGptApi
import com.arekalov.todoistreminder.data.remote.yandex.dto.MessageDto
import javax.inject.Inject

class FormatTasksUseCase @Inject constructor(
    private val yandexGptApi: YandexGptApi
) {
    suspend operator fun invoke(tasksText: String): Result<String> = runCatching {
        val prompt = """
        Отформатируй список задач из Todoist. 
        Отсортируй по приоритету и важности.
        Выдели самые срочные задачи.
        Сделай текст кратким и удобным для чтения.
        
        Задачи:
        $tasksText
        """.trimIndent()
        
        val messages = listOf(
            MessageDto(role = "user", text = prompt)
        )
        
        yandexGptApi.sendMessage(messages, temperature = 0.3f)
            .getOrThrow()
    }
}

