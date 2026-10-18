package com.example.androidapp.DI


import com.example.androidapp.repository.PokemonDataRepository
import com.example.androidapp.usecase.GetPokemonDetailsUseCase
import com.example.androidapp.usecase.GetPokemonPageUseCase
import com.example.androidapp.repository.PokemonRepository

object AppContainer {
    private val repository: PokemonDataRepository = PokemonRepository()
    val getPokemonPageUseCase = GetPokemonPageUseCase(repository)
    val getPokemonDetailsUseCase = GetPokemonDetailsUseCase(repository)

}
