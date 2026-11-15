package com.example.androidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidapp.DI.AppContainer.downloadResumeUseCase
import com.example.androidapp.model.UserProfile
import com.example.androidapp.usecase.GetUserProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import com.example.androidapp.usecase.DownloadResumeUseCase
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider

class ProfileViewModel(
    private val getUserProfile: GetUserProfileUseCase,
    private val downloadResumeUseCase: DownloadResumeUseCase
) : ViewModel() {

    private val _profile = MutableStateFlow(UserProfile())
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    init {
        viewModelScope.launch {
            getUserProfile().collect { userProfile ->
                _profile.value = userProfile
            }
        }
    }
    private val _resumeFile = MutableStateFlow<File?>(null)
    val resumeFile: StateFlow<File?> = _resumeFile.asStateFlow()

    private val _isDownloading = MutableStateFlow(false)
    val isDownloading: StateFlow<Boolean> = _isDownloading.asStateFlow()

    private val _downloadError = MutableStateFlow<String?>(null)
    val downloadError: StateFlow<String?> = _downloadError.asStateFlow()

    fun downloadResume(url: String) {
        viewModelScope.launch {
            _isDownloading.value = true
            _downloadError.value = null

            try {
                _resumeFile.value = downloadResumeUseCase(url)
            } catch (e: Exception) {
                _downloadError.value = e.message ?: "Ошибка загрузки резюме"
            } finally {
                _isDownloading.value = false
            }
        }
    }

    fun openResume(context: Context) {
        val file = _resumeFile.value ?: return

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            _downloadError.value = "Не найдено приложение для открытия PDF"
        }
    }
}