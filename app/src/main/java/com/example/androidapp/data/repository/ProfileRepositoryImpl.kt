package com.example.androidapp.data.repository

import com.example.androidapp.data.preferences.ProfileDataStore
import com.example.androidapp.model.UserProfile
import com.example.androidapp.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow

class ProfileRepositoryImpl(
    private val dataStore: ProfileDataStore
) : ProfileRepository {

    override fun getProfile(): Flow<UserProfile> {
        return dataStore.profile
    }

    override suspend fun saveProfile(profile: UserProfile) {
        dataStore.saveProfile(profile)
    }
}