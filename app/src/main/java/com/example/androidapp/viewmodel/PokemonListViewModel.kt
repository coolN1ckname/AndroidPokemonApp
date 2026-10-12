package com.example.androidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidapp.model.Pokemon
import com.example.androidapp.repository.PokemonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

class PokemonListViewModel : ViewModel() {

    private val repository = PokemonRepository()

    private val _uiState =
        MutableStateFlow<PokemonListUiState>(
            PokemonListUiState.Loading
        )

    val uiState: StateFlow<PokemonListUiState> =
        _uiState.asStateFlow()

    private var offset = 0

    private val pageSize = 100

    private var isLoading = false

    init {
        loadFirstPage()
    }

    private fun loadFirstPage() {

        if (isLoading) return

        isLoading = true

        viewModelScope.launch {

            try {

                val response =
                    repository.getPokemonPage(
                        limit = pageSize,
                        offset = 0
                    )

                offset = pageSize

                _uiState.value =
                    PokemonListUiState.Success(
                        pokemons = response.pokemons,
                        hasMore = response.hasMore
                    )

            } catch (e: Exception) {

                e.printStackTrace()

                _uiState.value =
                    PokemonListUiState.Error(
                        "Не удалось загрузить список Pokémon"
                    )
            }

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

        _uiState.value =
            currentState.copy(
                isLoadingMore = true,
                errorMessage = null
            )

        viewModelScope.launch {

            try {

                val response =
                    repository.getPokemonPage(
                        limit = pageSize,
                        offset = offset
                    )

                offset += pageSize

                _uiState.value =
                    currentState.copy(
                        pokemons =
                            currentState.pokemons +
                                    response.pokemons,
                        isLoadingMore = false,
                        hasMore = response.hasMore
                    )

            } catch (e: Exception) {

                e.printStackTrace()

                _uiState.value =
                    currentState.copy(
                        isLoadingMore = false,
                        errorMessage =
                            "Не удалось загрузить следующую страницу"
                    )
            }

            isLoading = false
        }
    }

    fun retry() {

        val currentState =
            _uiState.value as? PokemonListUiState.Success

        if (currentState != null) {

            loadNextPage()

        } else {

            loadFirstPage()
        }
    }
}