package com.aarevalo.tasky.agenda.data

import com.aarevalo.tasky.agenda.data.local.dao.PendingItemSyncDao
import com.aarevalo.tasky.agenda.domain.AgendaRepository
import com.aarevalo.tasky.agenda.domain.LocalAgendaDataSource
import com.aarevalo.tasky.agenda.domain.RemoteAgendaDataSource
import com.aarevalo.tasky.agenda.domain.model.AgendaItem
import com.aarevalo.tasky.agenda.domain.model.Attendee
import com.aarevalo.tasky.core.domain.preferences.SessionStorage
import com.aarevalo.tasky.core.domain.util.DataError
import com.aarevalo.tasky.core.domain.util.EmptyResult
import com.aarevalo.tasky.core.domain.util.Result
import com.aarevalo.tasky.core.domain.util.asEmptyDataResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class OfflineFirstAgendaRepository @Inject constructor(
    private val remoteAgendaSource: RemoteAgendaDataSource,
    private val localAgendaSource: LocalAgendaDataSource,
    private val sessionStorage: SessionStorage,
    private val applicationScope: CoroutineScope,
    private val pendingItemSyncDao: PendingItemSyncDao
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
        val localResult = localAgendaSource.upsertAgendaItem(agendaItem)
        if(localResult is Result.Error){
            return localResult.asEmptyDataResult()
        }
        return when(val remoteResult = remoteAgendaSource.createAgendaItem(agendaItem)){
            is Result.Error -> {
                localAgendaSource.deleteAgendaItem(agendaItem.id)
                println("Error: creating agenda item remotely!")
                remoteResult.asEmptyDataResult()
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
        return when(val remoteResult = remoteAgendaSource.updateAgendaItem(agendaItem, deletedPhotoKeys, isGoing)){
            is Result.Error -> {
                localAgendaSource.deleteAgendaItem(agendaItem.id)
                println("Error: creating agenda item remotely!")
                remoteResult.asEmptyDataResult()
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

    override suspend fun deleteAgendaItem(agendaItemId: String) {
        localAgendaSource.deleteAgendaItem(agendaItemId)

        // Edge case where the agenda item is created in offline-mode.
        // And deleted in offline-mode as well.
        val isPendingSync = pendingItemSyncDao.getPendingItemSyncById(agendaItemId) != null
        if(isPendingSync){
           pendingItemSyncDao.deletePendingItemSyncById(agendaItemId)
           return
        }

        val remoteResult = applicationScope.async {
            remoteAgendaSource.deleteAgendaItem(agendaItemId)
        }.await()

        if(remoteResult is Result.Error){
            // TODO sync delete run
        }
    }

    override suspend fun syncPendingAgendaItems() {
        TODO("Not yet implemented")
    }


    override suspend fun getAttendee(email: String): Result<Attendee?, DataError.Network> {
        return remoteAgendaSource.fetchAttendee(email)
    }

    override suspend fun logout(): EmptyResult<DataError.Network> {
        val response = remoteAgendaSource.logout()
        sessionStorage.setSession(null)
        localAgendaSource.deleteAllAgendaItems()
        return response
    }
}