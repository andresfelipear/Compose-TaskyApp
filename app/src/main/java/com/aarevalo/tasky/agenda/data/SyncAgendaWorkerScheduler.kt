package com.aarevalo.tasky.agenda.data

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.await
import com.aarevalo.tasky.agenda.data.local.dao.PendingItemSyncDao
import com.aarevalo.tasky.agenda.data.workers.PeriodicFetchAgendaWorker
import com.aarevalo.tasky.agenda.domain.SyncAgendaScheduler
import com.aarevalo.tasky.core.domain.preferences.SessionStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.toJavaDuration

class SyncAgendaWorkerScheduler @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val pendingItemSyncDao: PendingItemSyncDao,
    private val sessionStorage: SessionStorage,
    private val applicationScope: CoroutineScope
): SyncAgendaScheduler {

    private val workManager = WorkManager.getInstance(context)
    override suspend fun scheduleSyncAgenda(syncType: SyncAgendaScheduler.SyncType) {
        when(syncType) {
            is SyncAgendaScheduler.SyncType.PeriodicFetch -> schedulePeriodicFetch(syncType.interval)
            is SyncAgendaScheduler.SyncType.CreateAgendaItem -> scheduleCreateAgendaItem(syncType.itemId)
            is SyncAgendaScheduler.SyncType.UpdateAgendaItem -> scheduleUpdateAgendaItem(syncType.itemId)
            is SyncAgendaScheduler.SyncType.DeleteAgendaItem -> scheduleDeleteAgendaItem(syncType.itemId, syncType.itemType)
        }
    }

    override suspend fun cancelAllSyncs() {
        WorkManager.getInstance(context).cancelAllWork().await()
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