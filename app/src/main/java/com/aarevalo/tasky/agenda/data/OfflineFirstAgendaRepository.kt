package com.aarevalo.tasky.agenda.data

import android.database.SQLException
import com.aarevalo.tasky.agenda.data.local.dao.PendingItemSyncDao
import com.aarevalo.tasky.agenda.data.local.entity.PendingItemSyncEntity
import com.aarevalo.tasky.agenda.data.local.entity.SyncOperation
import com.aarevalo.tasky.agenda.data.local.mappers.toAlarmItem
import com.aarevalo.tasky.agenda.domain.AgendaRepository
import com.aarevalo.tasky.agenda.domain.AlarmScheduler
import com.aarevalo.tasky.agenda.domain.LocalAgendaDataSource
import com.aarevalo.tasky.agenda.domain.RemoteAgendaDataSource
import com.aarevalo.tasky.agenda.domain.SyncAgendaScheduler
import com.aarevalo.tasky.agenda.domain.model.AgendaItem
import com.aarevalo.tasky.agenda.domain.model.AgendaItemType
import com.aarevalo.tasky.agenda.domain.model.Attendee
import com.aarevalo.tasky.agenda.domain.util.AgendaItemJsonConverter
import com.aarevalo.tasky.agenda.presentation.agenda_detail.AgendaItemDetails
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
import java.time.ZonedDateTime
import javax.inject.Inject

