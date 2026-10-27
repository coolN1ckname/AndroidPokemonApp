package com.example.androidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.androidapp.DI.AppContainer

class FilterViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(FilterViewModel::class.java)) {
            return FilterViewModel(
                getPokemonFilter = AppContainer.getPokemonFilterUseCase,
                savePokemonFilter = AppContainer.savePokemonFilterUseCase,
                resetPokemonFilter = AppContainer.resetPokemonFilterUseCase,
                filterBadgeCache = AppContainer.filterBadgeCache
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}
