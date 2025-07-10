package com.aarevalo.tasky.agenda.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aarevalo.tasky.agenda.domain.AgendaRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class PeriodicFetchAgendaWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val agendaRepository: AgendaRepository,
): CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        if(runAttemptCount >= 5){
            return Result.failure()
        }
        return when(val result = agendaRepository.fetchAgendaItems()){
            is com.aarevalo.tasky.core.domain.util.Result.Error -> {
                result.error.toWorkerResult()
            }
            is com.aarevalo.tasky.core.domain.util.Result.Success -> {
                Result.success()
            }
        }
    }
}