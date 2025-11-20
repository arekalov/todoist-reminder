package com.arekalov.todoistreminder.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arekalov.todoistreminder.domain.model.ReminderSettings
import com.arekalov.todoistreminder.domain.repository.ReminderPreferencesRepository
import com.arekalov.todoistreminder.domain.usecase.ScheduleRemindersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: ReminderPreferencesRepository,
    private val scheduleRemindersUseCase: ScheduleRemindersUseCase
) : ViewModel() {
    
    private val _settings = MutableStateFlow(ReminderSettings())
    val settings: StateFlow<ReminderSettings> = _settings.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            _settings.value = preferencesRepository.getSettings()
        }
    }
    
    fun updateStartHour(hour: Int) {
        viewModelScope.launch {
            val newSettings = _settings.value.copy(startHour = hour)
            preferencesRepository.saveSettings(newSettings)
            _settings.value = newSettings
            rescheduleIfEnabled()
        }
    }
    
    fun updateEndHour(hour: Int) {
        viewModelScope.launch {
            val newSettings = _settings.value.copy(endHour = hour)
            preferencesRepository.saveSettings(newSettings)
            _settings.value = newSettings
            rescheduleIfEnabled()
        }
    }
    
    fun updateInterval(minutes: Int) {
        viewModelScope.launch {
            val newSettings = _settings.value.copy(intervalMinutes = minutes)
            preferencesRepository.saveSettings(newSettings)
            _settings.value = newSettings
            rescheduleIfEnabled()
        }
    }
    
    fun toggleEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val newSettings = _settings.value.copy(enabled = enabled)
            preferencesRepository.saveSettings(newSettings)
            _settings.value = newSettings
            scheduleRemindersUseCase(newSettings)
        }
    }
    
    private fun rescheduleIfEnabled() {
        if (_settings.value.enabled) {
            viewModelScope.launch {
                scheduleRemindersUseCase(_settings.value)
            }
        }
    }
}

