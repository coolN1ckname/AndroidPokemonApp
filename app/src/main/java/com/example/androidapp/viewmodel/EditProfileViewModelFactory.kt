package com.example.androidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.androidapp.DI.AppContainer
import com.example.androidapp.usecase.ScheduleLessonNotificationUseCase

class EditProfileViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditProfileViewModel(
                getUserProfile = AppContainer.getUserProfileUseCase,
                saveUserProfile = AppContainer.saveUserProfileUseCase,
                scheduleLessonNotification = ScheduleLessonNotificationUseCase(
                    AppContainer.lessonAlarmScheduler
                )
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}