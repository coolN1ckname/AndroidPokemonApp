package com.example.androidapp.usecase

import com.example.androidapp.repository.PokemonDataRepository
import com.example.androidapp.repository.PokemonPage

class GetPokemonPageUseCase(
    private val repository: PokemonDataRepository
) {
    suspend operator fun invoke(
        limit: Int,
        offset: Int
    ): PokemonPage {
        return repository.getPokemonPage(limit, offset)
    }
}
