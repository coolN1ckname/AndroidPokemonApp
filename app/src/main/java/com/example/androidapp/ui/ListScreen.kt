package com.example.androidapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.androidapp.DI.AppContainer
import com.example.androidapp.model.Pokemon
import com.example.androidapp.viewmodel.FavoritesViewModel
import com.example.androidapp.viewmodel.FavoritesViewModelFactory
import com.example.androidapp.viewmodel.PokemonListUiState
import com.example.androidapp.viewmodel.PokemonListViewModel

class ListScreen {

    @Composable
    fun MainListScreen(
        onPokemonClick: (Int) -> Unit = {},
        onProfileClick: () -> Unit = {}
    ) {
        val viewModel: PokemonListViewModel = viewModel()

        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        val favoritesViewModel: FavoritesViewModel = viewModel(
            factory = FavoritesViewModelFactory(
                getFavorites = AppContainer.getFavoritePokemonUseCase,
                addFavorite = AppContainer.addFavoritePokemonUseCase,
                removeFavorite = AppContainer.removeFavoritePokemonUseCase
            )
        )

        val favorites by favoritesViewModel.favorites
            .collectAsStateWithLifecycle()

        val favoriteIds = favorites.map { it.id }.toSet()

        var searchText by rememberSaveable {
            mutableStateOf("")
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Поиск покемона") },
                placeholder = { Text("Например, Pikachu") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = uiState) {
                PokemonListUiState.Loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Загрузка Pokémon...")
                    }
                }

                is PokemonListUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(state.message)

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedButton(
                            onClick = { viewModel.retry() }
                        ) {
                            Text("Повторить")
                        }
                    }
                }

                is PokemonListUiState.Success -> {
                    val filteredPokemons = state.pokemons.filter { pokemon ->
                        pokemon.name.contains(
                            searchText,
                            ignoreCase = true
                        )
                    }

                    PokemonList(
                        pokemons = filteredPokemons,
                        favoriteIds = favoriteIds,
                        isLoadingMore = state.isLoadingMore,
                        errorMessage = state.errorMessage,
                        onPokemonClick = onPokemonClick,
                        onFavoriteClick = { pokemon ->
                            favoritesViewModel.toggleFavorite(pokemon)
                        },
                        onLoadMore = {
                            viewModel.loadNextPage()
                        },
                        onRetry = {
                            viewModel.retry()
                        }
                    )
                }
            }
        }
    }

    @Composable
    private fun PokemonList(
        pokemons: List<Pokemon>,
        favoriteIds: Set<Int>,
        isLoadingMore: Boolean,
        errorMessage: String?,
        onPokemonClick: (Int) -> Unit,
        onFavoriteClick: (Pokemon) -> Unit,
        onLoadMore: () -> Unit,
        onRetry: () -> Unit
    ) {
        val listState = rememberLazyListState()

        LaunchedEffect(
            listState.firstVisibleItemIndex,
            pokemons.size
        ) {
            val lastVisibleItem = listState.layoutInfo
                .visibleItemsInfo
                .lastOrNull()
                ?.index

            if (
                lastVisibleItem != null &&
                lastVisibleItem >= pokemons.size - 10
            ) {
                onLoadMore()
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = pokemons,
                key = { pokemon -> pokemon.id }
            ) { pokemon ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "#${pokemon.id} ${
                                pokemon.name.replaceFirstChar {
                                    it.uppercase()
                                }
                            }",
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    onPokemonClick(pokemon.id)
                                },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        IconButton(
                            onClick = {
                                onFavoriteClick(pokemon)
                            }
                        ) {
                            Icon(
                                imageVector = if (
                                    pokemon.id in favoriteIds
                                ) {
                                    Icons.Default.Favorite
                                } else {
                                    Icons.Default.FavoriteBorder
                                },
                                contentDescription = if (
                                    pokemon.id in favoriteIds
                                ) {
                                    "Удалить из избранного"
                                } else {
                                    "Добавить в избранное"
                                },
                                tint = if (
                                    pokemon.id in favoriteIds
                                ) {
                                    Color.Red
                                } else {
                                    Color.Gray
                                }
                            )
                        }
                    }

                    AsyncImage(
                        model = pokemon.imageUrl,
                        contentDescription = pokemon.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clickable {
                                onPokemonClick(pokemon.id)
                            }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    HorizontalDivider(
                        thickness = 1.dp,
                        color = Color.LightGray
                    )
                }
            }

            if (isLoadingMore) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Загружаем ещё...")
                    }
                }
            }

            if (errorMessage != null) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(errorMessage)

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(onClick = onRetry) {
                            Text("Повторить")
                        }
                    }
                }
            }
        }
    }
}
