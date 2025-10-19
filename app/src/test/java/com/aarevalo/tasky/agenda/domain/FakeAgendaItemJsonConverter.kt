package com.aarevalo.tasky.agenda.domain

import com.aarevalo.tasky.agenda.domain.model.AgendaItem
import com.aarevalo.tasky.agenda.domain.model.AgendaItemType
import com.aarevalo.tasky.agenda.domain.util.AgendaItemJsonConverter

/**
 * Fake implementation of AgendaItemJsonConverter for testing.
 * Uses simple toString/fromString instead of actual JSON serialization.
 */
class FakeAgendaItemJsonConverter : AgendaItemJsonConverter {
    
    private val serializedItems = mutableMapOf<String, AgendaItem>()
    
    override fun getJsonFromAgendaItem(agendaItem: AgendaItem): String? {
        val json = agendaItem.id  // Simple: just use ID as "JSON"
        serializedItems[json] = agendaItem
        return json
    }
    
    override fun getAgendaItemFromJson(itemJson: String, type: AgendaItemType): AgendaItem? {
        return serializedItems[itemJson]
    }
    
    // Helper methods
    fun reset() {
        serializedItems.clear()
    }
}

