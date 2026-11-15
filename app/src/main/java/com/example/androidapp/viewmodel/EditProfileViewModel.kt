package com.example.androidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidapp.model.UserProfile
import com.example.androidapp.usecase.GetUserProfileUseCase
import com.example.androidapp.usecase.SaveUserProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class EditProfileViewModel(
    private val getUserProfile: GetUserProfileUseCase,
    private val saveUserProfile: SaveUserProfileUseCase
) : ViewModel() {

    private val _profile = MutableStateFlow(UserProfile())
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

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

    fun updatePhotoUri(value: String?) {
        _profile.value = _profile.value.copy(photoUri = value)
    }

    fun saveProfile(onSaved: () -> Unit) {
        viewModelScope.launch {
            saveUserProfile(_profile.value)
            onSaved()
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