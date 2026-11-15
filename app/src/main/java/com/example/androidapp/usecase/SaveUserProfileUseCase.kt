package com.example.androidapp.usecase

import com.example.androidapp.model.UserProfile
import com.example.androidapp.repository.ProfileRepository

class SaveUserProfileUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(profile: UserProfile) {
        repository.saveProfile(profile)
    }
}