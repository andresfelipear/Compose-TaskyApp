package com.aarevalo.tasky.agenda.data.remote.util

import com.aarevalo.tasky.agenda.data.local.entity.EventEntity
import com.aarevalo.tasky.agenda.data.local.entity.ReminderEntity
import com.aarevalo.tasky.agenda.data.local.entity.TaskEntity
import com.aarevalo.tasky.agenda.data.local.mappers.toAgendaItem
import com.aarevalo.tasky.agenda.domain.model.AgendaItem
import com.aarevalo.tasky.agenda.domain.model.AgendaItemType
import com.aarevalo.tasky.agenda.domain.util.AgendaItemDeserializer
import com.squareup.moshi.Moshi
import javax.inject.Inject

class MoshiAgendaItemDeserializer @Inject constructor(
    private val moshi: Moshi
): AgendaItemDeserializer {
    override fun getAgendaItemFromJson(itemJson: String, type: AgendaItemType): AgendaItem? {
        val entityToSync: Any

        try {
            entityToSync = when(type){
                AgendaItemType.EVENT -> moshi.adapter(EventEntity::class.java).fromJson(itemJson) ?: return null
                AgendaItemType.TASK -> moshi.adapter(TaskEntity::class.java).fromJson(itemJson) ?: return null
                AgendaItemType.REMINDER -> moshi.adapter(ReminderEntity::class.java).fromJson(itemJson) ?: return null
            }
        } catch (e: Exception) {
            println("Error deserializing itemJson: ${e.message}")
            return null
        }

        return when(entityToSync){
            is EventEntity -> entityToSync.toAgendaItem()
            is TaskEntity -> entityToSync.toAgendaItem()
            is ReminderEntity -> entityToSync.toAgendaItem()
            else -> {
                println("Unknown entity type deserialized: " + entityToSync::class.simpleName)
                null
            }
        }
    }
}