package com.example.androidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidapp.data.cache.FilterBadgeCache
import com.example.androidapp.model.PokemonFilter
import com.example.androidapp.usecase.GetPokemonFilterUseCase
import com.example.androidapp.usecase.ResetPokemonFilterUseCase
import com.example.androidapp.usecase.SavePokemonFilterUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FilterViewModel(
    private val getPokemonFilter: GetPokemonFilterUseCase,
    private val savePokemonFilter: SavePokemonFilterUseCase,
    private val resetPokemonFilter: ResetPokemonFilterUseCase,
    private val filterBadgeCache: FilterBadgeCache
) : ViewModel() {

    private val _filter = MutableStateFlow(PokemonFilter())
    val filter: StateFlow<PokemonFilter> = _filter.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val showFilterBadge: StateFlow<Boolean> = filterBadgeCache.showBadge

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        if (exception !is CancellationException) {
            _error.value = exception.message ?: "Произошла ошибка"
            _isSaving.value = false
        }
    }

    init {
        observeFilter()
    }

    private fun observeFilter() {
        viewModelScope.launch(exceptionHandler) {
            getPokemonFilter().collect { savedFilter ->
                _filter.value = savedFilter
                updateBadge(savedFilter)
            }
        }
    }

    private fun updateBadge(filter: PokemonFilter) {
        filterBadgeCache.update(
            hasCustomSettings = filter != PokemonFilter()
        )
    }

    fun updateName(name: String) {
        _filter.value = _filter.value.copy(name = name)
    }

    fun updateMinId(value: String) {
        val minId = value.toIntOrNull() ?: return

        if (minId in 1..1025) {
            _filter.value = _filter.value.copy(minId = minId)
        }
    }

    fun updateMaxId(value: String) {
        val maxId = value.toIntOrNull() ?: return

        if (maxId in 1..1025) {
            _filter.value = _filter.value.copy(maxId = maxId)
        }
    }

    fun saveFilter(onSaved: () -> Unit) {
        val currentFilter = _filter.value

        if (
            currentFilter.minId !in 1..1025 ||
            currentFilter.maxId !in 1..1025
        ) {
            _error.value = "ID должны быть от 1 до 1025"
            return
        }

        if (currentFilter.minId > currentFilter.maxId) {
            _error.value = "Минимальный ID не может быть больше максимального"
            return
        }

        viewModelScope.launch(exceptionHandler) {
            _isSaving.value = true
            _error.value = null

            try {
                savePokemonFilter(currentFilter)
                updateBadge(currentFilter)
                onSaved()
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun resetFilter() {
        viewModelScope.launch(exceptionHandler) {
            _error.value = null

            resetPokemonFilter()

            _filter.value = PokemonFilter()
            filterBadgeCache.update(false)
        }
    }
}