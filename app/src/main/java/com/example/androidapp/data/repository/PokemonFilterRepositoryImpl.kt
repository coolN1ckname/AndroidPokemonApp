package com.example.androidapp.data.repository

import com.example.androidapp.data.preferences.PokemonFilterDataStore
import com.example.androidapp.model.PokemonFilter
import com.example.androidapp.repository.PokemonFilterRepository
import kotlinx.coroutines.flow.Flow

class PokemonFilterRepositoryImpl(
    private val dataStore: PokemonFilterDataStore
) : PokemonFilterRepository {

    override val filter: Flow<PokemonFilter>
        get() = dataStore.filter

    override suspend fun saveFilter(filter: PokemonFilter) {
        dataStore.saveFilter(filter)
    }

    override suspend fun resetFilter() {
        dataStore.resetFilter()
    }
}