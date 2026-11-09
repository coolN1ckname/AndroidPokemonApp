package com.example.androidapp.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.androidapp.viewmodel.EditProfileViewModel
import com.example.androidapp.viewmodel.EditProfileViewModelFactory
import java.io.File

@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    editProfileViewModel: EditProfileViewModel = viewModel(
        factory = EditProfileViewModelFactory()
    )
) {
    val profile by editProfileViewModel.profile.collectAsStateWithLifecycle()

    val context = LocalContext.current

    var cameraPhotoUri by remember {
        mutableStateOf<Uri?>(null)
    }

    // Выбор фотографии из галереи
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            editProfileViewModel.onPhotoSelected(
                context = context,
                uri = it.toString()
            )
        }
    }

    // Фотографирование с камеры
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraPhotoUri?.let {
                editProfileViewModel.onPhotoCaptured(it.toString())
            }
        }
    }

    // Запуск камеры
    fun openCamera() {
        val photoFile = File(
            context.filesDir,
            "camera_${System.currentTimeMillis()}.jpg"
        )

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )

        cameraPhotoUri = uri
        cameraLauncher.launch(uri)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = "Редактирование профиля",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Аватар
        if (profile.photoUri != null) {
            AsyncImage(
                model = profile.photoUri,
                contentDescription = "Аватар",
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .clickable {
                        galleryLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    }
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Добавить фотографию",
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .clickable {
                        galleryLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Выбор из галереи
        Button(
            onClick = {
                galleryLauncher.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Выбрать из галереи")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Съёмка камерой
        Button(
            onClick = {
                openCamera()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сделать фото")
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = profile.nickname,
            onValueChange = editProfileViewModel::updateNickname,
            label = { Text("Никнейм") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = profile.position,
            onValueChange = editProfileViewModel::updatePosition,
            label = { Text("Должность") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = profile.resumeUrl,
            onValueChange = editProfileViewModel::updateResumeUrl,
            label = { Text("Ссылка на резюме (PDF)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                editProfileViewModel.saveProfile(onSaved = onBack)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Готово")
        }
    }
}