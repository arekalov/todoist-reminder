package com.arekalov.todoistreminder.data.remote.yandex

import com.arekalov.todoistreminder.data.remote.yandex.dto.CompletionOptions
import com.arekalov.todoistreminder.data.remote.yandex.dto.MessageDto
import com.arekalov.todoistreminder.data.remote.yandex.dto.YandexGptRequest
import com.arekalov.todoistreminder.data.remote.yandex.dto.YandexGptResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class YandexGptApi @Inject constructor(
    private val httpClient: HttpClient,
    private val apiKey: String,
    private val folderId: String
) {
    suspend fun sendMessage(
        messages: List<MessageDto>,
        temperature: Float = 0.7f
    ): Result<String> = runCatching {
        val request = YandexGptRequest(
            modelUri = "gpt://$folderId/yandexgpt",
            completionOptions = CompletionOptions(
                stream = false,
                temperature = temperature.toDouble(),
                maxTokens = 3000
            ),
            messages = messages
        )

        val httpResponse = httpClient.post(BASE_URL) {
            contentType(ContentType.Application.Json)
            bearerAuth(apiKey)
            setBody(request)
        }

        val response: YandexGptResponse = httpResponse.body()
        val alternative = response.result.alternatives.firstOrNull()
            ?: throw Exception("No response from API")

        alternative.message.text
    }

    companion object {
        private const val BASE_URL =
            "https://llm.api.cloud.yandex.net/foundationModels/v1/completion"
    }
}

