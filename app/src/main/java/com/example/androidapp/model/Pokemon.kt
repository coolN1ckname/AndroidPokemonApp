package com.example.androidapp.model

data class Pokemon(
    val id: Int,
    val name: String,
    val imageUrl: String
)

data class PokemonDetails(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val height: Int,
    val weight: Int,
    val types: List<String>,
    val abilities: List<String>,
    val stats: List<PokemonStat>
)
data class PokemonStat(
    val name: String,
    val value: Int
)

