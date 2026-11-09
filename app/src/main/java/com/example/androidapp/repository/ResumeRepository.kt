package com.example.androidapp.repository

import java.io.File

interface ResumeRepository {
    suspend fun downloadResume(url: String): File
}