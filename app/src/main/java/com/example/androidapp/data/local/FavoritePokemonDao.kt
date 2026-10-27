package com.example.androidapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritePokemonDao {

    @Query("SELECT * FROM favorite_pokemon ORDER BY id ASC")
    fun getAllFavorites(): Flow<List<FavoritePokemonEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_pokemon WHERE id = :pokemonId)")
    fun isFavorite(pokemonId: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(pokemon: FavoritePokemonEntity)

    @Query("DELETE FROM favorite_pokemon WHERE id = :pokemonId")
    suspend fun removeFavorite(pokemonId: Int)

    @Query("DELETE FROM favorite_pokemon")
    suspend fun clearFavorites()
}