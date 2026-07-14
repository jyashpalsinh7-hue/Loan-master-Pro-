package com.loanmaster.pro.core.managers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

object NotificationHelper {

    fun scheduleReminder(context: Context, title: String, message: String, delayMillis: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
            putExtra("notificationId", System.currentTimeMillis().toInt())
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val triggerAtMillis = System.currentTimeMillis() + delayMillis

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
            Log.d("NotificationHelper", "Scheduled reminder: $title in $delayMillis ms")
        } catch (e: SecurityException) {
            Log.e("NotificationHelper", "Permission denied for exact alarm", e)
            // Fallback to inexact alarm if permission denied
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }
    
    fun sendImmediateNotification(context: Context, title: String, message: String) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
            putExtra("notificationId", System.currentTimeMillis().toInt())
        }
        context.sendBroadcast(intent)
    }
}
