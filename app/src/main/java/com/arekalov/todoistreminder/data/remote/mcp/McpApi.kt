package com.arekalov.todoistreminder.data.remote.mcp

import com.arekalov.todoistreminder.data.remote.mcp.models.DailySummary
import com.arekalov.todoistreminder.data.remote.mcp.models.McpContent
import com.arekalov.todoistreminder.data.remote.mcp.models.McpToolCallRequest
import com.arekalov.todoistreminder.data.remote.mcp.models.McpToolCallResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class McpApi @Inject constructor(
    private val httpClient: HttpClient,
    private val mcpServerUrl: String
) {
    suspend fun callTool(toolName: String): Result<String> = runCatching {
        val request = McpToolCallRequest(
            name = toolName,
            arguments = emptyMap()
        )
        
        val response: McpToolCallResponse = httpClient.post("$mcpServerUrl/tools/call") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
        
        if (response.isError) {
            throw Exception(response.content?.firstOrNull()?.text ?: "Unknown error")
        }
        
        response.content?.firstOrNull()?.text ?: ""
    }
    
    suspend fun getActiveTasks(): Result<String> = callTool("get_active_tasks")
    
    /**
     * Получает дневную сводку от композитора: задачи + анекдот
     */
    suspend fun getDailySummary(): Result<DailySummary> = runCatching {
        val request = McpToolCallRequest(
            name = "get_daily_summary",
            arguments = emptyMap()
        )
        
        val response: McpToolCallResponse = httpClient.post("$mcpServerUrl/mcp/tools/call") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
        
        // Композитор использует format {result, error}
        if (response.error != null) {
            throw Exception(response.error)
        }
        
        val fullText = response.result ?: ""
        
        // Парсим текстовый результат
        // Формат: "🎯 ДНЕВНАЯ СВОДКА\n\n😄 АНЕКДОТ ДНЯ:\n{joke}\n\n📋 ЗАДАЧИ НА СЕГОДНЯ:\n{tasks}"
        val parts = fullText.split("📋 ЗАДАЧИ НА СЕГОДНЯ:")
        
        val joke = if (parts.isNotEmpty()) {
            val jokeSection = parts[0]
                .substringAfter("😄 АНЕКДОТ ДНЯ:")
                .trim()
            jokeSection
        } else {
            ""
        }
        
        val tasks = if (parts.size > 1) {
            parts[1].trim()
        } else {
            ""
        }
        
        DailySummary(tasks = tasks, joke = joke)
    }
}

