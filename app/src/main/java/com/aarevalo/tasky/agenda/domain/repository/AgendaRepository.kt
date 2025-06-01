package com.aarevalo.tasky.agenda.domain.repository

import com.aarevalo.tasky.core.domain.util.DataError
import com.aarevalo.tasky.core.domain.util.EmptyResult

interface AgendaRepository {
    suspend fun logout(): EmptyResult<DataError.Network>
}