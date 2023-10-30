package com.example.pokedex.repositories

import com.example.pokedex.dataclasses.remote.PokeApi
import com.example.pokedex.dataclasses.remote.responses.Pokemon
import com.example.pokedex.dataclasses.remote.responses.PokemonList
import com.example.pokedex.util.Resource
import dagger.hilt.android.scopes.ActivityScoped
import timber.log.Timber
import javax.inject.Inject

@ActivityScoped
class PokemonRepo @Inject constructor(
    private val pokeApi: PokeApi
) {

    suspend fun getPokemonList(limit : Int, offset : Int) : Resource<PokemonList> {

        val response = try {
            pokeApi.getPokemonList(limit, offset)
        } catch (e : Exception) {
            return Resource.Error("An error occurred.")
        }

        return Resource.Success(response)
    }

    suspend fun getPokemonInfo(name : String) : Resource<Pokemon> {

        val response = try {
            pokeApi.getPokemonInfo(name)
        } catch (e : Exception) {
            Timber.d(e)
            return Resource.Error("An error occurred.")
        }

        return Resource.Success(response)
    }

}