package com.arekalov.todoistreminder.data.remote.yandex.dto

import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(
    val role: String,
    val text: String = ""
)

@Serializable
data class CompletionOptions(
    val stream: Boolean = false,
    val temperature: Double = 0.8,
    val maxTokens: Int = 3000
)

@Serializable
data class YandexGptRequest(
    val modelUri: String,
    val completionOptions: CompletionOptions,
    val messages: List<MessageDto>
)

@Serializable
data class YandexGptResponse(
    val result: ResultData
)

@Serializable
data class ResultData(
    val alternatives: List<Alternative>,
    val usage: Usage,
    val modelVersion: String
)

@Serializable
data class Alternative(
    val message: MessageDto,
    val status: String
)

@Serializable
data class Usage(
    val inputTextTokens: String,
    val completionTokens: String,
    val totalTokens: String
)

