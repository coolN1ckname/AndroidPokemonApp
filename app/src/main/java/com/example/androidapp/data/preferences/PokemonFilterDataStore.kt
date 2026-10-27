package com.example.androidapp.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.androidapp.model.PokemonFilter
import com.example.androidapp.repository.PokemonFilterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.filterDataStore by preferencesDataStore(
    name = "pokemon_filter_preferences"
)

class PokemonFilterDataStore(
    private val context: Context
) : PokemonFilterRepository {

    private object Keys {
        val NAME = stringPreferencesKey("pokemon_name")
        val MIN_ID = intPreferencesKey("pokemon_min_id")
        val MAX_ID = intPreferencesKey("pokemon_max_id")
    }

    override val filter: Flow<PokemonFilter> =
        context.filterDataStore.data.map { preferences ->
            PokemonFilter(
                name = preferences[Keys.NAME] ?: "",
                minId = preferences[Keys.MIN_ID] ?: 1,
                maxId = preferences[Keys.MAX_ID] ?: 1025
            )
        }

    override suspend fun saveFilter(filter: PokemonFilter) {
        context.filterDataStore.edit { preferences ->
            preferences[Keys.NAME] = filter.name
            preferences[Keys.MIN_ID] = filter.minId
            preferences[Keys.MAX_ID] = filter.maxId
        }
    }

    override suspend fun resetFilter() {
        context.filterDataStore.edit { preferences ->
            preferences[Keys.NAME] = ""
            preferences[Keys.MIN_ID] = 1
            preferences[Keys.MAX_ID] = 1025
        }
    }
}