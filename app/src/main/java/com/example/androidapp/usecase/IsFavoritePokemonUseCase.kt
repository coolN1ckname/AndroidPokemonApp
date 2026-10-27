package com.example.androidapp.usecase

import com.example.androidapp.repository.FavoritesRepository

class IsFavoritePokemonUseCase(
    private val repository: FavoritesRepository
) {
    operator fun invoke(pokemonId: Int) =
        repository.isFavorite(pokemonId)
}