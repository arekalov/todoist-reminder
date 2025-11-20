package com.arekalov.todoistreminder.domain.usecase

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.arekalov.todoistreminder.domain.model.ReminderSettings
import com.arekalov.todoistreminder.workers.ReminderWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ScheduleRemindersUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(settings: ReminderSettings) {
        val workManager = WorkManager.getInstance(context)
        
        // Отменить все предыдущие
        workManager.cancelAllWorkByTag(REMINDER_WORK_TAG)
        
        if (!settings.enabled) return
        
        // Создать новый periodic work
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        
        val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
            settings.intervalMinutes.toLong(), TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .addTag(REMINDER_WORK_TAG)
            .build()
        
        workManager.enqueue(workRequest)
    }
    
    companion object {
        const val REMINDER_WORK_TAG = "reminder_work"
    }
}

