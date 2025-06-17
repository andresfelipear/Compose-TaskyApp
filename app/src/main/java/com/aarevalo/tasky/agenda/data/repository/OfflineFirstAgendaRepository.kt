package com.aarevalo.tasky.agenda.data.repository

import com.aarevalo.tasky.agenda.data.remote.api.TaskyAgendaApi
import com.aarevalo.tasky.agenda.domain.AgendaRepository
import com.aarevalo.tasky.agenda.domain.RemoteAgendaDataSource
import com.aarevalo.tasky.agenda.domain.model.AgendaItem
import com.aarevalo.tasky.agenda.domain.model.Attendee
import com.aarevalo.tasky.core.data.networking.responseToResult
import com.aarevalo.tasky.core.domain.preferences.SessionStorage
import com.aarevalo.tasky.core.domain.util.DataError
import com.aarevalo.tasky.core.domain.util.EmptyResult
import com.aarevalo.tasky.core.domain.util.Result
import com.aarevalo.tasky.core.domain.util.asEmptyDataResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OfflineFirstAgendaRepository @Inject constructor(
    private val remoteAgendaSource: RemoteAgendaDataSource,
    private val sessionStorage: SessionStorage
): AgendaRepository {
    override fun getAgendaItems(): Flow<List<AgendaItem>> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchAgendaItems(): EmptyResult<DataError> {
        TODO("Not yet implemented")
    }

    override suspend fun getAgendaItemById(agendaItemId: String): AgendaItem? {
        TODO("Not yet implemented")
    }

    override suspend fun upsertAgendaItem(agendaItem: AgendaItem): EmptyResult<DataError> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAgendaItem(agendaItemId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun syncPendingAgendaItems() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllAgendaItems() {
        TODO("Not yet implemented")
    }

    override suspend fun getAttendee(email: String): Result<Attendee, DataError.Network> {
        TODO("Not yet implemented")
    }

    override suspend fun logout(): EmptyResult<DataError.Network> {
        val response = remoteAgendaSource.logout()
        sessionStorage.setSession(null)
        return response
    }
}