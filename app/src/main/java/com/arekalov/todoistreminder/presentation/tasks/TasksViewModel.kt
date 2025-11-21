package com.arekalov.todoistreminder.presentation.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arekalov.todoistreminder.data.remote.mcp.McpApi
import com.arekalov.todoistreminder.domain.usecase.FormatTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val mcpApi: McpApi,
    private val formatTasksUseCase: FormatTasksUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<TasksUiState>(TasksUiState.Initial)
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()
    
    fun loadTasks() {
        viewModelScope.launch {
            _uiState.value = TasksUiState.Loading
            
            // 1. Получить дневную сводку (задачи + анекдот) через MCP композитор
            val summaryResult = mcpApi.getDailySummary()
            if (summaryResult.isFailure) {
                _uiState.value = TasksUiState.Error(
                    summaryResult.exceptionOrNull()?.message ?: "Unknown error"
                )
                return@launch
            }
            
            val summary = summaryResult.getOrNull() ?: run {
                _uiState.value = TasksUiState.Error("Failed to get summary")
                return@launch
            }
            
            // Проверка на отсутствие задач
            val hasNoTasks = summary.tasks.contains("No active tasks") || summary.tasks.isBlank()
            
            if (hasNoTasks) {
                // Форматируем только анекдот
                val formattedJokeResult = formatTasksUseCase("Анекдот дня:\n${summary.joke}")
                val formattedJoke = formattedJokeResult.getOrNull() ?: summary.joke
                
                _uiState.value = TasksUiState.Success(
                    joke = formattedJoke,
                    tasks = "✅ У вас нет активных задач на сегодня!"
                )
                return@launch
            }
            
            // 2. Форматировать задачи через Yandex GPT
            val formattedTasksResult = formatTasksUseCase(summary.tasks)
            if (formattedTasksResult.isFailure) {
                _uiState.value = TasksUiState.Error(
                    formattedTasksResult.exceptionOrNull()?.message ?: "Formatting error"
                )
                return@launch
            }
            
            // 3. Форматировать анекдот через Yandex GPT
            val formattedJokeResult = formatTasksUseCase("Анекдот дня:\n${summary.joke}")
            
            _uiState.value = TasksUiState.Success(
                joke = formattedJokeResult.getOrNull() ?: summary.joke,
                tasks = formattedTasksResult.getOrNull() ?: summary.tasks
            )
        }
    }
}

sealed class TasksUiState {
    object Initial : TasksUiState()
    object Loading : TasksUiState()
    data class Success(val joke: String, val tasks: String) : TasksUiState()
    data class Error(val message: String) : TasksUiState()
}

