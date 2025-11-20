package com.arekalov.todoistreminder.domain.model

data class ReminderSettings(
    val startHour: Int = 8,
    val endHour: Int = 19,
    val intervalMinutes: Int = 60,
    val enabled: Boolean = false
)

