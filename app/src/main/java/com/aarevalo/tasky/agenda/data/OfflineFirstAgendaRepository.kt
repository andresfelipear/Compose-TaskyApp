package com.aarevalo.tasky.agenda.data

import android.database.SQLException
import com.aarevalo.tasky.agenda.data.local.dao.PendingItemSyncDao
import com.aarevalo.tasky.agenda.data.local.entity.PendingItemSyncEntity
import com.aarevalo.tasky.agenda.data.local.entity.SyncOperation
import com.aarevalo.tasky.agenda.domain.AgendaRepository
import com.aarevalo.tasky.agenda.domain.LocalAgendaDataSource
import com.aarevalo.tasky.agenda.domain.RemoteAgendaDataSource
import com.aarevalo.tasky.agenda.domain.SyncAgendaScheduler
import com.aarevalo.tasky.agenda.domain.model.AgendaItem
import com.aarevalo.tasky.agenda.domain.model.Attendee
import com.aarevalo.tasky.agenda.domain.util.AgendaItemJsonConverter
import com.aarevalo.tasky.core.domain.preferences.SessionStorage
import com.aarevalo.tasky.core.domain.util.DataError
import com.aarevalo.tasky.core.domain.util.DispatcherProvider
import com.aarevalo.tasky.core.domain.util.EmptyResult
import com.aarevalo.tasky.core.domain.util.Result
import com.aarevalo.tasky.core.domain.util.asEmptyDataResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

