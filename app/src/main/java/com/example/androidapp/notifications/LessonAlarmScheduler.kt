package com.example.androidapp.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

class LessonAlarmScheduler(
    private val context: Context
) {

    companion object {
        private const val REQUEST_CODE = 1001
    }

    fun scheduleToday(
        nickname: String,
        lessonTime: String
    ): Boolean {
        val parts = lessonTime.split(":")
        if (parts.size != 2) return false

        val hour = parts[0].toIntOrNull() ?: return false
        val minute = parts[1].toIntOrNull() ?: return false

        if (hour !in 0..23 || minute !in 0..59) {
            return false
        }

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Не планируем уведомление на прошедшее время.
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            return false
        }

        val alarmManager = context.getSystemService(
            Context.ALARM_SERVICE
        ) as AlarmManager

        val pendingIntent = createPendingIntent(nickname)

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            !alarmManager.canScheduleExactAlarms()
        ) {
            // Если нет разрешения на точный будильник,
            // используем приблизительное время.
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }

        return true
    }

    fun cancel() {
        val intent = Intent(
            context,
            LessonAlarmReceiver::class.java
        )

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(
            Context.ALARM_SERVICE
        ) as AlarmManager

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    private fun createPendingIntent(
        nickname: String
    ): PendingIntent {
        val intent = Intent(
            context,
            LessonAlarmReceiver::class.java
        ).apply {
            putExtra("nickname", nickname)
        }

        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )
    }
}
