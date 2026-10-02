package com.example.androidapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.androidapp.model.PokemonDetails
import com.example.androidapp.repository.PokemonRepository

class DetailsScreen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PokemonDetailScreen(
        pokemonId: Int,
        onBackClick: () -> Unit
    ) {
        var pokemon by remember {
            mutableStateOf<PokemonDetails?>(null)
        }

        var isLoading by remember {
            mutableStateOf(true)
        }

        var errorMessage by remember {
            mutableStateOf<String?>(null)
        }

        LaunchedEffect(pokemonId) {
            isLoading = true
            pokemon = null
            errorMessage = null

            try {
                pokemon = PokemonRepository().getPokemonById(pokemonId)
            } catch (e: Exception) {
                errorMessage = "Не удалось загрузить данные"
            } finally {
                isLoading = false
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = pokemon?.name?.replaceFirstChar {
                                it.uppercase()
                            } ?: "Pokemon #$pokemonId"
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBackClick
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Назад"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->

            if (isLoading) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }

            } else if (pokemon != null) {

                val currentPokemon = pokemon!!

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                ) {
                    item {

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )

                        AsyncImage(
                            model = currentPokemon.imageUrl,
                            contentDescription = currentPokemon.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                        )

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )

                        Text(
                            text = "#${currentPokemon.id} ${
                                currentPokemon.name.replaceFirstChar {
                                    it.uppercase()
                                }
                            }",
                            style = MaterialTheme.typography.headlineMedium
                        )

                        HorizontalDivider(
                            thickness = 1.dp,
                            color = Color.LightGray
                        )

                        Spacer(
                            modifier = Modifier.height(20.dp)
                        )

                        Text(
                            text = "Основная информация",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )


                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Рост")
                            Text("${currentPokemon.height / 10.0} м")
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Вес")
                            Text("${currentPokemon.weight / 10.0} кг")
                        }

                        Spacer(
                            modifier = Modifier.height(20.dp)
                        )

                        HorizontalDivider(
                            thickness = 1.dp,
                            color = Color.LightGray
                        )

                        Text(
                            text = "Типы",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        currentPokemon.types.forEach { type ->
                            Text(
                                text = "• ${type.replaceFirstChar { it.uppercase() }}",
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(20.dp)
                        )

                        HorizontalDivider(
                            thickness = 1.dp,
                            color = Color.LightGray
                        )

                        Text(
                            text = "Способности",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        currentPokemon.abilities.forEach { ability ->
                            Text(
                                text = "• ${ability.replaceFirstChar { it.uppercase() }}",
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(20.dp)
                        )

                        HorizontalDivider(
                            thickness = 1.dp,
                            color = Color.LightGray
                        )

                        Text(
                            text = "Характеристики",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        currentPokemon.stats.forEach { stat ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(stat.name)
                                Text(stat.value.toString())
                            }
                        }

                        Spacer(
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }
            }
            else if (errorMessage != null) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = errorMessage!!,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

            }
        }
    }
}