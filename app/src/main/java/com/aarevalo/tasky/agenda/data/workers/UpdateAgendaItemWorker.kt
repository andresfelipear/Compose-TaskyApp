package com.aarevalo.tasky.agenda.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aarevalo.tasky.agenda.data.local.dao.PendingItemSyncDao
import com.aarevalo.tasky.agenda.domain.RemoteAgendaDataSource
import com.aarevalo.tasky.agenda.domain.util.AgendaItemJsonConverter
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class UpdateAgendaItemWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val remoteAgendaDataSource: RemoteAgendaDataSource,
    private val pendingItemSyncDao: PendingItemSyncDao,
    private val agendaItemJsonConverter: AgendaItemJsonConverter
): CoroutineWorker(context, params) {

    init {
        Timber.d("UpdateAgendaItemWorker created via Hilt injection")
    }

    override suspend fun doWork(): Result {
        if(runAttemptCount >= 5){
            return Result.failure()
        }

        val pendingAgendaItemId = params.inputData.getString(AGENDA_ITEM_ID) ?: return Result.failure()
        Timber.d("UpdateAgendaItemWorker: pendingAgendaItemId: $pendingAgendaItemId")

        val pendingAgendaItemEntity = pendingItemSyncDao.getPendingItemSyncById(pendingAgendaItemId)?: run {
            Timber.e("UpdateAgendaItemWorker: Missing agendaItemId in inputData.")
            return Result.failure()
        }

        val agendaItem = agendaItemJsonConverter.getAgendaItemFromJson(pendingAgendaItemEntity.itemJson, pendingAgendaItemEntity.itemType) ?: return Result.failure()

        return when(val result = remoteAgendaDataSource.updateAgendaItem(
            agendaItem = agendaItem,
            deletedPhotoKeys = pendingAgendaItemEntity.deletedPhotoKeys,
            isGoing = pendingAgendaItemEntity.isGoing
        )){
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
    }

}