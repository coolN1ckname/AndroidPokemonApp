package com.example.androidapp.usecase

import com.example.androidapp.repository.FavoritesRepository

class RemoveFavoritePokemonUseCase(
    private val repository: FavoritesRepository
) {
    suspend operator fun invoke(pokemonId: Int) {
        repository.removeFavorite(pokemonId)
    }
}