package com.example.androidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidapp.model.PokemonDetails
import com.example.androidapp.repository.PokemonRepository
import kotlinx.coroutines.CancellationException
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

class PokemonDetailsScreenViewModel : ViewModel() {

    private val repository = PokemonRepository()

    private val _uiState =
        MutableStateFlow<PokemonDetailsUiState>(
            PokemonDetailsUiState.Loading
        )

    val uiState: StateFlow<PokemonDetailsUiState> =
        _uiState.asStateFlow()

    fun loadPokemon(pokemonId: Int) {
        viewModelScope.launch {
            _uiState.value = PokemonDetailsUiState.Loading

            try {
                val pokemon = repository.getPokemonById(pokemonId)

                _uiState.value = PokemonDetailsUiState.Success(
                    pokemon = pokemon
                )

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                e.printStackTrace()

                _uiState.value = PokemonDetailsUiState.Error(
                    message = "Не удалось загрузить данные"
                )
            }
        }
    }
}
