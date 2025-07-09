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
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    override fun getAgendaItems(): Flow<List<AgendaItem>> {
        return localAgendaSource.getAgendaItems()
    }

    override fun getAgendaItemsByDate(date: LocalDate): Flow<List<AgendaItem>> {
        return localAgendaSource.getAgendaItemsByDate(date)
    }

    override suspend fun fetchAgendaItems(): EmptyResult<DataError> {
        return when(val result = remoteAgendaSource.fetchFullAgenda()) {
            is Result.Error -> {
                println("Error: fetching agenda items remotely! $result")
                result.asEmptyDataResult()
            }
            is Result.Success -> {
                applicationScope.async {
                    localAgendaSource.deleteAllAgendaItems()
                    localAgendaSource.upsertAgendaItems(result.data).asEmptyDataResult()
                }.await()
            }
        }
    }

    override suspend fun getAgendaItemById(agendaItemId: String): AgendaItem? {
        return localAgendaSource.getAgendaItemById(agendaItemId)
    }

    override suspend fun createAgendaItem(agendaItem: AgendaItem): EmptyResult<DataError> {
        println("Creating agenda item locally!")
        val localResult = localAgendaSource.upsertAgendaItem(agendaItem)
        if(localResult is Result.Error){
            return localResult.asEmptyDataResult()
        }
        println("Creating agenda item remotely!")
        return when(val remoteResult = remoteAgendaSource.createAgendaItem(agendaItem)){
            is Result.Error -> {
                println("Error: creating agenda item remotely!")
                applicationScope.launch {
                    syncAgendaScheduler.scheduleSyncAgenda(
                        syncType = SyncAgendaScheduler.SyncType.CreateAgendaItem(agendaItem)
                    )
                }.join()
                Result.Success(Unit)
            }
            is Result.Success -> {
                println("Success: creating agenda item remotely!")
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
        val localResult = localAgendaSource.upsertAgendaItem(agendaItem)
        if(localResult is Result.Error){
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
                    itemJson =  agendaItemJsonConverter.getJsonFromAgendaItem(agendaItem) ?: return Result.Error(DataError.Local.BAD_DATA)
                )
            )
        }

        return when(val remoteResult = remoteAgendaSource.updateAgendaItem(agendaItem, deletedPhotoKeys, isGoing)){
            is Result.Error -> {
                println("Error: updating agenda item remotely!")
                println("Error: $remoteResult")
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
                println("Success: creating agenda item remotely!")
                // edge case when the worker is created before the remote update. but the remote update success.
                // No need for the worker anymore.
                if(existingPendingItem != null){
                    pendingItemSyncDao.deletePendingItemSyncById(agendaItem.id)
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
        try{
            localAgendaSource.deleteAgendaItem(agendaItemId)
        }
        catch (e: SQLException){
            return Result.Error(DataError.Local.DB_ERROR)
        }

        // Edge case where the agenda item is created in offline-mode.
        // And deleted in offline-mode as well.
        val isPendingSync = pendingItemSyncDao.getPendingItemSyncById(agendaItemId) != null
        if(isPendingSync){
           pendingItemSyncDao.deletePendingItemSyncById(agendaItemId)
           return Result.Success(Unit)
        }

        val remoteResult = applicationScope.async {
            remoteAgendaSource.deleteAgendaItem(agendaItemId)
        }.await()

        return when(remoteResult){
            is Result.Error -> {
                applicationScope.launch {
                    syncAgendaScheduler.scheduleSyncAgenda(
                        syncType = SyncAgendaScheduler.SyncType.DeleteAgendaItem(agendaItemId)
                    )
                }
                Result.Success(Unit)
            }
            is Result.Success -> remoteResult.asEmptyDataResult()
        }
    }

    override suspend fun syncPendingAgendaItems() {
        println("Syncing pending agenda items")
        withContext(dispatcher.io){
            val userId = sessionStorage.getSession()?.userId ?: return@withContext

            val pendingItems = async{
                pendingItemSyncDao.getPendingItemSyncByUserId(userId)
            }

            println("Pending items: ${pendingItems.await()}")

            val createJobs = pendingItems
                .await()
                .map {
                    launch {
                        println("Syncing pending item: $it")
                        val result = when(it.syncOperation){
                            SyncOperation.CREATE -> {
                                val agendaItem = agendaItemJsonConverter.getAgendaItemFromJson(it.itemJson, it.itemType) ?: return@launch
                                createAgendaItem(agendaItem)
                            }
                            SyncOperation.UPDATE -> {
                                val agendaItem = agendaItemJsonConverter.getAgendaItemFromJson(it.itemJson, it.itemType) ?: return@launch
                                updateAgendaItem(agendaItem, it.isGoing, it.deletedPhotoKeys)
                            }
                            SyncOperation.DELETE -> {
                                deleteAgendaItem(it.itemId)
                                Result.Success(Unit)
                            }
                        }
                        when(result){
                            is Result.Error -> Unit
                            is Result.Success -> {
                                applicationScope.launch {
                                    pendingItemSyncDao.deletePendingItemSyncById(it.itemId)
                                }.join()
                            }
                        }
                    }
                }

            createJobs.joinAll()
        }

    }


    override suspend fun getAttendee(email: String): Result<Attendee?, DataError.Network> {
        return remoteAgendaSource.fetchAttendee(email)
    }

    override suspend fun logout(): EmptyResult<DataError.Network> {
        syncAgendaScheduler.cancelAllSyncs()
        localAgendaSource.deleteAllAgendaItems()
        sessionStorage.setSession(null)
        return remoteAgendaSource.logout()
    }
}