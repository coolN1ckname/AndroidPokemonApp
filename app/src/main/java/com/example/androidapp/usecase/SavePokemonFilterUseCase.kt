package com.example.androidapp.usecase

import com.example.androidapp.model.PokemonFilter
import com.example.androidapp.repository.PokemonFilterRepository

class SavePokemonFilterUseCase(
    private val repository: PokemonFilterRepository
) {
    suspend operator fun invoke(filter: PokemonFilter) {
        repository.saveFilter(filter)
    }
}