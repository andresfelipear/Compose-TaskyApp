package com.aarevalo.tasky.agenda.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aarevalo.tasky.agenda.domain.AgendaRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class PeriodicFetchAgendaWorker @Inject constructor(
    @ApplicationContext private val context: Context,
    params: WorkerParameters,
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