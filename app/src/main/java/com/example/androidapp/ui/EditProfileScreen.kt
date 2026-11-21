package com.example.androidapp.ui

import android.Manifest
import android.net.Uri
import android.os.Build
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.androidapp.viewmodel.EditProfileEvent
import com.example.androidapp.viewmodel.EditProfileViewModel
import com.example.androidapp.viewmodel.EditProfileViewModelFactory
import java.io.File
import java.util.Locale
import kotlinx.coroutines.flow.collect

@OptIn(ExperimentalMaterial3Api::class)
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

    var showTimePicker by remember {
        mutableStateOf(false)
    }

    val isLessonTimeValid =
        editProfileViewModel.isLessonTimeValid()

    val initialHour = profile.lessonTime
        .substringBefore(":")
        .toIntOrNull()
        ?.coerceIn(0, 23) ?: 9

    val initialMinute = profile.lessonTime
        .substringAfter(":", "")
        .toIntOrNull()
        ?.coerceIn(0, 59) ?: 0

    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    // Системный запрос разрешения на уведомления.
    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->
            editProfileViewModel.onNotificationPermissionResult(granted)
        }

    // Обработка одноразовых событий ViewModel.
    LaunchedEffect(editProfileViewModel) {
        editProfileViewModel.events.collect { event ->
            when (event) {
                EditProfileEvent.RequestNotificationPermission -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationPermissionLauncher.launch(
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                    } else {
                        editProfileViewModel
                            .onNotificationPermissionResult(true)
                    }
                }

                EditProfileEvent.NavigateBack -> {
                    onBack()
                }
            }
        }
    }

    // Выбор фотографии из галереи.
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

    // Фотографирование с камеры.
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraPhotoUri?.let {
                editProfileViewModel.onPhotoCaptured(it.toString())
            }
        }
    }

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

        // Аватар.
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

        Button(
            onClick = {
                openCamera()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сделать фото")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Никнейм.
        OutlinedTextField(
            value = profile.nickname,
            onValueChange = editProfileViewModel::updateNickname,
            label = { Text("Никнейм") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Должность.
        OutlinedTextField(
            value = profile.position,
            onValueChange = editProfileViewModel::updatePosition,
            label = { Text("Должность") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Ссылка на резюме.
        OutlinedTextField(
            value = profile.resumeUrl,
            onValueChange = editProfileViewModel::updateResumeUrl,
            label = { Text("Ссылка на резюме (PDF)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Время любимой пары.
        OutlinedTextField(
            value = profile.lessonTime,
            onValueChange = { value ->
                if (
                    value.length <= 5 &&
                    value.all { it.isDigit() || it == ':' }
                ) {
                    editProfileViewModel.updateLessonTime(value)
                }
            },
            label = { Text("Время любимой пары") },
            placeholder = { Text("HH:mm") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = !isLessonTimeValid,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            trailingIcon = {
                IconButton(
                    onClick = {
                        showTimePicker = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Выбрать время"
                    )
                }
            },
            supportingText = {
                if (!isLessonTimeValid) {
                    Text("Введите корректное время, например 09:30")
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ViewModel сама запускает процесс сохранения.
        Button(
            onClick = {
                editProfileViewModel.onSaveClicked()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isLessonTimeValid
        ) {
            Text("Готово")
        }
    }

    // Диалог выбора времени.
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = {
                showTimePicker = false
            },
            title = {
                Text("Выберите время пары")
            },
            text = {
                TimePicker(state = timePickerState)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedTime = String.format(
                            Locale.getDefault(),
                            "%02d:%02d",
                            timePickerState.hour,
                            timePickerState.minute
                        )

                        editProfileViewModel.updateLessonTime(
                            selectedTime
                        )

                        showTimePicker = false
                    }
                ) {
                    Text("Готово")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showTimePicker = false
                    }
                ) {
                    Text("Отмена")
                }
            }
        )
    }
}