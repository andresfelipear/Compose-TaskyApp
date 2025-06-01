package com.aarevalo.tasky.agenda.data.repository

import com.aarevalo.tasky.agenda.data.remote.api.TaskyAgendaApi
import com.aarevalo.tasky.agenda.domain.repository.AgendaRepository
import com.aarevalo.tasky.core.data.networking.responseToResult
import com.aarevalo.tasky.core.domain.preferences.SessionStorage
import com.aarevalo.tasky.core.domain.util.DataError
import com.aarevalo.tasky.core.domain.util.EmptyResult
import com.aarevalo.tasky.core.domain.util.asEmptyDataResult
import javax.inject.Inject

class AgendaRepositoryImpl @Inject constructor(
    private val api: TaskyAgendaApi,
    private val sessionStorage: SessionStorage
): AgendaRepository {
    override suspend fun logout(): EmptyResult<DataError.Network> {
        val response = responseToResult(api.logout())
        sessionStorage.setSession(null)
        return response.asEmptyDataResult()
    }

}