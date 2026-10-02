package com.example.androidapp.api

import com.example.androidapp.model.PokemonDetailsResponse
import com.example.androidapp.model.PokemonListResponse
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface PokeApi {

    @GET("pokemon")
    suspend fun getPokemon(
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): PokemonListResponse

    @GET("pokemon/{id}")
    suspend fun getPokemonById(
        @Path("id") id: Int
    ): PokemonDetailsResponse

    companion object {

        private const val BASE_URL = "https://pokeapi.co/api/v2/"

        private val client =
            OkHttpClient.Builder()
                .connectTimeout(
                    10,
                    TimeUnit.SECONDS
                )
                .readTimeout(
                    10,
                    TimeUnit.SECONDS
                )
                .writeTimeout(
                    10,
                    TimeUnit.SECONDS
                )
                .build()

        val api: PokeApi by lazy {

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(
                    GsonConverterFactory.create()
                )
                .build()
                .create(PokeApi::class.java)
        }
    }
}