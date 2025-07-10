package com.aarevalo.tasky.agenda.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aarevalo.tasky.agenda.domain.AgendaRepository
import com.aarevalo.tasky.core.domain.preferences.SessionStorage
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import timber.log.Timber

@HiltWorker
class RescheduleAlarmsWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val agendaRepository: AgendaRepository,
    private val sessionStorage: SessionStorage
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        if (runAttemptCount >= 5) {
            Timber.w("RescheduleAlarmsWorker failed after %d attempts. Giving up.", runAttemptCount)
            return Result.failure()
        }

        Timber.d("RescheduleAlarmsWorker started (attempt %d).", runAttemptCount)

        val currentUserId = sessionStorage.getSession()?.userId
        if (currentUserId == null) {
            Timber.w("RescheduleAlarmsWorker: No user logged in. Skipping alarm rescheduling.")
            return Result.success()
        }

        return try {

            val allAgendaItems = agendaRepository.getAllAgendaItems().first()
            Timber.d("RescheduleAlarmsWorker: Found %d local agenda items for user %s.", allAgendaItems.size, currentUserId)

            var successfullyScheduledCount = 0
            for (item in allAgendaItems) {
                agendaRepository.scheduleReminder(item)
                successfullyScheduledCount++
            }

            Timber.d("RescheduleAlarmsWorker finished. Processed %d local items.", successfullyScheduledCount)
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "RescheduleAlarmsWorker failed to reschedule alarms due to an unexpected exception. Retrying...")
            Result.retry()
        }
    }

    companion object {
        const val TAG = "RescheduleAlarmsWorker"
    }
}