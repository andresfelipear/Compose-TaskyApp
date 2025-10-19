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
import timber.log.Timber
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
            is SyncAgendaScheduler.SyncType.DeleteAgendaItem -> scheduleDeleteAgendaItem(syncType.itemId, syncType.itemType)
        }
    }

    override suspend fun cancelAllSyncs() {
        Timber.d("Cancelling all WorkManager tasks.")
        WorkManager.getInstance(context).cancelAllWork().await()
    }

    private suspend fun scheduleCreateAgendaItem(agendaItem: AgendaItem) {
        val pendingAgendaItem = PendingItemSyncEntity(
            itemId = agendaItem.id,
            userId = agendaItem.hostId,
            isGoing = false,
            deletedPhotoKeys = emptyList(),
            itemType = agendaItem.type,
            syncOperation = SyncOperation.CREATE,
            itemJson =  agendaItemJsonConverter.getJsonFromAgendaItem(agendaItem) ?: run {
                Timber.e("Failed to get JSON from agenda item for creation: %s", agendaItem.id)
                return
            }
        )

        pendingItemSyncDao.upsertPendingItemSyn(pendingAgendaItem)
        Timber.d("Pending create agenda item saved locally: %s", agendaItem.id)

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
            .build() // Build the request here

        applicationScope.launch {
            workManager.enqueue(workRequest).await() // Enqueue the built request
            Timber.d("WorkRequest enqueued for creating agenda item: %s", agendaItem.id)
        }
    }

    private suspend fun scheduleUpdateAgendaItem(agendaItem: AgendaItem, isGoing: Boolean, deletedPhotoKeys: List<String>) {
        Timber.d("Scheduling update agenda item: %s", agendaItem)
        Timber.d("AgendaItem ID: %s", agendaItem.id)

        val pendingAgendaItem = PendingItemSyncEntity(
            itemId = agendaItem.id,
            userId = agendaItem.hostId,
            isGoing = isGoing,
            deletedPhotoKeys = deletedPhotoKeys,
            itemType = agendaItem.type,
            syncOperation = SyncOperation.UPDATE,
            itemJson =  agendaItemJsonConverter.getJsonFromAgendaItem(agendaItem) ?: run {
                Timber.e("Failed to get json from agenda item for update: %s", agendaItem.id)
                return
            }
        )

        pendingItemSyncDao.upsertPendingItemSyn(pendingAgendaItem)
        Timber.d("Pending update agenda item saved locally: %s", agendaItem.id)

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
            .build() // Build the request here

        applicationScope.launch {
            workManager.enqueue(workRequest).await() // Enqueue the built request
            Timber.d("WorkRequest enqueued for updating agenda item: %s", agendaItem.id)
        }
    }

    private suspend fun scheduleDeleteAgendaItem(itemId: String, itemType: com.aarevalo.tasky.agenda.domain.model.AgendaItemType) {
        val userId = sessionStorage.getSession()?.userId
        if (userId == null) {
            Timber.e("Cannot schedule delete agenda item: User session not found for item ID: %s", itemId)
            return
        }

        val pendingAgendaItem = PendingItemSyncEntity(
            itemId = itemId,
            userId = userId,
            isGoing = false,
            deletedPhotoKeys = emptyList(),
            itemType = itemType,
            syncOperation = SyncOperation.DELETE,
            itemJson = "" // No JSON needed for delete
        )

        pendingItemSyncDao.upsertPendingItemSyn(pendingAgendaItem)
        Timber.d("Pending delete agenda item saved locally: %s", itemId)

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
                    .putString(DeleteAgendaItemWorker.AGENDA_ITEM_TYPE, itemType.name)
                    .build()
            )
            .build() // Build the request here

        applicationScope.launch {
            workManager.enqueue(workRequest).await() // Enqueue the built request
            Timber.d("WorkRequest enqueued for deleting agenda item: %s", itemId)
        }
    }

    private suspend fun schedulePeriodicFetch(interval: Duration) {
        val periodicFetchTag = "sync_work" // Consistent tag for periodic fetch
        val isSyncScheduled = withContext(Dispatchers.IO){
            workManager
                .getWorkInfosByTag(periodicFetchTag)
                .get()
                .any { it.state == androidx.work.WorkInfo.State.ENQUEUED || it.state == androidx.work.WorkInfo.State.RUNNING }
        }

        if(isSyncScheduled){
            Timber.d("Periodic fetch work already scheduled. Skipping new request.")
            return
        }

        Timber.d("Scheduling periodic fetch agenda work with interval: %s", interval)

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
            .addTag(periodicFetchTag)
            .build()

        workManager.enqueue(workRequest).await()
        Timber.d("Periodic fetch work enqueued.")
    }
}