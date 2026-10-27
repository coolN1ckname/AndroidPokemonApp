
package com.example.androidapp.DI

import android.content.Context
import com.example.androidapp.data.local.PokemonDatabase
import com.example.androidapp.data.preferences.PokemonFilterDataStore
import com.example.androidapp.data.repository.FavoritesRepositoryImpl
import com.example.androidapp.data.repository.PokemonFilterRepositoryImpl
import com.example.androidapp.repository.FavoritesRepository
import com.example.androidapp.repository.PokemonFilterRepository
import com.example.androidapp.repository.PokemonDataRepository
import com.example.androidapp.repository.PokemonRepository
import com.example.androidapp.usecase.GetPokemonDetailsUseCase
import com.example.androidapp.usecase.GetPokemonPageUseCase
import com.example.androidapp.usecase.GetPokemonFilterUseCase
import com.example.androidapp.usecase.ResetPokemonFilterUseCase
import com.example.androidapp.usecase.SavePokemonFilterUseCase
import com.example.androidapp.usecase.GetFavoritePokemonUseCase
import com.example.androidapp.usecase.IsFavoritePokemonUseCase
import com.example.androidapp.usecase.AddFavoritePokemonUseCase
import com.example.androidapp.usecase.RemoveFavoritePokemonUseCase
import com.example.androidapp.data.cache.FilterBadgeCache

object AppContainer {

    val filterBadgeCache = FilterBadgeCache()

    private val pokemonRepository: PokemonDataRepository =
        PokemonRepository()

    val getPokemonPageUseCase =
        GetPokemonPageUseCase(pokemonRepository)

    val getPokemonDetailsUseCase =
        GetPokemonDetailsUseCase(pokemonRepository)

    private lateinit var filterRepository: PokemonFilterRepository

    lateinit var getPokemonFilterUseCase: GetPokemonFilterUseCase
        private set

    lateinit var savePokemonFilterUseCase: SavePokemonFilterUseCase
        private set

    lateinit var resetPokemonFilterUseCase: ResetPokemonFilterUseCase
        private set

    private lateinit var favoritesRepository: FavoritesRepository

    lateinit var getFavoritePokemonUseCase: GetFavoritePokemonUseCase
        private set

    lateinit var isFavoritePokemonUseCase: IsFavoritePokemonUseCase
        private set

    lateinit var addFavoritePokemonUseCase: AddFavoritePokemonUseCase
        private set

    lateinit var removeFavoritePokemonUseCase: RemoveFavoritePokemonUseCase
        private set

    fun initialize(context: Context) {
        // Фильтры
        val dataStore = PokemonFilterDataStore(
            context.applicationContext
        )

        filterRepository = PokemonFilterRepositoryImpl(dataStore)

        getPokemonFilterUseCase =
            GetPokemonFilterUseCase(filterRepository)

        savePokemonFilterUseCase =
            SavePokemonFilterUseCase(filterRepository)

        resetPokemonFilterUseCase =
            ResetPokemonFilterUseCase(filterRepository)

        // Избранное
        val database = PokemonDatabase.getDatabase(
            context.applicationContext
        )

        favoritesRepository = FavoritesRepositoryImpl(
            database.favoritePokemonDao()
        )

        getFavoritePokemonUseCase =
            GetFavoritePokemonUseCase(favoritesRepository)

        isFavoritePokemonUseCase =
            IsFavoritePokemonUseCase(favoritesRepository)

        addFavoritePokemonUseCase =
            AddFavoritePokemonUseCase(favoritesRepository)

        removeFavoritePokemonUseCase =
            RemoveFavoritePokemonUseCase(favoritesRepository)
    }
}
