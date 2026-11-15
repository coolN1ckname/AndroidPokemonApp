package com.example.androidapp.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.androidapp.MainActivity
import com.example.androidapp.R

class LessonAlarmReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "lesson_notifications"
        private const val NOTIFICATION_ID = 1001
    }

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        val nickname = intent.getStringExtra("nickname")
            .orEmpty()
            .ifBlank { "Пользователь" }

        createNotificationChannel(context)

        val openAppIntent = Intent(
            context,
            MainActivity::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val contentPendingIntent = PendingIntent.getActivity(
            context,
            1002,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            context,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Начало любимой пары!")
            .setContentText("$nickname, пора на занятие!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .build()

        try {
            NotificationManagerCompat
                .from(context)
                .notify(NOTIFICATION_ID, notification)
        } catch (_: SecurityException) {
            // Уведомления не покажутся, если пользователь
            // не предоставил приложению разрешение
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Уведомления о любимой паре",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Напоминание о начале занятия"
            }

            val manager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }
}
