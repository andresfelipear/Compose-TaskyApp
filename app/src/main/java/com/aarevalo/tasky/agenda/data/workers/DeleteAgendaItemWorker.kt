package com.aarevalo.tasky.agenda.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aarevalo.tasky.agenda.data.local.dao.PendingItemSyncDao
import com.aarevalo.tasky.agenda.domain.RemoteAgendaDataSource
import com.aarevalo.tasky.agenda.domain.model.AgendaItemType
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DeleteAgendaItemWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val remoteAgendaDataSource: RemoteAgendaDataSource,
    private val pendingItemSyncDao: PendingItemSyncDao,
): CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        if(runAttemptCount >= 5){
            return Result.failure()
        }

        val pendingAgendaItemId = params.inputData.getString(AGENDA_ITEM_ID) ?: return Result.failure()

        val pendingAgendaItemType = params.inputData.getString(AGENDA_ITEM_TYPE) ?: return Result.failure()

        return when(val result = remoteAgendaDataSource.deleteAgendaItem(pendingAgendaItemId, AgendaItemType.valueOf(pendingAgendaItemType))){
            is com.aarevalo.tasky.core.domain.util.Result.Success -> {
                pendingItemSyncDao.deletePendingItemSyncById(pendingAgendaItemId)
                Result.success()
            }
            is com.aarevalo.tasky.core.domain.util.Result.Error -> {
                result.error.toWorkerResult()
            }
        }
    }

    companion object {
        const val AGENDA_ITEM_ID = "AGENDA_ITEM_ID"
        const val AGENDA_ITEM_TYPE = "AGENDA_ITEM_TYPE"
    }

}