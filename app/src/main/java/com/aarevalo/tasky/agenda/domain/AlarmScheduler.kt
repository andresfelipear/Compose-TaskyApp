package com.aarevalo.tasky.agenda.domain

import com.aarevalo.tasky.agenda.domain.model.AlarmItem

interface AlarmScheduler {
    fun schedule(item: AlarmItem)
    fun cancel(item: AlarmItem)
}