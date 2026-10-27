package com.example.androidapp.usecase

import com.example.androidapp.repository.PokemonFilterRepository

class ResetPokemonFilterUseCase(
    private val repository: PokemonFilterRepository
) {
    suspend operator fun invoke() {
        repository.resetFilter()
    }
}