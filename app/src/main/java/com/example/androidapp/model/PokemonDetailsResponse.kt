package com.example.androidapp.model

data class PokemonDetailsResponse(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val sprites: Sprites,
    val types: List<PokemonTypeResponse>,
    val abilities: List<PokemonAbilityResponse>,
    val stats: List<PokemonStatResponse>
)

data class Sprites(
    val front_default: String?
)

data class PokemonTypeResponse(
    val type: PokemonTypeInfo
)

data class PokemonTypeInfo(
    val name: String
)

data class PokemonAbilityResponse(
    val ability: PokemonAbilityInfo
)

data class PokemonAbilityInfo(
    val name: String
)

data class PokemonStatResponse(
    val base_stat: Int,
    val stat: PokemonStatInfo
)

data class PokemonStatInfo(
    val name: String
)
