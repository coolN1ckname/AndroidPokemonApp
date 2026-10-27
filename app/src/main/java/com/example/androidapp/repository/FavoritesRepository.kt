package com.example.androidapp.repository

import com.example.androidapp.model.Pokemon
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {

    fun getFavorites(): Flow<List<Pokemon>>

    fun isFavorite(pokemonId: Int): Flow<Boolean>

    suspend fun addFavorite(pokemon: Pokemon)

    suspend fun removeFavorite(pokemonId: Int)
}