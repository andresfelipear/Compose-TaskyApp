package com.aarevalo.tasky.agenda.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aarevalo.tasky.agenda.data.local.dao.PendingItemSyncDao
import com.aarevalo.tasky.agenda.data.local.converter.MoshiAgendaItemJsonConverter
import com.aarevalo.tasky.agenda.domain.RemoteAgendaDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CreateAgendaItemWorker @Inject constructor(
    @ApplicationContext private val context: Context,
    private val params: WorkerParameters,
    private val remoteAgendaDataSource: RemoteAgendaDataSource,
    private val pendingItemSyncDao: PendingItemSyncDao,
    private val agendaItemDeserializer: MoshiAgendaItemJsonConverter
): CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        if(runAttemptCount >= 5){
            return Result.failure()
        }

        val pendingAgendaItemId = params.inputData.getString(AGENDA_ITEM_ID) ?: return Result.failure()
        val pendingAgendaItemEntity = pendingItemSyncDao.getPendingItemSyncById(pendingAgendaItemId) ?: return Result.failure()

        val agendaItem = agendaItemDeserializer.getAgendaItemFromJson(pendingAgendaItemEntity.itemJson, pendingAgendaItemEntity.itemType) ?: return Result.failure()

        return when(val result = remoteAgendaDataSource.createAgendaItem(agendaItem)){
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