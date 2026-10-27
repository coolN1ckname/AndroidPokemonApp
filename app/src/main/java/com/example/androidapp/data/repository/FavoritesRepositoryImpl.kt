package com.example.androidapp.data.repository

import com.example.androidapp.data.local.FavoritePokemonDao
import com.example.androidapp.data.local.FavoritePokemonEntity
import com.example.androidapp.repository.FavoritesRepository
import com.example.androidapp.model.Pokemon
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val dao: FavoritePokemonDao
) : FavoritesRepository {

    override fun getFavorites(): Flow<List<Pokemon>> {
        return dao.getAllFavorites().map { entities ->
            entities.map { entity ->
                Pokemon(
                    id = entity.id,
                    name = entity.name,
                    imageUrl = entity.imageUrl
                )
            }
        }
    }

    override fun isFavorite(pokemonId: Int): Flow<Boolean> {
        return dao.isFavorite(pokemonId)
    }

    override suspend fun addFavorite(pokemon: Pokemon) {
        dao.addFavorite(
            FavoritePokemonEntity(
                id = pokemon.id,
                name = pokemon.name,
                imageUrl = pokemon.imageUrl
            )
        )
    }

    override suspend fun removeFavorite(pokemonId: Int) {
        dao.removeFavorite(pokemonId)
    }
}