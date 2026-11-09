package com.example.androidapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.androidapp.viewmodel.ProfileViewModel
import com.example.androidapp.viewmodel.ProfileViewModelFactory

@Composable
fun ProfileScreen(
    onEditClick: () -> Unit,
    profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory()
    )
) {
    val profile by profileViewModel.profile.collectAsStateWithLifecycle()
    val isDownloading by profileViewModel.isDownloading.collectAsStateWithLifecycle()
    val resumeFile by profileViewModel.resumeFile.collectAsStateWithLifecycle()
    val downloadError by profileViewModel.downloadError.collectAsStateWithLifecycle()

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Мой профиль",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (profile.photoUri != null) {
            AsyncImage(
                model = profile.photoUri,
                contentDescription = "Аватар",
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Аватар",
                modifier = Modifier.size(140.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = profile.nickname,
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = profile.position,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onEditClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Редактировать профиль")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                profileViewModel.downloadResume(profile.resumeUrl)
            },
            enabled = profile.resumeUrl.isNotBlank() && !isDownloading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isDownloading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Скачать резюме")
            }
        }

        if (resumeFile != null) {
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    profileViewModel.openResume(context)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Открыть резюме")
            }
        }

        if (downloadError != null) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = downloadError!!,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}