class OfflineFirstAgendaRepository @Inject constructor(
    private val remoteAgendaSource: RemoteAgendaDataSource,
    private val localAgendaSource: LocalAgendaDataSource,
    private val sessionStorage: SessionStorage,
    private val applicationScope: CoroutineScope,
    private val pendingItemSyncDao: PendingItemSyncDao,
    private val syncAgendaScheduler: SyncAgendaScheduler,
    private val agendaItemJsonConverter: AgendaItemJsonConverter,
    private val dispatcher: DispatcherProvider
): AgendaRepository {

    override fun getAgendaItemsByDate(date: LocalDate): Flow<List<AgendaItem>> {
        return localAgendaSource.getAgendaItemsByDate(date)
    }

    override suspend fun fetchAgendaItems(): EmptyResult<DataError> {
        return when(val result = remoteAgendaSource.fetchFullAgenda()) {
            is Result.Error -> {
                Timber.e("Error fetching agenda items remotely! %s", result)
                result.asEmptyDataResult()
            }
            is Result.Success -> {
                applicationScope.async {
                    val localItems = localAgendaSource.getAgendaItems().first()
                    val localItemsIds = localItems.map { it.id }

                    val remoteItems = result.data
                    val remoteItemsIds = remoteItems.map { it.id }

                    val itemsToDeleteLocally = localItemsIds.minus(remoteItemsIds.toSet())
                    itemsToDeleteLocally.forEach{
                        try {
                            localAgendaSource.deleteAgendaItem(it)
                        } catch(e: SQLException){
                            Timber.e(e, "Failed to delete agenda item locally for ID: %s", it)
                            return@forEach
                        }
                    }

                    localAgendaSource.upsertAgendaItems(remoteItems).asEmptyDataResult()
                }.await()
            }
        }
    }

    override suspend fun getAgendaItemById(agendaItemId: String): AgendaItem? {
        return localAgendaSource.getAgendaItemById(agendaItemId)
    }

    override suspend fun createAgendaItem(agendaItem: AgendaItem): EmptyResult<DataError> {
        Timber.d("Creating agenda item locally: %s", agendaItem.id)
        val localResult = localAgendaSource.upsertAgendaItem(agendaItem)
        if(localResult is Result.Error){
            Timber.e("Failed to create agenda item locally for ID: %s, Error: %s", agendaItem.id, localResult.error)
            return localResult.asEmptyDataResult()
        }

        Timber.d("Attempting to create agenda item remotely: %s", agendaItem.id)
        return when(val remoteResult = remoteAgendaSource.createAgendaItem(agendaItem)){
            is Result.Error -> {
                Timber.e("Error creating agenda item remotely for ID: %s! Error: %s", agendaItem.id, remoteResult)
                applicationScope.launch {
                    syncAgendaScheduler.scheduleSyncAgenda(
                        syncType = SyncAgendaScheduler.SyncType.CreateAgendaItem(agendaItem)
                    )
                }.join()
                Result.Success(Unit)
            }
            is Result.Success -> {
                Timber.d("Success creating agenda item remotely: %s", agendaItem.id)
                if(remoteResult.data != null){
                    applicationScope.async {
                        localAgendaSource.upsertAgendaItem(remoteResult.data)
                    }.await().asEmptyDataResult()
                }
                else{
                    remoteResult.asEmptyDataResult()
                }
            }
        }
    }

    override suspend fun updateAgendaItem(
        agendaItem: AgendaItem,
        isGoing: Boolean,
        deletedPhotoKeys: List<String>
    ): EmptyResult<DataError> {
        Timber.d("Updating agenda item locally: %s", agendaItem.id)
        val localResult = localAgendaSource.upsertAgendaItem(agendaItem)
        if(localResult is Result.Error){
            Timber.e("Failed to update agenda item locally for ID: %s, Error: %s", agendaItem.id, localResult.error)
            return localResult.asEmptyDataResult()
        }

        // edge case - when agenda item is created / or updated in offline-mode
        // the data is updated but the operation keep the same. Keep using the same worker
        val existingPendingItem = pendingItemSyncDao.getPendingItemSyncById(agendaItem.id)

        existingPendingItem?.let { pendingItem ->
            pendingItemSyncDao.upsertPendingItemSyn(
                PendingItemSyncEntity(
                    itemId = agendaItem.id,
                    userId = agendaItem.hostId,
                    isGoing = isGoing,
                    deletedPhotoKeys = deletedPhotoKeys,
                    itemType = AgendaItem.getAgendaItemTypeFromItemId(agendaItem.id),
                    syncOperation = pendingItem.syncOperation,
                    itemJson = agendaItemJsonConverter.getJsonFromAgendaItem(agendaItem)
                        ?: return Result.Error(DataError.Local.BAD_DATA)
                )
            )
        }

        Timber.d("Attempting to update agenda item remotely: %s", agendaItem.id)
        return when(val remoteResult = remoteAgendaSource.updateAgendaItem(agendaItem, deletedPhotoKeys, isGoing)){
            is Result.Error -> {
                Timber.e("Error updating agenda item remotely for ID: %s! Error: %s", agendaItem.id, remoteResult)
                if(existingPendingItem == null){
                    applicationScope.launch {
                        syncAgendaScheduler.scheduleSyncAgenda(
                            syncType = SyncAgendaScheduler.SyncType.UpdateAgendaItem(
                                agendaItem = agendaItem,
                                isGoing = isGoing,
                                deletedPhotoKeys = deletedPhotoKeys
                            )
                        )
                    }.join()
                }
                Result.Success(Unit)
            }
            is Result.Success -> {
                Timber.d("Success updating agenda item remotely: %s", agendaItem.id)
                // edge case when the worker is created before the remote update. but the remote update success.
                // No need for the worker anymore.
                if(existingPendingItem != null){
                    pendingItemSyncDao.deletePendingItemSyncById(agendaItem.id)
                    Timber.d("Deleted pending sync item for ID: %s after successful remote update.", agendaItem.id)
                }
                if(remoteResult.data != null){
                    applicationScope.async {
                        localAgendaSource.upsertAgendaItem(remoteResult.data)
                    }.await().asEmptyDataResult()
                }
                else{
                    remoteResult.asEmptyDataResult()
                }
            }
        }
    }

    override suspend fun deleteAgendaItem(agendaItemId: String): EmptyResult<DataError> {
        Timber.d("Deleting agenda item locally: %s", agendaItemId)
        try{
            localAgendaSource.deleteAgendaItem(agendaItemId)
        }
        catch (e: SQLException){
            Timber.e(e, "Failed to delete agenda item locally for ID: %s", agendaItemId)
            return Result.Error(DataError.Local.DB_ERROR)
        }

        // Edge case where the agenda item is created in offline-mode.
        // And deleted in offline-mode as well.
        val isPendingSync = pendingItemSyncDao.getPendingItemSyncById(agendaItemId) != null
        if(isPendingSync){
            Timber.d("Agenda item ID: %s was pending, deleting from pending sync table.", agendaItemId)
            pendingItemSyncDao.deletePendingItemSyncById(agendaItemId)
            return Result.Success(Unit)
        }

        Timber.d("Attempting to delete agenda item remotely: %s", agendaItemId)
        val remoteResult = applicationScope.async {
            remoteAgendaSource.deleteAgendaItem(agendaItemId)
        }.await()

        return when(remoteResult){
            is Result.Error -> {
                Timber.e("Error deleting agenda item remotely for ID: %s! Error: %s", agendaItemId, remoteResult)
                applicationScope.launch {
                    syncAgendaScheduler.scheduleSyncAgenda(
                        syncType = SyncAgendaScheduler.SyncType.DeleteAgendaItem(agendaItemId)
                    )
                }
                Result.Success(Unit)
            }
            is Result.Success -> {
                Timber.d("Success deleting agenda item remotely: %s", agendaItemId)
                remoteResult.asEmptyDataResult()
            }
        }
    }

    override suspend fun syncPendingAgendaItems() {
        Timber.d("Syncing pending agenda items (centralized process).")
        withContext(dispatcher.io){
            val userId = sessionStorage.getSession()?.userId ?: run {
                Timber.e("Cannot sync pending items: User session not found.")
                return@withContext
            }

            val pendingItems = async{
                pendingItemSyncDao.getPendingItemSyncByUserId(userId)
            }

            Timber.d("Found pending items: %s", pendingItems.await())

            val syncCrudJobs = pendingItems
                .await()
                .map { pendingItem ->
                    launch {
                        Timber.d("Processing pending item: %s, Operation: %s", pendingItem.itemId, pendingItem.syncOperation)
                        val result = when(pendingItem.syncOperation){
                            SyncOperation.CREATE -> {
                                val agendaItem = agendaItemJsonConverter.getAgendaItemFromJson(pendingItem.itemJson, pendingItem.itemType)
                                if (agendaItem == null) {
                                    Timber.e("Failed to deserialize pending CREATE item: %s", pendingItem.itemId)
                                    Result.Error(DataError.Local.BAD_DATA) // Mark as error so it's not deleted
                                } else {
                                    remoteAgendaSource.createAgendaItem(agendaItem)
                                }
                            }
                            SyncOperation.UPDATE -> {
                                val agendaItem = agendaItemJsonConverter.getAgendaItemFromJson(pendingItem.itemJson, pendingItem.itemType)
                                if (agendaItem == null) {
                                    Timber.e("Failed to deserialize pending UPDATE item: %s", pendingItem.itemId)
                                    Result.Error(DataError.Local.BAD_DATA) // Mark as error so it's not deleted
                                } else {
                                    remoteAgendaSource.updateAgendaItem(
                                        agendaItem = agendaItem,
                                        isGoing = pendingItem.isGoing,
                                        deletedPhotoKeys = pendingItem.deletedPhotoKeys
                                    )
                                }
                            }
                            SyncOperation.DELETE -> {
                                remoteAgendaSource.deleteAgendaItem(pendingItem.itemId)
                            }
                        }
                        when(result){
                            is Result.Error -> {
                                Timber.w("Failed to sync pending item %s (%s). Error: %s",
                                         pendingItem.itemId, pendingItem.syncOperation, result.error)
                                // Keep pending item in DB for next retry
                            }
                            is Result.Success -> {
                                Timber.d("Successfully synced pending item %s (%s). Deleting from pending sync.",
                                         pendingItem.itemId, pendingItem.syncOperation)
                                applicationScope.launch {
                                    pendingItemSyncDao.deletePendingItemSyncById(pendingItem.itemId)
                                }.join()
                            }
                        }
                    }
                }

            syncCrudJobs.joinAll()
        }
    }


    override suspend fun getAttendee(email: String): Result<Attendee?, DataError.Network> {
        return remoteAgendaSource.fetchAttendee(email)
    }

    override suspend fun logout(): EmptyResult<DataError.Network> {
        syncAgendaScheduler.cancelAllSyncs()
        localAgendaSource.deleteAllAgendaItems()
        sessionStorage.setSession(null)
        Timber.d("User logged out. Local data cleared and syncs cancelled.")
        return remoteAgendaSource.logout()
    }
}