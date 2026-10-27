package com.example.androidapp.data.cache

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FilterBadgeCache {

    private val _showBadge = MutableStateFlow(false)

    val showBadge: StateFlow<Boolean> = _showBadge.asStateFlow()

    fun update(hasCustomSettings: Boolean) {
        _showBadge.value = hasCustomSettings
    }
}