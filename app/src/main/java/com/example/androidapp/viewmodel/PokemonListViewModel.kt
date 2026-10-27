package com.example.androidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidapp.DI.AppContainer
import com.example.androidapp.model.Pokemon
import com.example.androidapp.model.PokemonFilter
import com.example.androidapp.usecase.GetPokemonPageUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed interface PokemonListUiState {

    data object Loading : PokemonListUiState

    data class Success(
        val pokemons: List<Pokemon>,
        val isLoadingMore: Boolean = false,
        val hasMore: Boolean = true,
        val errorMessage: String? = null
    ) : PokemonListUiState

    data class Error(
        val message: String
    ) : PokemonListUiState
}

class PokemonListViewModel(
    private val getPokemonPage: GetPokemonPageUseCase =
        AppContainer.getPokemonPageUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<PokemonListUiState>(
            PokemonListUiState.Loading
        )

    val uiState: StateFlow<PokemonListUiState> =
        _uiState.asStateFlow()

    private val pageSize = 100

    private var offset = 0
    private var isLoading = false
    private var currentFilter = PokemonFilter()
    private var loadJob: Job? = null

    init {
        observeFilters()
    }

    private fun observeFilters() {
        viewModelScope.launch {
            AppContainer.getPokemonFilterUseCase()
                .catch { exception ->
                    _uiState.value = PokemonListUiState.Error(
                        exception.message
                            ?: "Не удалось загрузить фильтры"
                    )
                }
                .collectLatest { filter ->
                    currentFilter = filter

                    loadJob?.cancel()

                    loadJob = launch {
                        loadForFilter(filter)
                    }
                }
        }
    }

    private suspend fun loadForFilter(
        filter: PokemonFilter
    ) {
        isLoading = true
        offset = 0

        _uiState.value = PokemonListUiState.Loading

        try {
            val minId = filter.minId.coerceAtLeast(1)
            val maxId = filter.maxId.coerceAtMost(1025)
            val nameQuery = filter.name.trim()

            if (minId > maxId) {
                _uiState.value = PokemonListUiState.Success(
                    pokemons = emptyList(),
                    hasMore = false
                )
                return
            }

            val hasActiveFilter =
                nameQuery.isNotBlank() ||
                        minId > 1 ||
                        maxId < 1025

            if (!hasActiveFilter) {
                val response = getPokemonPage(
                    limit = pageSize,
                    offset = 0
                )

                offset = response.pokemons.size

                _uiState.value = PokemonListUiState.Success(
                    pokemons = response.pokemons,
                    hasMore = response.hasMore
                )

                return
            }

            val matchingPokemons = mutableListOf<Pokemon>()

            var currentOffset = minId - 1
            val endOffset = maxId

            while (currentOffset < endOffset) {
                val limit = minOf(
                    pageSize,
                    endOffset - currentOffset
                )

                val response = getPokemonPage(
                    limit = limit,
                    offset = currentOffset
                )

                val filteredPage = response.pokemons.filter { pokemon ->
                    pokemon.id in minId..maxId &&
                            (
                                    nameQuery.isBlank() ||
                                            pokemon.name.contains(
                                                nameQuery,
                                                ignoreCase = true
                                            )
                                    )
                }

                matchingPokemons.addAll(filteredPage)

                if (response.pokemons.isEmpty()) {
                    break
                }

                currentOffset += response.pokemons.size

                if (!response.hasMore) {
                    break
                }
            }

            offset = currentOffset

            _uiState.value = PokemonListUiState.Success(
                pokemons = matchingPokemons,
                hasMore = false
            )

        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            _uiState.value = PokemonListUiState.Error(
                exception.message
                    ?: "Не удалось загрузить список Pokémon"
            )
        } finally {
            isLoading = false
        }
    }

    fun loadNextPage() {
        if (isLoading) return

        val currentState =
            _uiState.value as? PokemonListUiState.Success
                ?: return

        if (!currentState.hasMore) return

        isLoading = true

        _uiState.value = currentState.copy(
            isLoadingMore = true,
            errorMessage = null
        )

        viewModelScope.launch {
            try {
                val response = getPokemonPage(
                    limit = pageSize,
                    offset = offset
                )

                offset += response.pokemons.size

                val filteredPage = response.pokemons.filter { pokemon ->
                    val matchesName =
                        currentFilter.name.isBlank() ||
                                pokemon.name.contains(
                                    currentFilter.name.trim(),
                                    ignoreCase = true
                                )

                    val matchesId =
                        pokemon.id in
                                currentFilter.minId..currentFilter.maxId

                    matchesName && matchesId
                }

                _uiState.value = currentState.copy(
                    pokemons = currentState.pokemons + filteredPage,
                    isLoadingMore = false,
                    hasMore = response.hasMore
                )

            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value = currentState.copy(
                    isLoadingMore = false,
                    errorMessage = exception.message
                        ?: "Не удалось загрузить следующую страницу"
                )
            } finally {
                isLoading = false
            }
        }
    }

    fun retry() {
        loadJob?.cancel()

        loadJob = viewModelScope.launch {
            loadForFilter(currentFilter)
        }
    }
}