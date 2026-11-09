package com.example.androidapp.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.androidapp.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.profileDataStore by preferencesDataStore(
    name = "user_profile"
)

class ProfileDataStore(
    private val context: Context
) {

    private object PreferencesKeys {
        val NICKNAME = stringPreferencesKey("nickname")
        val PHOTO_URI = stringPreferencesKey("photo_uri")
        val RESUME_URL = stringPreferencesKey("resume_url")
        val POSITION = stringPreferencesKey("position")
    }

    val profile: Flow<UserProfile> =
        context.profileDataStore.data.map { preferences ->
            UserProfile(
                nickname = preferences[PreferencesKeys.NICKNAME] ?: "",
                photoUri = preferences[PreferencesKeys.PHOTO_URI],
                resumeUrl = preferences[PreferencesKeys.RESUME_URL] ?: "",
                position = preferences[PreferencesKeys.POSITION] ?: ""
            )
        }

    suspend fun saveProfile(profile: UserProfile) {
        context.profileDataStore.edit { preferences ->
            preferences[PreferencesKeys.NICKNAME] = profile.nickname
            preferences[PreferencesKeys.RESUME_URL] = profile.resumeUrl
            preferences[PreferencesKeys.POSITION] = profile.position

            if (profile.photoUri == null) {
                preferences.remove(PreferencesKeys.PHOTO_URI)
            } else {
                preferences[PreferencesKeys.PHOTO_URI] = profile.photoUri
            }
        }
    }
}