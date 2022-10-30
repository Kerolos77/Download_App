package com.udacity

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat


private const val NOTIFICATION_ID = 0

@SuppressLint("UnspecifiedImmutableFlag")
fun NotificationManager.sendNotification(
    messageBody: String,
    applicationContext: Context,
    status: String
) {

    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent.apply {
        putExtra("fileName", messageBody)
        putExtra("status", status)
    }
    val contentPendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
    )

//    val showIntent = Intent(applicationContext, MainActivity::class.java)
//    val showPendingIntent: PendingIntent = PendingIntent.getActivity(
//        applicationContext,
//        REQUEST_CODE,
//        showIntent,
//        FLAGS
//    )

    val builder = NotificationCompat.Builder(
            applicationContext,
            applicationContext.getString(R.string.download_notification_channel_id)
    )
            .setSmallIcon(R.drawable.ic_baseline_check_circle_outline_24)
            .setContentTitle(applicationContext
                    .getString(R.string.notification_title))
            .setContentText(messageBody)
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)
            .addAction(
                R.drawable.ic_baseline_download_done_24,
                applicationContext.getString(R.string.show_details),
                contentPendingIntent
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
    notify(NOTIFICATION_ID, builder.build())
}