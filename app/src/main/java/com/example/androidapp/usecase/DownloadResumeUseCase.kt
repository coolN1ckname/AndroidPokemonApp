package com.example.androidapp.usecase

import com.example.androidapp.repository.ResumeRepository

class DownloadResumeUseCase(
    private val repository: ResumeRepository
) {
    suspend operator fun invoke(url: String) =
        repository.downloadResume(url)
}