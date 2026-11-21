package com.example.androidapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidapp.DI.AppContainer
import com.example.androidapp.viewmodel.FavoritesViewModel
import com.example.androidapp.viewmodel.FavoritesViewModelFactory

@Composable
fun FavoritesScreen(
    onPokemonClick: (Int) -> Unit,
    favoritesViewModel: FavoritesViewModel = viewModel(
        factory = FavoritesViewModelFactory(
            getFavorites = AppContainer.getFavoritePokemonUseCase,
            addFavorite = AppContainer.addFavoritePokemonUseCase,
            removeFavorite = AppContainer.removeFavoritePokemonUseCase
        )
    )
) {
    val favorites by favoritesViewModel.favorites
        .collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Избранные покемоны")

        Spacer(modifier = Modifier.height(16.dp))

        if (favorites.isEmpty()) {
            Text("Пока нет избранных покемонов")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = favorites,
                    key = { it.id }
                ) { pokemon ->
                    PokemonCard(
                        pokemon = pokemon,
                        isFavorite = true,
                        onPokemonClick = onPokemonClick,
                        onFavoriteClick = {
                            favoritesViewModel.removeFavorite(pokemon.id)
                        }
                    )
                }
            }
        }
    }
}