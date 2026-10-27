package com.example.androidapp.usecase

import com.example.androidapp.repository.FavoritesRepository
import com.example.androidapp.model.Pokemon

class AddFavoritePokemonUseCase(
    private val repository: FavoritesRepository
) {
    suspend operator fun invoke(pokemon: Pokemon) {
        repository.addFavorite(pokemon)
    }
}