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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.androidapp.model.PokemonDetails
import com.example.androidapp.viewmodel.PokemonDetailsUiState
import com.example.androidapp.viewmodel.PokemonDetailsViewModel
import java.util.Locale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Color

@Composable
fun DetailsScreen(
    pokemonId: Int,
    onBack: () -> Unit
) {
    val viewModel: PokemonDetailsViewModel = viewModel(
        key = "pokemon-details-$pokemonId",
        factory = PokemonDetailsViewModel.factory(pokemonId)
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is PokemonDetailsUiState.Loading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(12.dp))
                Text("Загрузочка")
            }
        }

        is PokemonDetailsUiState.Error -> {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(state.message)

                OutlinedButton(
                    onClick = { viewModel.loadPokemon() }
                ) {
                    Text("Повторить")
                }

                OutlinedButton(onClick = onBack) {
                    Text("Назад")
                }
            }
        }

        is PokemonDetailsUiState.Success -> {
            PokemonDetailsContent(
                pokemon = state.pokemon,
                onBack = onBack
            )
        }
    }
}

@Composable
private fun PokemonDetailsContent(
    pokemon: PokemonDetails,
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            OutlinedButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            AsyncImage(
                model = pokemon.imageUrl,
                contentDescription = pokemon.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "#${pokemon.id} ${
                    pokemon.name.replaceFirstChar {
                        if (it.isLowerCase()) {
                            it.titlecase(Locale.ROOT)
                        } else {
                            it.toString()
                        }
                    }
                }",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            HorizontalDivider(
                thickness = 1.dp,
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Основная информация",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Рост: ${pokemon.height / 10.0} м")
            Text("Вес: ${pokemon.weight / 10.0} кг")
            Text("Типы: ${pokemon.types.joinToString()}")

            HorizontalDivider(
                thickness = 1.dp,
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Способности",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            pokemon.abilities.forEach { ability ->
                Text(
                    text = "• ${
                        ability.replaceFirstChar {
                            if (it.isLowerCase()) {
                                it.titlecase(Locale.ROOT)
                            } else {
                                it.toString()
                            }
                        }
                    }"
                )
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Характеристики",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        items(pokemon.stats) { stat ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stat.name.replace('-', ' '),
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = stat.value.toString(),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}