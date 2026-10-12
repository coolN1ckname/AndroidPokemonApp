package com.example.androidapp.repository

import com.example.androidapp.model.PokemonDetails
import com.example.androidapp.repository.PokemonPage

interface PokemonDataRepository {
    suspend fun getPokemonPage(
        limit: Int,
        offset: Int
    ): PokemonPage


    suspend fun getPokemonById(
        id: Int
    ): PokemonDetails


}
