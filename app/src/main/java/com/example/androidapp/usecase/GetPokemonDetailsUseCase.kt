package com.example.androidapp.usecase

import com.example.androidapp.repository.PokemonDataRepository
import com.example.androidapp.model.PokemonDetails

class GetPokemonDetailsUseCase(
    private val repository: PokemonDataRepository
) {
    suspend operator fun invoke(id: Int): PokemonDetails {
        return repository.getPokemonById(id)
    }
}
