package com.example.androidapp.repository

import com.example.androidapp.api.PokeApi
import com.example.androidapp.model.Pokemon
import com.example.androidapp.model.PokemonDetails
import com.example.androidapp.model.PokemonStat
import com.example.androidapp.repository.PokemonDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class PokemonPage(
    val pokemons: List<Pokemon>,
    val hasMore: Boolean
)

class PokemonRepository : PokemonDataRepository {

    private val api = PokeApi.api

    suspend override fun getPokemonPage(
        limit: Int,
        offset: Int
    ): PokemonPage {

        val response = api.getPokemon(
            limit = limit,
            offset = offset
        )

        val pokemons =
            response.results.map { item ->

                val id = item.url
                    .trimEnd('/')
                    .substringAfterLast('/')
                    .toInt()

                Pokemon(
                    id = id,
                    name = item.name,
                    imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"
                )
            }

        return PokemonPage(
            pokemons = pokemons,
            hasMore = response.next != null
        )
    }

    suspend override fun getPokemonById(
        id: Int
    ): PokemonDetails {

        val response =
            api.getPokemonById(id)

        return PokemonDetails(
            id = response.id,
            name = response.name,
            imageUrl =
                response.sprites.front_default ?: "",
            height = response.height,
            weight = response.weight,
            types = response.types.map {
                it.type.name
            },
            abilities = response.abilities.map {
                it.ability.name
            },
            stats = response.stats.map {
                PokemonStat(
                    name = it.stat.name,
                    value = it.base_stat
                )
            }
        )
    }

}