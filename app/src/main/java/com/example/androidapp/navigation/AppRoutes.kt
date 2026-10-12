package com.example.androidapp.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface AppRoute : NavKey {

    @Serializable
    data object PokemonList : AppRoute

    @Serializable
    data class PokemonDetail(
        val pokemonId: Int
    ) : AppRoute
    @Serializable
    data object Profile : AppRoute


}