package com.example.androidapp.usecase

import com.example.androidapp.notifications.LessonAlarmScheduler
import com.example.androidapp.model.UserProfile

class ScheduleLessonNotificationUseCase(
    private val lessonAlarmScheduler: LessonAlarmScheduler
) {
    operator fun invoke(profile: UserProfile) {
        lessonAlarmScheduler.cancel()

        if (profile.lessonTime.isNotBlank()) {
            lessonAlarmScheduler.scheduleToday(
                nickname = profile.nickname,
                lessonTime = profile.lessonTime
            )
        }
    }
}