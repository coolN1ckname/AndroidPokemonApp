package com.example.androidapp.usecase

import com.example.androidapp.repository.FavoritesRepository

class GetFavoritePokemonUseCase(
    private val repository: FavoritesRepository
) {
    operator fun invoke() = repository.getFavorites()
}