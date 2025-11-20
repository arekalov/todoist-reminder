package com.arekalov.todoistreminder.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.arekalov.todoistreminder.data.remote.mcp.McpApi
import com.arekalov.todoistreminder.domain.usecase.FormatTasksUseCase
import com.arekalov.todoistreminder.notifications.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val mcpApi: McpApi,
    private val formatTasksUseCase: FormatTasksUseCase,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        // Получить задачи через MCP
        val tasksResult = mcpApi.getActiveTasks()
        if (tasksResult.isFailure) {
            return Result.retry()
        }
        
        val rawTasks = tasksResult.getOrNull() ?: return Result.success()
        
        // Если задач нет, не показываем уведомление
        if (rawTasks.contains("No active tasks")) {
            return Result.success()
        }
        
        // Форматировать через Yandex GPT
        val formattedResult = formatTasksUseCase(rawTasks)
        val formattedText = formattedResult.getOrNull() ?: rawTasks
        
        // Показать уведомление
        notificationHelper.showTasksNotification(formattedText)
        
        return Result.success()
    }
}

