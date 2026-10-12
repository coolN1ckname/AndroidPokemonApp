package com.example.androidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidapp.DI.AppContainer
import com.example.androidapp.usecase.GetPokemonDetailsUseCase
import com.example.androidapp.model.PokemonDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface PokemonDetailsUiState {
    data object Loading : PokemonDetailsUiState

    data class Success(
        val pokemon: PokemonDetails
    ) : PokemonDetailsUiState

    data class Error(
        val message: String
    ) : PokemonDetailsUiState

}

class PokemonDetailsViewModel(
    private val pokemonId: Int,
    private val getPokemonDetails: GetPokemonDetailsUseCase =
        AppContainer.getPokemonDetailsUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<PokemonDetailsUiState>(
            PokemonDetailsUiState.Loading
        )

    val uiState: StateFlow<PokemonDetailsUiState> =
        _uiState.asStateFlow()

    init {
        loadPokemon()
    }

    fun loadPokemon() {
        viewModelScope.launch {
            _uiState.value = PokemonDetailsUiState.Loading

            try {
                val pokemon = getPokemonDetails(pokemonId)

                _uiState.value =
                    PokemonDetailsUiState.Success(pokemon)
            } catch (e: Exception) {
                e.printStackTrace()

                _uiState.value =
                    PokemonDetailsUiState.Error(
                        "Не удалось загрузить покемона"
                    )
            }
        }
    }

    companion object {
        fun factory(
            pokemonId: Int
        ): androidx.lifecycle.ViewModelProvider.Factory =
            object : androidx.lifecycle.ViewModelProvider.Factory {

                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>
                ): T {
                    if (
                        modelClass.isAssignableFrom(
                            PokemonDetailsViewModel::class.java
                        )
                    ) {
                        return PokemonDetailsViewModel(
                            pokemonId = pokemonId
                        ) as T
                    }

                    throw IllegalArgumentException(
                        "Unknown ViewModel class"
                    )
                }
            }
    }

}
