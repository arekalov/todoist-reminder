package com.arekalov.todoistreminder.data.remote.mcp.models

import kotlinx.serialization.Serializable

@Serializable
data class McpToolCallRequest(
    val name: String,
    val arguments: Map<String, String> = emptyMap()
)

@Serializable
data class McpToolCallResponse(
    val content: List<McpContent>? = null,  // Для simpletodoistmcp (старый формат)
    val isError: Boolean = false,
    val result: String? = null,  // Для композитора (новый формат)
    val error: String? = null
)

@Serializable
data class McpContent(
    val type: String = "text",
    val text: String
)

// Модель для парсинга ответа от композитора
data class DailySummary(
    val tasks: String,
    val joke: String
)

