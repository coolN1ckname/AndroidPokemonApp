package com.example.androidapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.androidapp.ui.DetailsScreen
import com.example.androidapp.ui.ListScreen

@Composable
fun AppNavigation() {

    val backStack = rememberNavBackStack(
        AppRoute.PokemonList
    )

    NavDisplay(
        backStack = backStack,

        onBack = {
            backStack.removeLastOrNull()
        },

        entryProvider = entryProvider {

            entry<AppRoute.PokemonList> {

                ListScreen().MainListScreen(
                    onPokemonClick = { pokemonId ->

                        backStack.add(
                            AppRoute.PokemonDetail(
                                pokemonId
                            )
                        )
                    }
                )
            }

            entry<AppRoute.PokemonDetail> { route ->

                DetailsScreen().PokemonDetailScreen(
                    pokemonId = route.pokemonId,
                    onBackClick = {
                        backStack.removeLastOrNull()
                    }
                )
            }
        }
    )
}