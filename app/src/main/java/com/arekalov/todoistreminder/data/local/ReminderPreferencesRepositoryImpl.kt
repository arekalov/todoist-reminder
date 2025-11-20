package com.arekalov.todoistreminder.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.arekalov.todoistreminder.domain.model.ReminderSettings
import com.arekalov.todoistreminder.domain.repository.ReminderPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ReminderPreferencesRepository {
    
    private object Keys {
        val START_HOUR = intPreferencesKey("start_hour")
        val END_HOUR = intPreferencesKey("end_hour")
        val INTERVAL_MINUTES = intPreferencesKey("interval_minutes")
        val ENABLED = booleanPreferencesKey("enabled")
    }
    
    override suspend fun getSettings(): ReminderSettings {
        return dataStore.data.map { prefs ->
            ReminderSettings(
                startHour = prefs[Keys.START_HOUR] ?: 8,
                endHour = prefs[Keys.END_HOUR] ?: 19,
                intervalMinutes = prefs[Keys.INTERVAL_MINUTES] ?: 60,
                enabled = prefs[Keys.ENABLED] ?: false
            )
        }.first()
    }
    
    override suspend fun saveSettings(settings: ReminderSettings) {
        dataStore.edit { prefs ->
            prefs[Keys.START_HOUR] = settings.startHour
            prefs[Keys.END_HOUR] = settings.endHour
            prefs[Keys.INTERVAL_MINUTES] = settings.intervalMinutes
            prefs[Keys.ENABLED] = settings.enabled
        }
    }
}

