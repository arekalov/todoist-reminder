package com.arekalov.todoistreminder.data.remote.mcp

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
            throw Exception(response.content.firstOrNull()?.text ?: "Unknown error")
        }
        
        response.content.firstOrNull()?.text ?: ""
    }
    
    suspend fun getActiveTasks(): Result<String> = callTool("get_active_tasks")
}

