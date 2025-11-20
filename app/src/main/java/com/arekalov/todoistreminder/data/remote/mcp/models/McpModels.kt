package com.arekalov.todoistreminder.data.remote.mcp.models

import kotlinx.serialization.Serializable

@Serializable
data class McpToolCallRequest(
    val name: String,
    val arguments: Map<String, String> = emptyMap()
)

@Serializable
data class McpToolCallResponse(
    val content: List<McpContent>,
    val isError: Boolean = false
)

@Serializable
data class McpContent(
    val type: String = "text",
    val text: String
)

