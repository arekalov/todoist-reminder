package com.arekalov.todoistreminder.domain.repository

import com.arekalov.todoistreminder.domain.model.ReminderSettings

interface ReminderPreferencesRepository {
    suspend fun getSettings(): ReminderSettings
    suspend fun saveSettings(settings: ReminderSettings)
}

