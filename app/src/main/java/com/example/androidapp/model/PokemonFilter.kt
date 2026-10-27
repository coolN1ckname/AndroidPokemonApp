package com.example.androidapp.model

data class PokemonFilter(
    val name: String = "",
    val minId: Int = 1,
    val maxId: Int = 1025
)