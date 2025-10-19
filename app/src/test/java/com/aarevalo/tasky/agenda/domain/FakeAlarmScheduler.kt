package com.aarevalo.tasky.agenda.domain

import com.aarevalo.tasky.agenda.domain.model.AlarmItem

/**
 * Fake implementation of AlarmScheduler for testing.
 * Tracks scheduled and cancelled alarms without actually scheduling system alarms.
 */
class FakeAlarmScheduler : AlarmScheduler {
    
    private val scheduledAlarms = mutableMapOf<String, AlarmItem>()
    
    // Track method calls
    val scheduleCalls = mutableListOf<AlarmItem>()
    val cancelCalls = mutableListOf<AlarmItem>()
    
    override fun schedule(alarmItem: AlarmItem) {
        scheduleCalls.add(alarmItem)
        scheduledAlarms[alarmItem.id] = alarmItem
    }
    
    override fun cancel(alarmItem: AlarmItem) {
        cancelCalls.add(alarmItem)
        scheduledAlarms.remove(alarmItem.id)
    }
    
    // Helper methods
    fun reset() {
        scheduledAlarms.clear()
        scheduleCalls.clear()
        cancelCalls.clear()
    }
    
    fun isScheduled(alarmId: String): Boolean {
        return scheduledAlarms.containsKey(alarmId)
    }
    
    fun getScheduledAlarms(): List<AlarmItem> {
        return scheduledAlarms.values.toList()
    }
    
    fun getScheduledAlarmCount(): Int {
        return scheduledAlarms.size
    }
}

