package com.example.androidapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidapp.viewmodel.FilterViewModel
import com.example.androidapp.viewmodel.FilterViewModelFactory
import com.example.androidapp.ui.FilterScreen

@Composable
fun FilterScreen(
    onDone: () -> Unit,
    onBack: () -> Unit,
    viewModel: FilterViewModel = viewModel(
        factory = FilterViewModelFactory()
    )
) {
    val filter by viewModel.filter.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val error by viewModel.error.collectAsState()

    var name by rememberSaveable { mutableStateOf("") }
    var minId by rememberSaveable { mutableStateOf("1") }
    var maxId by rememberSaveable { mutableStateOf("1025") }

    LaunchedEffect(filter) {
        name = filter.name
        minId = filter.minId.toString()
        maxId = filter.maxId.toString()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Фильтры покемонов",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                viewModel.updateName(it)
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Покемон") },
            placeholder = { Text("Например, pikachu") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = minId,
            onValueChange = { value ->
                if (value.isEmpty() || value.all(Char::isDigit)) {
                    minId = value
                    if (value.isNotEmpty()) {
                        viewModel.updateMinId(value)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Минимальный ID") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = maxId,
            onValueChange = { value ->
                if (value.isEmpty() || value.all(Char::isDigit)) {
                    maxId = value
                    if (value.isNotEmpty()) {
                        viewModel.updateMaxId(value)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Максимальный ID") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (error != null) {
            Text(
                text = error.orEmpty(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = viewModel::resetFilter,
                enabled = !isSaving,
                modifier = Modifier.weight(1f)
            ) {
                Text("Сбросить")
            }

            Button(
                onClick = {
                    val min = minId.toIntOrNull()
                    val max = maxId.toIntOrNull()

                    if (min == null || max == null) {
                        return@Button
                    }

                    viewModel.updateMinId(minId)
                    viewModel.updateMaxId(maxId)
                    viewModel.saveFilter(onDone)
                },
                enabled = !isSaving,
                modifier = Modifier.weight(1f)
            ) {
                if (isSaving) {
                    CircularProgressIndicator()
                } else {
                    Text("Готово")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Назад")
        }
    }
}