package com.aarevalo.tasky.agenda.data.local

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.aarevalo.tasky.agenda.data.local.alarm.AlarmReceiver
import com.aarevalo.tasky.agenda.domain.AlarmScheduler
import com.aarevalo.tasky.agenda.domain.model.AlarmItem
import timber.log.Timber
import javax.inject.Inject

class AlarmManagerAlarmScheduler @Inject constructor(
    private val context: Context,
): AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    @SuppressLint("MissingPermission")
    override fun schedule(item: AlarmItem) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.TITLE_KEY, item.title)
            putExtra(AlarmReceiver.DESCRIPTION_KEY, item.description)
            putExtra(AlarmReceiver.ITEM_ID_KEY, item.id)
            putExtra(AlarmReceiver.ITEM_TYPE_KEY, item.itemType)
        }

        val requestCode = item.id.hashCode()

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            item.time.toInstant().toEpochMilli(),
            PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
        Timber.d("Alarm scheduled for ID: %s, Title: %s, Time: %s", item.id, item.title, item.time)
    }

    override fun cancel(item: AlarmItem) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val requestCode = item.id.hashCode()

        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
        Timber.d("Alarm cancelled for ID: %s", item.id)
    }
}