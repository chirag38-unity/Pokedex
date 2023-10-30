package com.example.pokedex.viewmodels

import androidx.lifecycle.ViewModel
import com.example.pokedex.dataclasses.remote.responses.Pokemon
import com.example.pokedex.repositories.PokemonRepo
import com.example.pokedex.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val pokemonRepo: PokemonRepo
) : ViewModel() {

    suspend fun getPokemonInfo(pokemonName : String) : Resource<Pokemon> {
        return pokemonRepo.getPokemonInfo(pokemonName)
    }

}