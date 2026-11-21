package com.example.androidapp.model

data class UserProfile(
    val nickname: String = "",
    val photoUri: String? = null,
    val resumeUrl: String = "",
    val position: String = "",
    val lessonTime: String = ""
)