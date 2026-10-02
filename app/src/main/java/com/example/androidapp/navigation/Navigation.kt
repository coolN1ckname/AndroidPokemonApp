package com.example.androidapp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.androidapp.ui.DetailsScreen
import com.example.androidapp.ui.ListScreen
import com.example.androidapp.ui.ProfileScreen

@Composable
fun AppNavigation() {

    val backStack = rememberNavBackStack(
        AppRoute.PokemonList
    )

    val currentRoute = backStack.lastOrNull()

    val showBottomBar =
        currentRoute is AppRoute.PokemonList ||
                currentRoute is AppRoute.Profile

    Scaffold(
        bottomBar = {

            if (showBottomBar) {

                NavigationBar {

                    NavigationBarItem(
                        selected =
                            currentRoute is AppRoute.PokemonList,

                        onClick = {

                            if (
                                currentRoute !is AppRoute.PokemonList
                            ) {
                                backStack.clear()

                                backStack.add(
                                    AppRoute.PokemonList
                                )
                            }
                        },

                        icon = {
                            Icon(
                                imageVector =
                                    Icons.Default.List,
                                contentDescription =
                                    "Pokémon"
                            )
                        },

                        label = {
                            Text("Pokémon")
                        }
                    )

                    NavigationBarItem(
                        selected =
                            currentRoute is AppRoute.Profile,

                        onClick = {

                            if (
                                currentRoute !is AppRoute.Profile
                            ) {
                                backStack.clear()

                                backStack.add(
                                    AppRoute.Profile
                                )
                            }
                        },

                        icon = {
                            Icon(
                                imageVector =
                                    Icons.Default.AccountCircle,
                                contentDescription =
                                    "Профиль"
                            )
                        },

                        label = {
                            Text("Профиль")
                        }
                    )
                }
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            NavDisplay(
                backStack = backStack,

                onBack = {
                    if (backStack.size > 1) {
                        backStack.removeLastOrNull()
                    }
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

                    entry<AppRoute.Profile> {

                        ProfileScreen()
                    }
                }
            )
        }
    }
}