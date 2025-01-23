package com.id22.dicodingevent

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.id22.dicodingevent.data.source.remote.network.ApiConfig
import com.id22.dicodingevent.util.Helper.Companion.convertDateString

class ReminderWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        return getEventUpcoming()
    }

    private fun getEventUpcoming(): Result {
        val client = ApiConfig.getApiService().getReminderEvents(-1, 1)

        try {
            val response = client.execute()

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    responseBody.listEvents?.let {
                        if (it.isNotEmpty()) {
                            val data = it.first()

                            val date = convertDateString(data.beginTime, "EEEE, dd MMMM yyyy")
                            val description = "Recommendation event for you on $date."

                            showNotification(data.name, description)
                            return Result.success()
                        }
                    }
                }
            }

            return Result.failure()
        } catch (e: Exception) {
            return Result.failure()
        }
    }

    private fun showNotification(title: String, description: String?) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground).setContentTitle(title)
                .setContentText(description)
                .setStyle(NotificationCompat.BigTextStyle().bigText(description))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            notification.setChannelId(CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "channel_01"
        const val CHANNEL_NAME = "event channel"
    }
}