package com.aarevalo.tasky.agenda.domain.util

import com.aarevalo.tasky.agenda.domain.model.AgendaItem
import com.aarevalo.tasky.agenda.domain.model.AgendaItemType

interface AgendaItemDeserializer {
    fun getAgendaItemFromJson(itemJson: String, type: AgendaItemType): AgendaItem?
}