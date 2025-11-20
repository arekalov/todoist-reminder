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
            
            // 1. Получить задачи через MCP
            val tasksResult = mcpApi.getActiveTasks()
            if (tasksResult.isFailure) {
                _uiState.value = TasksUiState.Error(
                    tasksResult.exceptionOrNull()?.message ?: "Unknown error"
                )
                return@launch
            }
            
            val rawTasks = tasksResult.getOrNull() ?: ""
            
            // Если задач нет
            if (rawTasks.contains("No active tasks")) {
                _uiState.value = TasksUiState.Success("✅ У вас нет активных задач на сегодня!")
                return@launch
            }
            
            // 2. Форматировать через Yandex GPT
            val formattedResult = formatTasksUseCase(rawTasks)
            if (formattedResult.isFailure) {
                _uiState.value = TasksUiState.Error(
                    formattedResult.exceptionOrNull()?.message ?: "Formatting error"
                )
                return@launch
            }
            
            _uiState.value = TasksUiState.Success(
                formattedText = formattedResult.getOrNull() ?: ""
            )
        }
    }
}

sealed class TasksUiState {
    object Initial : TasksUiState()
    object Loading : TasksUiState()
    data class Success(val formattedText: String) : TasksUiState()
    data class Error(val message: String) : TasksUiState()
}

