package com.example.androidapp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.FilterList
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
import com.example.androidapp.ui.FilterScreen
import com.example.androidapp.ui.ListScreen
import com.example.androidapp.ui.ProfileScreen
import androidx.compose.material.icons.filled.Favorite
import com.example.androidapp.ui.FavoritesScreen
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.androidapp.DI.AppContainer
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material.icons.filled.FilterList

@Composable
fun AppNavigation() {

    val backStack = rememberNavBackStack(
        AppRoute.PokemonList
    )

    val currentRoute = backStack.lastOrNull()

    val showBottomBar =
        currentRoute is AppRoute.PokemonList ||
                currentRoute is AppRoute.Profile ||
                currentRoute is AppRoute.PokemonFilters ||
                currentRoute is AppRoute.Favorites

    val showFilterBadge by AppContainer.filterBadgeCache.showBadge
        .collectAsState()

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {

                    // Вкладка Pokémon
                    NavigationBarItem(
                        selected = currentRoute is AppRoute.PokemonList,
                        onClick = {
                            if (currentRoute !is AppRoute.PokemonList) {
                                backStack.clear()
                                backStack.add(AppRoute.PokemonList)
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.List,
                                contentDescription = "Pokémon"
                            )
                        },
                        label = {
                            Text("Pokémon")
                        }
                    )

                    // Вкладка фильтров
                    NavigationBarItem(
                        selected = currentRoute is AppRoute.PokemonFilters,
                        onClick = {
                            if (currentRoute !is AppRoute.PokemonFilters) {
                                backStack.clear()
                                backStack.add(AppRoute.PokemonFilters)
                            }
                        },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (showFilterBadge) {
                                        Badge()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = "Фильтры"
                                )
                            }
                        },
                        label = {
                            Text("Фильтры")
                        }
                    )


                    NavigationBarItem(
                        selected = currentRoute is AppRoute.Favorites,
                        onClick = {
                            if (currentRoute !is AppRoute.Favorites) {
                                backStack.clear()
                                backStack.add(AppRoute.Favorites)
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Избранное"
                            )
                        },
                        label = {
                            Text("Избранное")
                        }
                    )

                    // Вкладка профиля
                    NavigationBarItem(
                        selected = currentRoute is AppRoute.Profile,
                        onClick = {
                            if (currentRoute !is AppRoute.Profile) {
                                backStack.clear()
                                backStack.add(AppRoute.Profile)
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Профиль"
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
                                    AppRoute.PokemonDetail(pokemonId)
                                )
                            }
                        )
                    }

                    entry<AppRoute.PokemonDetail> { route ->
                        DetailsScreen(
                            pokemonId = route.pokemonId,
                            onBack = {
                                backStack.removeLastOrNull()
                            }
                        )
                    }

                    entry<AppRoute.Profile> {
                        ProfileScreen()
                    }

                    entry<AppRoute.PokemonFilters> {
                        FilterScreen(
                            onDone = {
                                backStack.clear()
                                backStack.add(AppRoute.PokemonList)
                            },
                            onBack = {
                                backStack.clear()
                                backStack.add(AppRoute.PokemonList)
                            }
                        )
                    }

                    entry<AppRoute.Favorites> {
                        FavoritesScreen(
                            onPokemonClick = { pokemonId ->
                                backStack.add(
                                    AppRoute.PokemonDetail(pokemonId)
                                )
                            }
                        )
                    }

                }
            )
        }
    }
}