class OfflineFirstAgendaRepository @Inject constructor(
    private val remoteAgendaSource: RemoteAgendaDataSource,
    private val localAgendaSource: LocalAgendaDataSource,
    private val sessionStorage: SessionStorage,
    private val applicationScope: CoroutineScope,
    private val pendingItemSyncDao: PendingItemSyncDao,
    private val syncAgendaScheduler: SyncAgendaScheduler,
    private val agendaItemJsonConverter: AgendaItemJsonConverter,
    private val dispatcher: DispatcherProvider,
    private val alarmScheduler: AlarmScheduler
): AgendaRepository {

    override suspend fun scheduleReminder(agendaItem: AgendaItem){
        val currentUserId = sessionStorage.getSession()?.userId

        if(currentUserId == null){
            Timber.w("Cannot schedule reminder for item %s: User not logged in.", agendaItem.id)
            alarmScheduler.cancel(agendaItem.toAlarmItem())
            return
        }

        val reminderTimeMillis = agendaItem.remindAt.toInstant().toEpochMilli()
        val currentTimeMillis = ZonedDateTime.now().toInstant().toEpochMilli()

        val isTimeInFuture = reminderTimeMillis > currentTimeMillis

        // only schedule if the reminder time is in the future.
        println("is in the future (calculated): ${agendaItem.remindAt.toInstant().toEpochMilli() > ZonedDateTime.now().toInstant().toEpochMilli()}")

        if (isTimeInFuture) {
            alarmScheduler.schedule(agendaItem.toAlarmItem())
            Timber.d("Reminder scheduled for item %s (type: %s) at %s", agendaItem.id, agendaItem.javaClass.simpleName, agendaItem.remindAt)
        } else {
            Timber.w("Skipping scheduling reminder for item %s (type: %s) as time %s is in the past or not for current user.",
                     agendaItem.id, agendaItem.javaClass.simpleName, agendaItem.remindAt)
            alarmScheduler.cancel(agendaItem.toAlarmItem())
        }
    }

    override suspend fun cancelReminder(agendaItemId: String, itemType: AgendaItemType) {
        localAgendaSource.getAgendaItemById(agendaItemId, itemType)?.let { agendaItem ->
            alarmScheduler.cancel(agendaItem.toAlarmItem())
            Timber.d("Cancelled reminder for AgendaItem ID: %s", agendaItem.id)
        } ?: Timber.w("Attempted to cancel reminder for non-existent AgendaItem ID: %s", agendaItemId)
    }

    override fun getAllAgendaItems(): Flow<List<AgendaItem>> {
        return localAgendaSource.getAgendaItems()
    }

    override fun getAgendaItemsByDate(date: LocalDate): Flow<List<AgendaItem>> {
        return localAgendaSource.getAgendaItemsByDate(date)
    }

    override suspend fun fetchAgendaItems(): EmptyResult<DataError> {
        val currentUserId = sessionStorage.getSession()?.userId
        if (currentUserId == null) {
            Timber.e("Cannot fetch agenda items: User not logged in.")
            return Result.Error(DataError.Network.UNAUTHORIZED).asEmptyDataResult()
        }

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
                    itemsToDeleteLocally.forEach{ id ->
                        try {
                            // Find the local item to get its type
                            val localItem = localItems.find { it.id == id }
                            if(localItem != null) {
                                cancelReminder(id, localItem.type)
                                localAgendaSource.deleteAgendaItem(id, localItem.type)
                                Timber.d("Deleted local agenda item %s during reconciliation.", id)
                            } else {
                                Timber.w("Could not find local item with ID: %s for deletion", id)
                            }
                        } catch(e: SQLException){
                            Timber.e(e, "Failed to delete agenda item locally for ID: %s", id)
                            return@forEach
                        }
                    }

                    localAgendaSource.upsertAgendaItems(remoteItems)
                    Timber.d("Upserted %d remote agenda items locally.", remoteItems.size)

                    remoteItems.forEach { agendaItem ->
                        scheduleReminder(agendaItem)
                    }

                    Result.Success(Unit)
                }.await()
            }
        }
    }

    override suspend fun getAgendaItemById(agendaItemId: String): AgendaItem? {
        // Try to find the item in all tables since we don't know the type
        return localAgendaSource.getAgendaItemById(agendaItemId, AgendaItemType.EVENT)
            ?: localAgendaSource.getAgendaItemById(agendaItemId, AgendaItemType.TASK)
            ?: localAgendaSource.getAgendaItemById(agendaItemId, AgendaItemType.REMINDER)
    }

    override suspend fun createAgendaItem(agendaItem: AgendaItem): EmptyResult<DataError> {
        Timber.d("Creating agenda item locally: %s", agendaItem.id)
        val localResult = localAgendaSource.upsertAgendaItem(agendaItem)
        if(localResult is Result.Error){
            Timber.e("Failed to create agenda item locally for ID: %s, Error: %s", agendaItem.id, localResult.error)
            return localResult.asEmptyDataResult()
        }

        applicationScope.launch {
            scheduleReminder(agendaItem)
        }

        Timber.d("Attempting to create agenda item remotely: %s", agendaItem.id)
        return when(val remoteResult = remoteAgendaSource.createAgendaItem(agendaItem)){
            is Result.Error -> {
                Timber.e("Error creating agenda item remotely for ID: %s! Error: %s", agendaItem.id, remoteResult)
                println("remoteResult: $remoteResult")
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
                        scheduleReminder(remoteResult.data)
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

        applicationScope.launch {
            scheduleReminder(agendaItem)
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
                    itemType = agendaItem.type,
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
                        scheduleReminder(remoteResult.data)
                        localAgendaSource.upsertAgendaItem(remoteResult.data)
                    }.await().asEmptyDataResult()
                }
                else{
                    remoteResult.asEmptyDataResult()
                }
            }
        }
    }

    override suspend fun deleteAgendaItem(agendaItemId: String, itemType: AgendaItemType): EmptyResult<DataError> {
        Timber.d("Deleting agenda item locally: %s", agendaItemId)
        
        try{
            cancelReminder(agendaItemId, itemType)
            localAgendaSource.deleteAgendaItem(agendaItemId, itemType)
            Timber.d("Agenda item ID: %s deleted locally.", agendaItemId)
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
            remoteAgendaSource.deleteAgendaItem(agendaItemId, itemType)
        }.await()

        return when(remoteResult){
            is Result.Error -> {
                Timber.e("Error deleting agenda item remotely for ID: %s! Error: %s", agendaItemId, remoteResult)
                applicationScope.launch {
                    syncAgendaScheduler.scheduleSyncAgenda(
                        syncType = SyncAgendaScheduler.SyncType.DeleteAgendaItem(agendaItemId, itemType)
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
                                    Result.Error(DataError.Local.BAD_DATA)
                                } else {
                                    remoteAgendaSource.createAgendaItem(agendaItem)
                                }
                            }
                            SyncOperation.UPDATE -> {
                                val agendaItem = agendaItemJsonConverter.getAgendaItemFromJson(pendingItem.itemJson, pendingItem.itemType)
                                if (agendaItem == null) {
                                    Timber.e("Failed to deserialize pending UPDATE item: %s", pendingItem.itemId)
                                    Result.Error(DataError.Local.BAD_DATA)
                                } else {
                                    remoteAgendaSource.updateAgendaItem(
                                        agendaItem = agendaItem,
                                        isGoing = pendingItem.isGoing,
                                        deletedPhotoKeys = pendingItem.deletedPhotoKeys
                                    )
                                }
                            }
                            SyncOperation.DELETE -> {
                                remoteAgendaSource.deleteAgendaItem(pendingItem.itemId, pendingItem.itemType)
                            }
                        }
                        when(result){
                            is Result.Error -> {
                                Timber.w("Failed to sync pending item %s (%s). Error: %s",
                                         pendingItem.itemId, pendingItem.syncOperation, result.error)
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
        Timber.d("User logging out. Cancelling all syncs and alarms.")
        syncAgendaScheduler.cancelAllSyncs()

        val session = sessionStorage.getSession()
        val currentUserId = session?.userId
        val refreshToken = session?.refreshToken.orEmpty()

        // only schedule if the current user is login

        if (currentUserId != null) {
            localAgendaSource.getAgendaItems().first().forEach { agendaItem ->
                alarmScheduler.cancel(agendaItem.toAlarmItem())
            }
            Timber.d("Cancelled all alarms for user ID: %s.", currentUserId)
        } else {
            Timber.w("No user ID found during logout to explicitly cancel alarms.")
        }

        localAgendaSource.deleteAllAgendaItems()
        val logoutResult = remoteAgendaSource.logout(refreshToken)

        if(logoutResult is Result.Success){
            sessionStorage.setSession(null)
            Timber.d("User logged out. Local data cleared.")

        }
        return logoutResult
    }
}