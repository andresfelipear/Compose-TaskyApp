package com.aarevalo.tasky.agenda.data.remote

import com.aarevalo.tasky.agenda.data.remote.api.TaskyAgendaApi
import com.aarevalo.tasky.agenda.domain.RemoteAgendaDataSource
import com.aarevalo.tasky.agenda.domain.model.AgendaItem
import com.aarevalo.tasky.agenda.domain.model.Attendee
import com.aarevalo.tasky.core.data.networking.responseToResult
import com.aarevalo.tasky.core.domain.util.DataError
import com.aarevalo.tasky.core.domain.util.EmptyResult
import com.aarevalo.tasky.core.domain.util.Result
import com.aarevalo.tasky.core.domain.util.asEmptyDataResult
import javax.inject.Inject

class RetrofitRemoteAgendaDataSource @Inject constructor(
    private val api: TaskyAgendaApi
): RemoteAgendaDataSource {
    override suspend fun fetchFullAgenda(): Result<List<AgendaItem>, DataError.Network> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchAgendaItems(
        from: Long,
        to: Long
    ): Result<List<AgendaItem>, DataError.Network> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchAgendaItem(agendaItemId: String): Result<AgendaItem?, DataError.Network> {
        TODO("Not yet implemented")
    }

    override suspend fun insertAgendaItem(agendaItem: AgendaItem): Result<AgendaItem, DataError.Network> {
        TODO("Not yet implemented")
    }

    override suspend fun updateAgendaItem(agendaItem: AgendaItem): Result<AgendaItem, DataError.Network> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchAttendee(email: String): Result<Attendee, DataError.Network> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAttendee(eventId: String): EmptyResult<DataError.Network> {
        TODO("Not yet implemented")
    }

    override suspend fun syncAgenda(
        deletedEventIds: List<String>,
        deletedTaskIds: List<String>,
        deletedReminderIds: List<String>
    ): EmptyResult<DataError.Network> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAgendaItem(agendaItemId: String): EmptyResult<DataError.Network> {
        TODO("Not yet implemented")
    }

    override suspend fun logout(): EmptyResult<DataError.Network> {
        val response = responseToResult(api.logout())
        return response.asEmptyDataResult()
    }

}