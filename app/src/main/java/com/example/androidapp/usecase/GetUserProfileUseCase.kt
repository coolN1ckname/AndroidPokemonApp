package com.example.androidapp.usecase

import com.example.androidapp.repository.ProfileRepository

class GetUserProfileUseCase(
    private val repository: ProfileRepository
) {
    operator fun invoke() = repository.getProfile()
}