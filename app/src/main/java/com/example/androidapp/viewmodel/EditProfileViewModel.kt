package com.example.androidapp.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidapp.model.UserProfile
import com.example.androidapp.usecase.GetUserProfileUseCase
import com.example.androidapp.usecase.SaveUserProfileUseCase
import com.example.androidapp.usecase.ScheduleLessonNotificationUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

sealed interface EditProfileEvent {
    data object RequestNotificationPermission : EditProfileEvent
    data object NavigateBack : EditProfileEvent
}

class EditProfileViewModel(
    private val getUserProfile: GetUserProfileUseCase,
    private val saveUserProfile: SaveUserProfileUseCase,
    private val scheduleLessonNotification: ScheduleLessonNotificationUseCase
) : ViewModel() {

    private val _profile = MutableStateFlow(UserProfile())
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    private val eventChannel = Channel<EditProfileEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            getUserProfile().collect { savedProfile ->
                _profile.value = savedProfile
            }
        }
    }

    fun updateNickname(value: String) {
        _profile.value = _profile.value.copy(nickname = value)
    }

    fun updatePosition(value: String) {
        _profile.value = _profile.value.copy(position = value)
    }

    fun updateResumeUrl(value: String) {
        _profile.value = _profile.value.copy(resumeUrl = value)
    }

    fun updateLessonTime(value: String) {
        _profile.value = _profile.value.copy(lessonTime = value)
    }

    fun updatePhotoUri(value: String?) {
        _profile.value = _profile.value.copy(photoUri = value)
    }

    fun isLessonTimeValid(): Boolean {
        return Regex("^(?:[01]\\d|2[0-3]):[0-5]\\d$")
            .matches(_profile.value.lessonTime)
    }

    fun onSaveClicked() {
        if (!isLessonTimeValid()) return

        viewModelScope.launch {
            eventChannel.send(
                EditProfileEvent.RequestNotificationPermission
            )
        }
    }

    fun onNotificationPermissionResult(granted: Boolean) {
        viewModelScope.launch {
            val currentProfile = _profile.value

            saveUserProfile(currentProfile)

            // Планирование напоминания выполняется через UseCase.
            scheduleLessonNotification(currentProfile)

            // Экран получает событие навигации.
            eventChannel.send(EditProfileEvent.NavigateBack)
        }
    }

    fun onPhotoSelected(uri: String) {
        updatePhotoUri(uri)
    }

    fun onPhotoCaptured(uri: String) {
        updatePhotoUri(uri)
    }

    fun onPhotoSelected(context: Context, uri: String) {
        viewModelScope.launch {
            val savedPhotoUri = withContext(Dispatchers.IO) {
                val inputStream = context.contentResolver
                    .openInputStream(Uri.parse(uri))

                val photoFile = File(
                    context.filesDir,
                    "avatar_${System.currentTimeMillis()}.jpg"
                )

                inputStream?.use { input ->
                    photoFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                if (photoFile.exists() && photoFile.length() > 0) {
                    Uri.fromFile(photoFile).toString()
                } else {
                    null
                }
            }

            if (savedPhotoUri != null) {
                updatePhotoUri(savedPhotoUri)
            }
        }
    }
}