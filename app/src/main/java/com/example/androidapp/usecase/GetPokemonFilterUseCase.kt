package com.example.androidapp.usecase

import com.example.androidapp.repository.PokemonFilterRepository

class GetPokemonFilterUseCase(
    private val repository: PokemonFilterRepository
) {
    operator fun invoke() = repository.filter
}