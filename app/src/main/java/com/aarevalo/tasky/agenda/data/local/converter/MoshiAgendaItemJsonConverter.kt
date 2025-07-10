package com.aarevalo.tasky.agenda.data.local.converter

import com.aarevalo.tasky.agenda.data.local.entity.EventEntity
import com.aarevalo.tasky.agenda.data.local.entity.ReminderEntity
import com.aarevalo.tasky.agenda.data.local.entity.TaskEntity
import com.aarevalo.tasky.agenda.data.local.mappers.toAgendaItem
import com.aarevalo.tasky.agenda.data.local.mappers.toEventEntity
import com.aarevalo.tasky.agenda.data.local.mappers.toReminderEntity
import com.aarevalo.tasky.agenda.data.local.mappers.toTaskEntity
import com.aarevalo.tasky.agenda.domain.model.AgendaItem
import com.aarevalo.tasky.agenda.domain.model.AgendaItemType
import com.aarevalo.tasky.agenda.domain.util.AgendaItemJsonConverter
import com.aarevalo.tasky.agenda.presentation.agenda_detail.AgendaItemDetails
import com.squareup.moshi.Moshi
import javax.inject.Inject

class MoshiAgendaItemJsonConverter @Inject constructor(
    private val moshi: Moshi
): AgendaItemJsonConverter {
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

    override fun getJsonFromAgendaItem(agendaItem: AgendaItem): String? {
        return try {
            when(agendaItem.details){
                is AgendaItemDetails.Event -> {
                    val eventEntity = agendaItem.toEventEntity()
                    moshi.adapter(EventEntity::class.java).toJson(eventEntity)
                }
                is AgendaItemDetails.Reminder -> {
                    val reminderEntity = agendaItem.toReminderEntity()
                    moshi.adapter(ReminderEntity::class.java).toJson(reminderEntity)
                }
                is AgendaItemDetails.Task -> {
                    val taskEntity = agendaItem.toTaskEntity()
                    moshi.adapter(TaskEntity::class.java).toJson(taskEntity)
                }
            }
        } catch(e: Exception){
            println("Error serializing agendaItem: ${e.message}")
            null
        }
    }
}