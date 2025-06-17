package com.aarevalo.tasky.agenda.data.local

import com.aarevalo.tasky.agenda.data.local.dao.AttendeeDao
import com.aarevalo.tasky.agenda.data.local.dao.EventDao
import com.aarevalo.tasky.agenda.data.local.dao.PhotoDao
import com.aarevalo.tasky.agenda.data.local.dao.ReminderDao
import com.aarevalo.tasky.agenda.data.local.dao.TaskDao
import com.aarevalo.tasky.agenda.domain.LocalAgendaDataSource
import com.aarevalo.tasky.agenda.domain.model.AgendaItem
import com.aarevalo.tasky.core.domain.util.DataError
import com.aarevalo.tasky.core.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class RoomLocalAgendaDataSource @Inject constructor(
    private val eventDao: EventDao,
    private val taskDao: TaskDao,
    private val reminderDao: ReminderDao,
    private val attendeeDao: AttendeeDao,
    private val photoDao: PhotoDao
): LocalAgendaDataSource{
    override fun getAgendaItems(): Flow<List<AgendaItem>> {
        TODO("Not yet implemented")
    }

    override fun getAgendaItemsByDate(date: LocalDate): Flow<List<AgendaItem>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAgendaItemById(agendaItemId: String): AgendaItem? {
        TODO("Not yet implemented")
    }

    override suspend fun upsertAgendaItem(agendaItem: AgendaItem): EmptyResult<DataError.Local> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAgendaItem(agendaItemId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllAgendaItems() {
        TODO("Not yet implemented")
    }

}