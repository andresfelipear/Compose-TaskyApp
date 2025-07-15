package com.aarevalo.tasky.agenda.data.local.alarm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.aarevalo.tasky.R
import com.aarevalo.tasky.agenda.data.workers.RescheduleAlarmsWorker
import com.aarevalo.tasky.core.domain.preferences.SessionStorage
import com.aarevalo.tasky.core.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver(){

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var sessionStorage: SessionStorage

    override fun onReceive(context: Context?, intent: Intent?){
        val appContext = context ?: return
        val receivedIntent = intent ?: return

        when (receivedIntent.action) {
            Intent.ACTION_BOOT_COMPLETED, Intent.ACTION_LOCKED_BOOT_COMPLETED -> {
                Timber.d("Device booted. Scheduling RescheduleAlarmsWorker to re-schedule alarms.")
                // Enqueue a WorkManager task to re-schedule all alarms in the background.
                // This worker will fetch all AgendaItems from the local DB and schedule their alarms.
                WorkManager.getInstance(appContext).enqueueUniqueWork(
                    RescheduleAlarmsWorker.TAG,
                    ExistingWorkPolicy.REPLACE,
                    OneTimeWorkRequest.Builder(RescheduleAlarmsWorker::class.java).build()
                )
                return
            }
        }

        val title = receivedIntent.getStringExtra(TITLE_KEY) ?: appContext.getString(R.string.default_reminder_title)
        val description = receivedIntent.getStringExtra(DESCRIPTION_KEY) ?: appContext.getString(R.string.default_reminder_description)
        val itemId = receivedIntent.getStringExtra(ITEM_ID_KEY)
        val itemType = receivedIntent.getStringExtra(ITEM_TYPE_KEY)

        Timber.d("Alarm triggered for item ID: %s, Title: %s, Description: %s", itemId, title, description)

        val currentUserId = runBlocking { sessionStorage.getSession()?.userId }
        if (currentUserId == null) {
            Timber.w("Skipping reminder for item ID: %s. User not logged in",
                     itemId)
            return
        }

        createNotificationChannel(appContext)

        showNotification(appContext, title, description, itemId, itemType)
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.reminder_notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.reminder_notification_channel_description)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
            Timber.d("Notification channel '%s' created.", CHANNEL_ID)
        }
    }

    private fun showNotification(context: Context, title: String, description: String, itemId: String?, itemType: String?) {
        val notificationId = itemId?.hashCode() ?: System.currentTimeMillis().toInt()

        val deepLinkUri = Uri.parse("tasky://agenda_detail/$itemId?isEditable=false&type=$itemType")

        val detailIntent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = deepLinkUri
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingDetailIntent = PendingIntent.getActivity(
            context,
            notificationId,
            detailIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle(title)
            .setContentText(description)
            .setStyle(NotificationCompat.BigTextStyle().bigText(description))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingDetailIntent)
            .build()

        notificationManager.notify(notificationId, notification)
        Timber.d("Notification shown for ID: %d, Title: %s", notificationId, title)
    }

    companion object {
        const val TITLE_KEY = "EXTRA_TITLE"
        const val DESCRIPTION_KEY = "EXTRA_DESCRIPTION"
        const val ITEM_ID_KEY = "EXTRA_ITEM_ID"
        const val ITEM_TYPE_KEY = "EXTRA_ITEM_TYPE"
        const val CHANNEL_ID = "tasky_reminder_channel"
    }
}