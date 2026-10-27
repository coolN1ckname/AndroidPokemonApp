package com.example.androidapp.repository

import com.example.androidapp.model.PokemonFilter
import kotlinx.coroutines.flow.Flow

interface PokemonFilterRepository {

    val filter: Flow<PokemonFilter>

    suspend fun saveFilter(filter: PokemonFilter)

    suspend fun resetFilter()
}