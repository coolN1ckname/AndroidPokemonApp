package com.example.androidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.androidapp.usecase.AddFavoritePokemonUseCase
import com.example.androidapp.usecase.GetFavoritePokemonUseCase
import com.example.androidapp.usecase.RemoveFavoritePokemonUseCase

class FavoritesViewModelFactory(
    private val getFavorites: GetFavoritePokemonUseCase,
    private val addFavorite: AddFavoritePokemonUseCase,
    private val removeFavorite: RemoveFavoritePokemonUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            return FavoritesViewModel(
                getFavorites = getFavorites,
                addFavoriteUseCase = addFavorite,
                removeFavoriteUseCase = removeFavorite
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}
