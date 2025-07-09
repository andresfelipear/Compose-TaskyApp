package com.aarevalo.tasky.agenda.data

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.await
import com.aarevalo.tasky.agenda.data.local.dao.PendingItemSyncDao
import com.aarevalo.tasky.agenda.data.local.entity.PendingItemSyncEntity
import com.aarevalo.tasky.agenda.data.local.entity.SyncOperation
import com.aarevalo.tasky.agenda.data.workers.CreateAgendaItemWorker
import com.aarevalo.tasky.agenda.data.workers.DeleteAgendaItemWorker
import com.aarevalo.tasky.agenda.data.workers.PeriodicFetchAgendaWorker
import com.aarevalo.tasky.agenda.data.workers.UpdateAgendaItemWorker
import com.aarevalo.tasky.agenda.domain.SyncAgendaScheduler
import com.aarevalo.tasky.agenda.domain.model.AgendaItem
import com.aarevalo.tasky.agenda.domain.util.AgendaItemJsonConverter
import com.aarevalo.tasky.core.domain.preferences.SessionStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.toJavaDuration

class SyncAgendaWorkerScheduler @Inject constructor(
    private val context: Context,
    private val pendingItemSyncDao: PendingItemSyncDao,
    private val sessionStorage: SessionStorage,
    private val applicationScope: CoroutineScope,
    private val agendaItemJsonConverter: AgendaItemJsonConverter
): SyncAgendaScheduler {

    private val workManager = WorkManager.getInstance(context)
    override suspend fun scheduleSyncAgenda(syncType: SyncAgendaScheduler.SyncType) {
        when(syncType) {
            is SyncAgendaScheduler.SyncType.PeriodicFetch -> schedulePeriodicFetch(syncType.interval)
            is SyncAgendaScheduler.SyncType.CreateAgendaItem -> scheduleCreateAgendaItem(syncType.agendaItem)
            is SyncAgendaScheduler.SyncType.UpdateAgendaItem -> scheduleUpdateAgendaItem(syncType.agendaItem, syncType.isGoing, syncType.deletedPhotoKeys)
            is SyncAgendaScheduler.SyncType.DeleteAgendaItem -> scheduleDeleteAgendaItem(syncType.itemId)
        }
    }

    override suspend fun cancelAllSyncs() {
        WorkManager.getInstance(context).cancelAllWork().await()
    }

    private suspend fun scheduleCreateAgendaItem(agendaItem: AgendaItem) {
        val pendingAgendaItem = PendingItemSyncEntity(
            itemId = agendaItem.id,
            userId = agendaItem.hostId,
            isGoing = false,
            deletedPhotoKeys = emptyList(),
            itemType = AgendaItem.getAgendaItemTypeFromItemId(agendaItem.id),
            syncOperation = SyncOperation.CREATE,
            itemJson =  agendaItemJsonConverter.getJsonFromAgendaItem(agendaItem) ?: return
        )

        pendingItemSyncDao.upsertPendingItemSyn(pendingAgendaItem)

        val workRequest = OneTimeWorkRequestBuilder<CreateAgendaItemWorker>()
            .addTag("create_agenda_item_${agendaItem.id}")
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = 2000L,
                timeUnit = TimeUnit.MILLISECONDS
            )
            .setInputData(
                Data.Builder()
                    .putString(CreateAgendaItemWorker.AGENDA_ITEM_ID, agendaItem.id)
                    .build()
            )

        applicationScope.launch {
            workManager.enqueue(workRequest.build()).await()
        }
    }

    private suspend fun scheduleUpdateAgendaItem(agendaItem: AgendaItem, isGoing: Boolean, deletedPhotoKeys: List<String>) {
        val pendingAgendaItem = PendingItemSyncEntity(
            itemId = agendaItem.id,
            userId = agendaItem.hostId,
            isGoing = isGoing,
            deletedPhotoKeys = deletedPhotoKeys,
            itemType = AgendaItem.getAgendaItemTypeFromItemId(agendaItem.id),
            syncOperation = SyncOperation.UPDATE,
            itemJson =  agendaItemJsonConverter.getJsonFromAgendaItem(agendaItem) ?: return
        )

        pendingItemSyncDao.upsertPendingItemSyn(pendingAgendaItem)

        val workRequest = OneTimeWorkRequestBuilder<UpdateAgendaItemWorker>()
            .addTag("update_agenda_item_${agendaItem.id}")
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = 2000L,
                timeUnit = TimeUnit.MILLISECONDS
            )
            .setInputData(
                Data.Builder()
                        .putString(UpdateAgendaItemWorker.AGENDA_ITEM_ID, agendaItem.id)
                    .build()
            )

        applicationScope.launch {
            workManager.enqueue(workRequest.build()).await()
        }
    }

    private suspend fun scheduleDeleteAgendaItem(itemId: String) {
        val pendingAgendaItem = PendingItemSyncEntity(
            itemId = itemId,
            userId = sessionStorage.getSession()?.userId ?: return,
            isGoing = false,
            deletedPhotoKeys = emptyList(),
            itemType = AgendaItem.getAgendaItemTypeFromItemId(itemId),
            syncOperation = SyncOperation.DELETE,
            itemJson = ""
        )

        pendingItemSyncDao.upsertPendingItemSyn(pendingAgendaItem)

        val workRequest = OneTimeWorkRequestBuilder<DeleteAgendaItemWorker>()
            .addTag("delete_agenda_item_${itemId}")
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = 2000L,
                timeUnit = TimeUnit.MILLISECONDS
            )
            .setInputData(
                Data.Builder()
                    .putString(DeleteAgendaItemWorker.AGENDA_ITEM_ID, itemId)
                    .build()
            )

        applicationScope.launch {
            workManager.enqueue(workRequest.build()).await()
        }
    }

    private suspend fun schedulePeriodicFetch(interval: Duration) {
        val isSyncScheduled = withContext(Dispatchers.IO){
            workManager
                .getWorkInfosByTag("sync_work")
                .get()
                .isNotEmpty()
        }

        if(isSyncScheduled){
            return
        }

        val workRequest = PeriodicWorkRequestBuilder<PeriodicFetchAgendaWorker>(
            repeatInterval = interval.toJavaDuration()
        )

            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = 2000L,
                timeUnit = TimeUnit.MILLISECONDS
            )
            .addTag("sync_work")
            .build()

        workManager.enqueue(workRequest).await()
    }
}