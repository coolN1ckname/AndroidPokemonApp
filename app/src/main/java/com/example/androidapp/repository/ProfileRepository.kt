package com.example.androidapp.repository

import com.example.androidapp.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {

    fun getProfile(): Flow<UserProfile>

    suspend fun saveProfile(profile: UserProfile)
}