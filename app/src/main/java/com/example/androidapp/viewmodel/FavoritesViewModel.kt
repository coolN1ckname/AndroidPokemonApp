package com.example.androidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidapp.model.Pokemon
import com.example.androidapp.usecase.AddFavoritePokemonUseCase
import com.example.androidapp.usecase.GetFavoritePokemonUseCase
import com.example.androidapp.usecase.RemoveFavoritePokemonUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val getFavorites: GetFavoritePokemonUseCase,
    private val addFavoriteUseCase: AddFavoritePokemonUseCase,
    private val removeFavoriteUseCase: RemoveFavoritePokemonUseCase
) : ViewModel() {

    private val _favorites =
        MutableStateFlow<List<Pokemon>>(emptyList())

    val favorites: StateFlow<List<Pokemon>> =
        _favorites.asStateFlow()

    init {
        viewModelScope.launch {
            getFavorites().collect { pokemons ->
                _favorites.value = pokemons
            }
        }
    }

    fun toggleFavorite(pokemon: Pokemon) {
        viewModelScope.launch {
            val alreadyFavorite =
                _favorites.value.any { it.id == pokemon.id }

            if (alreadyFavorite) {
                removeFavoriteUseCase(pokemon.id)
            } else {
                addFavoriteUseCase(pokemon)
            }
        }
    }

    fun removeFavorite(pokemonId: Int) {
        viewModelScope.launch {
            removeFavoriteUseCase(pokemonId)
        }
    }
}
